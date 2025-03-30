package com.example.nutripal.ui.feature.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutripal.domain.model.NutritionItem
import com.example.nutripal.domain.usecase.GetNutritionInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val getNutritionInfoUseCase: GetNutritionInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionUiState())
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun searchNutritionInfo() {
        val query = _uiState.value.searchQuery.trim()

        if (query.isEmpty()) {
            _uiState.update {
                it.copy(
                    error = "Masukkan nama makanan atau minuman",
                    isLoading = false
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                getNutritionInfoUseCase(query).collect { items ->
                    _uiState.update {
                        it.copy(
                            nutritionItems = items,
                            isLoading = false,
                            error = if (items.isEmpty()) "Tidak ada hasil ditemukan" else null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Terjadi kesalahan: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearSearch() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                nutritionItems = emptyList(),
                error = null
            )
        }
    }
}

data class NutritionUiState(
    val searchQuery: String = "",
    val nutritionItems: List<NutritionItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)