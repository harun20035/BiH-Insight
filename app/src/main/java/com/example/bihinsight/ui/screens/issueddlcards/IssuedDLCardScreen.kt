package com.example.bihinsight.ui.screens.issueddlcards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bihinsight.data.local.IssuedDLCardEntity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssuedDLCardScreen(
    viewModel: IssuedDLCardViewModel,
    onCardClick: (Int) -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onDatasetClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val filterText by viewModel.filterText.collectAsState()
    val yearFilter by viewModel.yearFilter.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val sortOptions = listOf(
        IssuedDLCardViewModel.SortOption.MUNICIPALITY to "Općina (A-Z)",
        IssuedDLCardViewModel.SortOption.YEAR_DESC to "Godina (najnovije)",
        IssuedDLCardViewModel.SortOption.TOTAL_DESC to "Ukupno izdatih (najviše)"
    )
    val expanded = remember { mutableStateOf(false) }
    val sortExpanded = remember { mutableStateOf(false) }

    // Prikupi sve godine iz trenutnog state-a (za dropdown)
    val allYears = (uiState as? IssuedDLCardUiState.Success)?.cards?.mapNotNull { it.year }?.distinct()?.sorted() ?: emptyList()

    val isRefreshing = uiState is IssuedDLCardUiState.Loading
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { viewModel.fetchIssuedDL() }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Izdate vozačke dozvole") },
                actions = {
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Filled.Star, contentDescription = "Favoriti")
                    }
                    IconButton(onClick = onDatasetClick) {
                        Icon(Icons.Filled.Settings, contentDescription = "Izbor skupa podataka")
                    }
                }
            )
            ExposedDropdownMenuBox(
                expanded = sortExpanded.value,
                onExpandedChange = { sortExpanded.value = !sortExpanded.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = sortOptions.first { it.first == sortOption }.second,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sortiraj po") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded.value) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors()
                )
                DropdownMenu(
                    expanded = sortExpanded.value,
                    onDismissRequest = { sortExpanded.value = false }
                ) {
                    sortOptions.forEach { (option, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.setSortOption(option)
                                sortExpanded.value = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = filterText,
                onValueChange = { viewModel.setFilterText(it) },
                label = { Text("Pretraži po općini") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded.value,
                onExpandedChange = { expanded.value = !expanded.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = yearFilter?.toString() ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Godina") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors()
                )
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sve godine") },
                        onClick = {
                            viewModel.setYearFilter(null)
                            expanded.value = false
                        }
                    )
                    allYears.forEach { year ->
                        DropdownMenuItem(
                            text = { Text(year.toString()) },
                            onClick = {
                                viewModel.setYearFilter(year)
                                expanded.value = false
                            }
                        )
                    }
                }
            }
            Box(modifier = Modifier.weight(1f)) {
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
                        IssuedDLCardList(cards, onCardClick)
                    }
                }
            }
        }
    }
}

@Composable
fun IssuedDLCardList(cards: List<IssuedDLCardEntity>, onCardClick: (Int) -> Unit) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCardClick(card.id) }
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