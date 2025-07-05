package com.example.bihinsight.ui.screens.chart

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.bihinsight.data.local.PersonsByRecordDateEntity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonsByRecordDateChartScreen(
    persons: List<PersonsByRecordDateEntity>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { 
                Text(
                    text = "Vizualizacija podataka",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Nazad"
                    )
                }
            }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Statistika registriranih osoba",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // Bar Chart - Godine po ukupnom broju
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ukupno registriranih osoba po godinama",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        AndroidView(
                            factory = { context ->
                                BarChart(context).apply {
                                    description.isEnabled = false
                                    legend.isEnabled = true
                                    setDrawGridBackground(false)
                                    setDrawBarShadow(false)
                                    setDrawValueAboveBar(true)
                                    
                                    xAxis.apply {
                                        position = XAxis.XAxisPosition.BOTTOM
                                        setDrawGridLines(false)
                                        granularity = 1f
                                    }
                                    
                                    axisLeft.apply {
                                        setDrawGridLines(true)
                                        axisMinimum = 0f
                                    }
                                    
                                    axisRight.isEnabled = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            update = { chart ->
                                val yearData = persons.groupBy { it.year }
                                    .filter { it.key != null }
                                    .mapValues { it.value.sumOf { person -> person.total ?: 0 } }
                                    .toList()
                                    .sortedBy { it.first }
                                
                                val entries = yearData.mapIndexed { index, (year, total) ->
                                    BarEntry(index.toFloat(), total.toFloat())
                                }
                                
                                val dataSet = BarDataSet(entries, "Ukupno registriranih").apply {
                                    colors = ColorTemplate.MATERIAL_COLORS.toList()
                                    valueTextColor = Color.BLACK
                                    valueTextSize = 12f
                                }
                                
                                val barData = BarData(dataSet)
                                chart.data = barData
                                
                                val xAxisLabels = yearData.map { it.first.toString() }
                                chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
                                
                                chart.invalidate()
                            }
                        )
                    }
                }
            }
            
            // Pie Chart - Distribucija po entitetima
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Distribucija po entitetima",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        AndroidView(
                            factory = { context ->
                                PieChart(context).apply {
                                    description.isEnabled = false
                                    legend.isEnabled = true
                                    setUsePercentValues(true)
                                    setDrawEntryLabels(true)
                                    setEntryLabelColor(Color.BLACK)
                                    setEntryLabelTextSize(12f)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            update = { chart ->
                                val entityData = persons.groupBy { it.entity }
                                    .mapValues { it.value.sumOf { person -> person.total ?: 0 } }
                                    .filter { it.value > 0 }
                                
                                val entries = entityData.map { (entity, total) ->
                                    PieEntry(total.toFloat(), entity ?: "Nepoznato")
                                }
                                
                                val dataSet = PieDataSet(entries, "Entiteti").apply {
                                    colors = ColorTemplate.MATERIAL_COLORS.toList()
                                    valueTextSize = 14f
                                    valueTextColor = Color.WHITE
                                }
                                
                                val pieData = PieData(dataSet).apply {
                                    setValueFormatter(PercentFormatter(chart))
                                }
                                
                                chart.data = pieData
                                chart.invalidate()
                            }
                        )
                    }
                }
            }
            
            // Statistika
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ukupna statistika",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        val totalRegistered = persons.sumOf { it.total ?: 0 }
                        val totalWithResidence = persons.sumOf { it.withResidenceTotal ?: 0 }
                        val uniqueMunicipalities = persons.mapNotNull { it.municipality }.distinct().size
                        val uniqueYears = persons.mapNotNull { it.year }.distinct().size
                        
                        StatisticRow("Ukupno registriranih:", totalRegistered.toString())
                        StatisticRow("Sa prebivalištem:", totalWithResidence.toString())
                        StatisticRow("Broj općina:", uniqueMunicipalities.toString())
                        StatisticRow("Broj godina:", uniqueYears.toString())
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
} 