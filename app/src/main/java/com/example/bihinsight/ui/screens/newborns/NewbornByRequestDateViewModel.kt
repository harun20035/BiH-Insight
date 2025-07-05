package com.example.bihinsight.ui.screens.newborns

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bihinsight.data.local.NewbornByRequestDateEntity
import com.example.bihinsight.data.repository.NewbornByRequestDateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import retrofit2.HttpException

sealed class NewbornByRequestDateUiState {
    object Loading : NewbornByRequestDateUiState()
    data class Success(val newborns: List<NewbornByRequestDateEntity>) : NewbornByRequestDateUiState()
    data class Error(val message: String) : NewbornByRequestDateUiState()
}

class NewbornByRequestDateViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: NewbornByRequestDateRepository,
    private val token: String? = null,
    private val languageId: Int = 1
) : ViewModel() {
    private val _uiState = MutableStateFlow<NewbornByRequestDateUiState>(NewbornByRequestDateUiState.Loading)
    val uiState: StateFlow<NewbornByRequestDateUiState> = _uiState.asStateFlow()

    val filterText: StateFlow<String> = savedStateHandle.getStateFlow("filter_text", "")
    val yearFilter: StateFlow<Int?> = savedStateHandle.getStateFlow("year_filter", null as Int?)
    val entityFilter: StateFlow<String?> = savedStateHandle.getStateFlow("entity_filter", null as String?)
    val isRefreshing: StateFlow<Boolean> = savedStateHandle.getStateFlow("is_refreshing", false)

    enum class SortOption { MUNICIPALITY, YEAR_DESC, TOTAL_DESC }
    val sortOption: StateFlow<SortOption> = savedStateHandle.getStateFlow("sort_option", SortOption.MUNICIPALITY)

    private val _detailNewborn = MutableStateFlow<NewbornByRequestDateEntity?>(null)
    val detailNewborn: StateFlow<NewbornByRequestDateEntity?> = _detailNewborn.asStateFlow()

    init {
        fetchNewborns()
    }

    fun fetchNewborns() {
        viewModelScope.launch {
            savedStateHandle["is_refreshing"] = true
            _uiState.value = NewbornByRequestDateUiState.Loading
            try {
                repository.fetchAndCacheNewborns(token, languageId)
                val data = repository.getAllFromDb()
                if (data.isEmpty()) {
                    _uiState.value = NewbornByRequestDateUiState.Error("Nema podataka za prikaz")
                } else {
                    _uiState.value = NewbornByRequestDateUiState.Success(data)
                }
            } catch (e: java.net.UnknownHostException) {
                _uiState.value = NewbornByRequestDateUiState.Error("Nema internet konekcije. Provjerite vašu mrežu.")
            } catch (e: java.net.SocketTimeoutException) {
                _uiState.value = NewbornByRequestDateUiState.Error("Vremensko ograničenje konekcije. Pokušajte ponovo.")
            } catch (e: retrofit2.HttpException) {
                val errorMessage = when (e.code()) {
                    401 -> "Neautorizovan pristup. Provjerite vaš token."
                    403 -> "Zabranjen pristup."
                    404 -> "Podaci nisu pronađeni."
                    500 -> "Greška na serveru. Pokušajte kasnije."
                    else -> "Greška mreže: ${e.code()}"
                }
                _uiState.value = NewbornByRequestDateUiState.Error(errorMessage)
            } catch (e: Exception) {
                Log.e("NewbornByRequestDateViewModel", "Error fetching data", e)
                _uiState.value = NewbornByRequestDateUiState.Error("Greška: ${e.message ?: "Nepoznata greška"}")
            } finally {
                savedStateHandle["is_refreshing"] = false
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
            _uiState.value = NewbornByRequestDateUiState.Loading
            try {
                val data = repository.filterCombined(
                    municipality = filterText.value.takeIf { it.isNotBlank() },
                    year = yearFilter.value
                )
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
                if (sorted.isEmpty()) {
                    _uiState.value = NewbornByRequestDateUiState.Error("Nema podataka koji odgovaraju vašim filterima")
                } else {
                    _uiState.value = NewbornByRequestDateUiState.Success(sorted)
                }
            } catch (e: Exception) {
                Log.e("NewbornByRequestDateViewModel", "Error filtering data", e)
                _uiState.value = NewbornByRequestDateUiState.Error("Greška pri filtriranju: ${e.message ?: "Nepoznata greška"}")
            }
        }
    }

    fun setFilterText(text: String) {
        savedStateHandle["filter_text"] = text
        filterCombined()
    }

    fun loadDetailNewborn(id: Int) {
        viewModelScope.launch {
            _detailNewborn.value = repository.getById(id)
        }
    }

    fun refreshDetailNewborn() {
        _detailNewborn.value?.id?.let { loadDetailNewborn(it) }
    }

    fun addToFavorites(id: Int) {
        viewModelScope.launch {
            val newborn = repository.getById(id)
            if (newborn != null) {
                repository.updateNewborn(newborn.copy(isFavorite = true))
                val updated = repository.getById(id)
                Log.d("FAV_DEBUG", "addToFavorites: id=$id, isFavorite=${updated?.isFavorite}")
            }
            filterCombined()
        }
    }

    fun removeFromFavorites(id: Int) {
        viewModelScope.launch {
            val newborn = repository.getById(id)
            if (newborn != null) {
                repository.updateNewborn(newborn.copy(isFavorite = false))
                val updated = repository.getById(id)
                Log.d("FAV_DEBUG", "removeFromFavorites: id=$id, isFavorite=${updated?.isFavorite}")
            }
            filterCombined()
        }
    }

    fun getFavorites(onResult: (List<NewbornByRequestDateEntity>) -> Unit) {
        viewModelScope.launch {
            val favorites = repository.getFavorites()
            onResult(favorites)
        }
    }

    fun observeDetailNewborn(id: Int) = repository.observeById(id)

    fun observeFavorites() = repository.observeFavorites()
} 