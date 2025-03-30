package com.example.nutripal.ui.feature.bmi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutripal.domain.model.BMIResult
import com.example.nutripal.domain.model.Gender
import com.example.nutripal.domain.usecase.CalculateBMIUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BMIViewModel @Inject constructor(
    private val calculateBMIUseCase: CalculateBMIUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BMIUiState())
    val uiState: StateFlow<BMIUiState> = _uiState.asStateFlow()

    fun onGenderChanged(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onAgeChanged(age: String) {
        _uiState.update { it.copy(age = age) }
    }

    fun onHeightChanged(height: String) {
        _uiState.update { it.copy(height = height) }
    }

    fun onWeightChanged(weight: String) {
        _uiState.update { it.copy(weight = weight) }
    }

    fun calculateBMI() {
        viewModelScope.launch {
            val currentState = _uiState.value

            // Validasi input
            if (currentState.age.isBlank() || currentState.height.isBlank() || currentState.weight.isBlank()) {
                _uiState.update { it.copy(error = "Semua field harus diisi") }
                return@launch
            }

            try {
                val age = currentState.age.toInt()
                val height = currentState.height.toDouble()
                val weight = currentState.weight.toDouble()

                if (age <= 0 || height <= 0 || weight <= 0) {
                    _uiState.update { it.copy(error = "Nilai tidak boleh 0 atau negatif") }
                    return@launch
                }

                val bmiResult = calculateBMIUseCase(
                    gender = currentState.gender,
                    age = age,
                    heightCm = height,
                    weightKg = weight
                )

                _uiState.update {
                    it.copy(
                        result = bmiResult,
                        error = null,
                        isCalculated = true
                    )
                }

            } catch (e: NumberFormatException) {
                _uiState.update { it.copy(error = "Format angka tidak valid") }
            }
        }
    }

    fun resetCalculation() {
        _uiState.update {
            it.copy(
                age = "",
                height = "",
                weight = "",
                result = null,
                error = null,
                isCalculated = false
            )
        }
    }
}

data class BMIUiState(
    val gender: Gender = Gender.MALE,
    val age: String = "",
    val height: String = "",
    val weight: String = "",
    val result: BMIResult? = null,
    val error: String? = null,
    val isCalculated: Boolean = false
)