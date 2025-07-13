package com.example.bihinsight.ui.screens.chart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bihinsight.data.local.NewbornByRequestDateEntity
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewbornByRequestDateChartScreen(
    newborns: List<NewbornByRequestDateEntity>,
    onBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBar(
                title = { 
                    Text(
                        text = "Vizualizacija",
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
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Nazad")
                    }
                }
            )
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Broj novorođenih po godinama (Top 10 općina)",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                BarChart(newborns)
            }
        }
    }
}

@Composable
fun BarChart(newborns: List<NewbornByRequestDateEntity>) {
    val top10 = newborns.sortedByDescending { it.total ?: 0 }.take(10)
    val maxTotal = top10.maxOfOrNull { it.total ?: 0 } ?: 1
    val maxBarWidth = 160.dp
    Column(modifier = Modifier.fillMaxWidth()) {
        top10.forEach { newborn ->
            val percent = (newborn.total?.toFloat() ?: 0f) / maxTotal
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = newborn.municipality ?: "Nepoznato",
                    modifier = Modifier.width(120.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width((maxBarWidth * percent).coerceAtLeast(8.dp))
                        .padding(end = 8.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                )
                Text(
                    text = newborn.total?.toString() ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .widthIn(min = 32.dp)
                )
            }
        }
    }
} 