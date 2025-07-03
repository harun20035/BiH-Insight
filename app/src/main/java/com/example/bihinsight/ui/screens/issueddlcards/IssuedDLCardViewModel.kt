package com.example.bihinsight.ui.screens.issueddlcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bihinsight.data.local.IssuedDLCardEntity
import com.example.bihinsight.data.repository.IssuedDLCardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class IssuedDLCardUiState {
    object Loading : IssuedDLCardUiState()
    data class Success(val cards: List<IssuedDLCardEntity>) : IssuedDLCardUiState()
    data class Error(val message: String) : IssuedDLCardUiState()
}

class IssuedDLCardViewModel(
    private val repository: IssuedDLCardRepository,
    private val token: String? = null,
    private val languageId: Int = 1
) : ViewModel() {
    private val _uiState = MutableStateFlow<IssuedDLCardUiState>(IssuedDLCardUiState.Loading)
    val uiState: StateFlow<IssuedDLCardUiState> = _uiState.asStateFlow()

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
} 