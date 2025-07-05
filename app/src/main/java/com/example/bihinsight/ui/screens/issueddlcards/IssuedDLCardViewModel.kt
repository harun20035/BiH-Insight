package com.example.bihinsight.ui.screens.issueddlcards

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bihinsight.data.local.IssuedDLCardEntity
import com.example.bihinsight.data.repository.IssuedDLCardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update

sealed class IssuedDLCardUiState {
    object Loading : IssuedDLCardUiState()
    data class Success(val cards: List<IssuedDLCardEntity>) : IssuedDLCardUiState()
    data class Error(val message: String) : IssuedDLCardUiState()
}

class IssuedDLCardViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: IssuedDLCardRepository,
    private val token: String? = null,
    private val languageId: Int = 1
) : ViewModel() {
    private val _uiState = MutableStateFlow<IssuedDLCardUiState>(IssuedDLCardUiState.Loading)
    val uiState: StateFlow<IssuedDLCardUiState> = _uiState.asStateFlow()

    // Spašavanje stanja sa SavedStateHandle
    val filterText: StateFlow<String> = savedStateHandle.getStateFlow("filter_text", "")
    val yearFilter: StateFlow<Int?> = savedStateHandle.getStateFlow("year_filter", null as Int?)
    val entityFilter: StateFlow<String?> = savedStateHandle.getStateFlow("entity_filter", null as String?)

    enum class SortOption { MUNICIPALITY, YEAR_DESC, TOTAL_DESC }
    val sortOption: StateFlow<SortOption> = savedStateHandle.getStateFlow("sort_option", SortOption.MUNICIPALITY)

    private val _detailCard = MutableStateFlow<IssuedDLCardEntity?>(null)
    val detailCard: StateFlow<IssuedDLCardEntity?> = _detailCard.asStateFlow()

    init {
        fetchIssuedDL()
    }

    fun fetchIssuedDL() {
        viewModelScope.launch {
            _uiState.value = IssuedDLCardUiState.Loading
            try {
                repository.fetchAndCacheIssuedDL(token, languageId)
                val data = repository.getAllFromDb()
                _uiState.value = IssuedDLCardUiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = IssuedDLCardUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setYearFilter(year: Int?) {
        savedStateHandle["year_filter"] = year
        filterCombined()
    }

    fun setEntityFilter(entity: String?) {
        savedStateHandle["entity_filter"] = entity
        filterCombined()
    }

    fun setSortOption(option: SortOption) {
        savedStateHandle["sort_option"] = option
        filterCombined()
    }

    private fun filterCombined() {
        viewModelScope.launch {
            _uiState.value = IssuedDLCardUiState.Loading
            try {
                val data = repository.filterCombined(
                    municipality = filterText.value.takeIf { it.isNotBlank() },
                    year = yearFilter.value
                )
                
                // Dodatno filtriranje po entitetu
                val entityFiltered = if (entityFilter.value != null) {
                    data.filter { it.entity == entityFilter.value }
                } else {
                    data
                }
                
                val sorted = when (sortOption.value) {
                    SortOption.MUNICIPALITY -> entityFiltered.sortedBy { it.municipality ?: "" }
                    SortOption.YEAR_DESC -> entityFiltered.sortedByDescending { it.year ?: 0 }
                    SortOption.TOTAL_DESC -> entityFiltered.sortedByDescending { it.total ?: 0 }
                }
                _uiState.value = IssuedDLCardUiState.Success(sorted)
            } catch (e: Exception) {
                _uiState.value = IssuedDLCardUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setFilterText(text: String) {
        savedStateHandle["filter_text"] = text
        filterCombined()
    }

    fun loadDetailCard(id: Int) {
        viewModelScope.launch {
            _detailCard.value = repository.getById(id)
        }
    }

    fun refreshDetailCard() {
        _detailCard.value?.id?.let { loadDetailCard(it) }
    }

    fun addToFavorites(id: Int) {
        viewModelScope.launch {
            val card = repository.getById(id)
            if (card != null) {
                repository.updateCard(card.copy(isFavorite = true))
                val updated = repository.getById(id)
                Log.d("FAV_DEBUG", "addToFavorites: id=$id, isFavorite=${updated?.isFavorite}")
            }
            filterCombined()
        }
    }

    fun removeFromFavorites(id: Int) {
        viewModelScope.launch {
            val card = repository.getById(id)
            if (card != null) {
                repository.updateCard(card.copy(isFavorite = false))
                val updated = repository.getById(id)
                Log.d("FAV_DEBUG", "removeFromFavorites: id=$id, isFavorite=${updated?.isFavorite}")
            }
            filterCombined()
        }
    }

    fun getFavorites(onResult: (List<IssuedDLCardEntity>) -> Unit) {
        viewModelScope.launch {
            val favorites = repository.getFavorites()
            onResult(favorites)
        }
    }

    fun observeDetailCard(id: Int) = repository.observeById(id)

    fun observeFavorites() = repository.observeFavorites()
} 