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
import com.example.bihinsight.data.local.PersonsByRecordDateEntity
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonsByRecordDateDetailsScreen(
    person: PersonsByRecordDateEntity,
    onBack: () -> Unit,
    onToggleFavorite: (Boolean) -> Unit,
    onShare: (String) -> Unit
) {
    val context = LocalContext.current
    
    // Copy to clipboard funkcija
    fun copyToClipboard() {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(
            "Podaci o registriranim osobama",
            """
            Općina: ${person.municipality ?: "Nepoznato"}
            Kanton: ${person.canton ?: "Nepoznato"}
            Entitet: ${person.entity ?: "Nepoznato"}
            Institucija: ${person.institution ?: "Nepoznato"}
            Godina: ${person.year ?: "-"}
            Mjesec: ${person.month ?: "-"}
            Datum ažuriranja: ${person.dateUpdate ?: "-"}
            
            Sa prebivalištem: ${person.withResidenceTotal ?: "-"}
            
            Ukupno: ${person.total ?: "-"}
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
                        text = "Detalji registriranih osoba",
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
                    IconButton(onClick = { onToggleFavorite(!person.isFavorite) }) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = if (person.isFavorite) "Ukloni iz favorita" else "Dodaj u favorite",
                            tint = if (person.isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    // Share dugme
                    IconButton(onClick = { 
                        val shareText = """
                            Podaci o registriranim osobama:
                            Općina: ${person.municipality ?: "Nepoznato"}
                            Godina: ${person.year ?: "-"}
                            Ukupno: ${person.total ?: "-"}
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
                    text = "Detalji o registriranim osobama",
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
                        DetailRow("Općina", person.municipality ?: "Nepoznato")
                        DetailRow("Kanton", person.canton ?: "Nepoznato")
                        DetailRow("Entitet", person.entity ?: "Nepoznato")
                        DetailRow("Institucija", person.institution ?: "Nepoznato")
                        DetailRow("Godina", person.year?.toString() ?: "-")
                        DetailRow("Mjesec", person.month?.toString() ?: "-")
                        DetailRow("Datum ažuriranja", person.dateUpdate ?: "-")
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        DetailRow("Sa prebivalištem", person.withResidenceTotal?.toString() ?: "-")
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        DetailRow(
                            "Ukupno", 
                            person.total?.toString() ?: "-",
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