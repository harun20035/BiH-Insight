package com.example.bihinsight.ui.screens.issueddlcards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bihinsight.data.local.IssuedDLCardEntity

@Composable
fun IssuedDLCardScreen(viewModel: IssuedDLCardViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is IssuedDLCardUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is IssuedDLCardUiState.Error -> {
            val message = (uiState as IssuedDLCardUiState.Error).message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Greška: $message")
            }
        }
        is IssuedDLCardUiState.Success -> {
            val cards = (uiState as IssuedDLCardUiState.Success).cards
            IssuedDLCardList(cards)
        }
    }
}

@Composable
fun IssuedDLCardList(cards: List<IssuedDLCardEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cards) { card ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Općina: ${card.municipality ?: "Nepoznato"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Godina: ${card.year ?: "-"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Ukupno: ${card.total ?: "-"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    // Dodaj još informacija po želji
                }
            }
        }
    }
} 