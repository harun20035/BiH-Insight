package com.example.bihinsight.ui.screens.newborns

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
import androidx.compose.ui.unit.size
import com.example.bihinsight.data.local.NewbornByRequestDateEntity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Info
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewbornByRequestDateScreen(
    viewModel: NewbornByRequestDateViewModel,
    onNewbornClick: (Int) -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onDatasetClick: () -> Unit = {},
    onChartClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val filterText by viewModel.filterText.collectAsState()
    val yearFilter by viewModel.yearFilter.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val sortOptions = listOf(
        NewbornByRequestDateViewModel.SortOption.MUNICIPALITY to "Općina (A-Z)",
        NewbornByRequestDateViewModel.SortOption.YEAR_DESC to "Godina (najnovije)",
        NewbornByRequestDateViewModel.SortOption.TOTAL_DESC to "Ukupno rođenih (najviše)"
    )
    val expanded = remember { mutableStateOf(false) }
    val sortExpanded = remember { mutableStateOf(false) }
    val filtersVisible = remember { mutableStateOf(false) }

    val allYears = (uiState as? NewbornByRequestDateUiState.Success)?.newborns?.mapNotNull { it.year }?.distinct()?.sorted() ?: emptyList()
    val allEntities = (uiState as? NewbornByRequestDateUiState.Success)?.newborns?.mapNotNull { it.entity }?.distinct()?.sorted() ?: emptyList()
    val selectedEntity = remember { mutableStateOf<String?>(null) }

    val isRefreshing = uiState is NewbornByRequestDateUiState.Loading
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { viewModel.fetchNewborns() }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = "BiH Insight",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Novorođene osobe",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { filtersVisible.value = !filtersVisible.value }) {
                        Icon(
                            if (filtersVisible.value) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (filtersVisible.value) "Sakrij filtere" else "Prikaži filtere"
                        )
                    }
                    IconButton(onClick = onChartClick) {
                        Icon(Icons.Filled.Info, contentDescription = "Vizualizacija")
                    }
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Filled.Star, contentDescription = "Favoriti")
                    }
                    IconButton(onClick = onDatasetClick) {
                        Icon(Icons.Filled.Settings, contentDescription = "Izbor skupa podataka")
                    }
                }
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aktivni dataset:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Novorođene osobe",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            AnimatedVisibility(
                visible = filtersVisible.value,
                enter = slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { -it }
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    animationSpec = tween(300),
                    targetOffsetY = { -it }
                ) + fadeOut(animationSpec = tween(300))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (allEntities.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = selectedEntity.value == null,
                                    onClick = { 
                                        selectedEntity.value = null
                                        viewModel.setEntityFilter(null)
                                    },
                                    label = { Text("Svi entiteti") }
                                )
                            }
                            items(allEntities) { entity ->
                                FilterChip(
                                    selected = selectedEntity.value == entity,
                                    onClick = { 
                                        selectedEntity.value = entity
                                        viewModel.setEntityFilter(entity)
                                    },
                                    label = { Text(entity) }
                                )
                            }
                        }
                    }
                    ExposedDropdownMenuBox(
                        expanded = sortExpanded.value,
                        onExpandedChange = { sortExpanded.value = !sortExpanded.value },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
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
                            .padding(bottom = 16.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded.value,
                        onExpandedChange = { expanded.value = !expanded.value },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
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
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                when (uiState) {
                    is NewbornByRequestDateUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is NewbornByRequestDateUiState.Error -> {
                        val message = (uiState as NewbornByRequestDateUiState.Error).message
                        val userMessage = if (message.contains("500")) {
                            "Došlo je do greške na serveru. Pokušajte ponovo kasnije."
                        } else {
                            "Greška: $message"
                        }
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = userMessage,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                    is NewbornByRequestDateUiState.Success -> {
                        val newborns = (uiState as NewbornByRequestDateUiState.Success).newborns
                        if (newborns.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Nema podataka za prikaz.", style = MaterialTheme.typography.bodyLarge)
                            }
                        } else {
                            NewbornByRequestDateList(newborns, onNewbornClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewbornByRequestDateList(newborns: List<NewbornByRequestDateEntity>, onNewbornClick: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(newborns) { newborn ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNewbornClick(newborn.id) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Općina: ${newborn.municipality ?: "Nepoznato"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Godina: ${newborn.year ?: "-"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Ukupno: ${newborn.total ?: "-"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Muški: ${newborn.maleTotal ?: "-"}, Ženski: ${newborn.femaleTotal ?: "-"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
} 