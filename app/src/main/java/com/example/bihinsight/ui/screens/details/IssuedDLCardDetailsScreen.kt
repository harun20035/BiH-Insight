package com.example.bihinsight.ui.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bihinsight.data.local.IssuedDLCardEntity
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssuedDLCardDetailsScreen(card: IssuedDLCardEntity, onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBar(
                title = { Text("Detalji") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Nazad")
                    }
                }
            )
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Detalji o izdatim vozačkim dozvolama",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(text = "Općina: ${card.municipality ?: "Nepoznato"}")
                Text(text = "Kanton: ${card.canton ?: "Nepoznato"}")
                Text(text = "Entitet: ${card.entity ?: "Nepoznato"}")
                Text(text = "Institucija: ${card.institution ?: "Nepoznato"}")
                Text(text = "Godina: ${card.year ?: "-"}")
                Text(text = "Mjesec: ${card.month ?: "-"}")
                Text(text = "Datum ažuriranja: ${card.dateUpdate ?: "-"}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Prvi put izdano (muškarci): ${card.issuedFirstTimeMaleTotal ?: "-"}")
                Text(text = "Zamijenjeno (muškarci): ${card.replacedMaleTotal ?: "-"}")
                Text(text = "Prvi put izdano (žene): ${card.issuedFirstTimeFemaleTotal ?: "-"}")
                Text(text = "Zamijenjeno (žene): ${card.replacedFemaleTotal ?: "-"}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Ukupno: ${card.total ?: "-"}", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
} 