package com.example.bihinsight.ui.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.text.font.FontWeight
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.example.bihinsight.data.local.NewbornByRequestDateEntity
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewbornByRequestDateDetailsScreen(
    newborn: NewbornByRequestDateEntity,
    onBack: () -> Unit,
    onToggleFavorite: (Boolean) -> Unit,
    onShare: (String) -> Unit
) {
    val context = LocalContext.current
    
    // Copy to clipboard funkcija
    fun copyToClipboard() {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(
            "Podaci o novorođenim osobama",
            """
            Općina: ${newborn.municipality ?: "Nepoznato"}
            Kanton: ${newborn.canton ?: "Nepoznato"}
            Entitet: ${newborn.entity ?: "Nepoznato"}
            Institucija: ${newborn.institution ?: "Nepoznato"}
            Godina: ${newborn.year ?: "-"}
            Mjesec: ${newborn.month ?: "-"}
            Datum ažuriranja: ${newborn.dateUpdate ?: "-"}
            
            Muški: ${newborn.maleTotal ?: "-"}
            Ženski: ${newborn.femaleTotal ?: "-"}
            
            Ukupno: ${newborn.total ?: "-"}
            """.trimIndent()
        )
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "Podaci kopirani u clipboard!", Toast.LENGTH_SHORT).show()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBar(
                title = { 
                    Text(
                        text = "Detalji novorođenih osoba",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack, 
                            contentDescription = "Nazad"
                        )
                    }
                },
                actions = {
                    // Copy to clipboard dugme
                    IconButton(onClick = { copyToClipboard() }) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = "Kopiraj u clipboard"
                        )
                    }
                    
                    // Favorite dugme
                    IconButton(onClick = { onToggleFavorite(!newborn.isFavorite) }) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = if (newborn.isFavorite) "Ukloni iz favorita" else "Dodaj u favorite",
                            tint = if (newborn.isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    // Share dugme
                    IconButton(onClick = { 
                        val shareText = """
                            Podaci o novorođenim osobama:
                            Općina: ${newborn.municipality ?: "Nepoznato"}
                            Godina: ${newborn.year ?: "-"}
                            Ukupno: ${newborn.total ?: "-"}
                        """.trimIndent()
                        onShare(shareText)
                    }) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = "Podijeli"
                        )
                    }
                }
            )
            
            Column(modifier = Modifier.padding(24.dp)) {
                // Success message za copy
                var showCopySuccess by remember { mutableStateOf(false) }
                
                if (showCopySuccess) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Podaci uspješno kopirani u clipboard!",
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    LaunchedEffect(Unit) {
                        delay(3000)
                        showCopySuccess = false
                    }
                }
                
                Text(
                    text = "Detalji o novorođenim osobama",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Podaci u card formatu
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        DetailRow("Općina", newborn.municipality ?: "Nepoznato")
                        DetailRow("Kanton", newborn.canton ?: "Nepoznato")
                        DetailRow("Entitet", newborn.entity ?: "Nepoznato")
                        DetailRow("Institucija", newborn.institution ?: "Nepoznato")
                        DetailRow("Godina", newborn.year?.toString() ?: "-")
                        DetailRow("Mjesec", newborn.month?.toString() ?: "-")
                        DetailRow("Datum ažuriranja", newborn.dateUpdate ?: "-")
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        DetailRow("Muški", newborn.maleTotal?.toString() ?: "-")
                        DetailRow("Ženski", newborn.femaleTotal?.toString() ?: "-")
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        DetailRow(
                            "Ukupno", 
                            newborn.total?.toString() ?: "-",
                            isTotal = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
} 