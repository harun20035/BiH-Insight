package com.example.bihinsight.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DatasetSelectionScreen(
    selectedDataset: String,
    onDatasetSelected: (String) -> Unit,
    onConfirm: () -> Unit
) {
    val datasets = listOf("Izdate vozačke dozvole", "Registrovane osobe", "Novorođene osobe")
    var currentSelection by remember { mutableStateOf(selectedDataset) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Izbor skupa podataka",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            datasets.forEach { dataset ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = currentSelection == dataset,
                        onClick = { currentSelection = dataset }
                    )
                    Text(text = dataset)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                onDatasetSelected(currentSelection)
                onConfirm()
            }) {
                Text("Potvrdi")
            }
        }
    }
} 