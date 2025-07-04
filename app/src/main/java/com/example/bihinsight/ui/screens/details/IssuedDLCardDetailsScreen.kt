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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssuedDLCardDetailsScreen(
    card: IssuedDLCardEntity,
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
                    IconButton(onClick = { onToggleFavorite(!card.isFavorite) }) {
                        Icon(
                            imageVector = if (card.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = if (card.isFavorite) "Ukloni iz favorita" else "Dodaj u favorite",
                            tint = if (card.isFavorite) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = {
                        val shareText = "Općina: ${card.municipality ?: "Nepoznato"}\n" +
                            "Godina: ${card.year ?: "-"}\n" +
                            "Ukupno: ${card.total ?: "-"}\n" +
                            "Više u aplikaciji BiH Insight!"
                        onShare(shareText)
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Podijeli")
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