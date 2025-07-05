package com.example.bihinsight.ui.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bihinsight.data.local.PersonsByRecordDateEntity
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonsByRecordDateDetailsScreen(
    person: PersonsByRecordDateEntity,
    onBack: () -> Unit,
    onToggleFavorite: (Boolean) -> Unit = {},
    onShare: (String) -> Unit = {}
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBar(
                title = { 
                    Text(
                        text = "Detalji",
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
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Nazad")
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleFavorite(!person.isFavorite) }) {
                        Icon(
                            imageVector = if (person.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = if (person.isFavorite) "Ukloni iz favorita" else "Dodaj u favorite",
                            tint = if (person.isFavorite) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = {
                        val shareText = "Općina: ${person.municipality ?: "Nepoznato"}\n" +
                            "Godina: ${person.year ?: "-"}\n" +
                            "Ukupno: ${person.total ?: "-"}\n" +
                            "Sa prebivalištem: ${person.withResidenceTotal ?: "-"}\n" +
                            "Više u aplikaciji BiH Insight!"
                        onShare(shareText)
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Podijeli")
                    }
                }
            )
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Detalji o registriranim osobama",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(text = "Općina: ${person.municipality ?: "Nepoznato"}")
                Text(text = "Kanton: ${person.canton ?: "Nepoznato"}")
                Text(text = "Entitet: ${person.entity ?: "Nepoznato"}")
                Text(text = "Institucija: ${person.institution ?: "Nepoznato"}")
                Text(text = "Godina: ${person.year ?: "-"}")
                Text(text = "Mjesec: ${person.month ?: "-"}")
                Text(text = "Datum ažuriranja: ${person.dateUpdate ?: "-"}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Sa prebivalištem: ${person.withResidenceTotal ?: "-"}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Ukupno: ${person.total ?: "-"}", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
} 