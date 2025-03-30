package com.example.nutripal.ui.feature.dailycalorie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutripal.domain.model.CalorieResult
import com.example.nutripal.domain.model.Gender
import com.example.nutripal.domain.usecase.CalculateDailyCaloriesUseCase
import com.example.nutripal.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyCalorieViewModel @Inject constructor(
    private val calculateDailyCaloriesUseCase: CalculateDailyCaloriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyCalorieUiState())
    val uiState: StateFlow<DailyCalorieUiState> = _uiState.asStateFlow()

    fun onGenderChanged(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onAgeChanged(age: String) {
        _uiState.update { it.copy(age = age) }
    }

    fun onHeightChanged(height: String) {
        _uiState.update { it.copy(height = height) }
    }

    fun onCurrentWeightChanged(weight: String) {
        _uiState.update { it.copy(currentWeight = weight) }
    }

    fun onTargetWeightChanged(weight: String) {
        _uiState.update { it.copy(targetWeight = weight) }
    }

    fun onTargetWeeksChanged(weeks: String) {
        _uiState.update { it.copy(targetWeeks = weeks) }
    }

    fun onActivityLevelChanged(level: ActivityLevel) {
        _uiState.update { it.copy(activityLevel = level) }
    }

    fun calculateDailyCalories() {
        viewModelScope.launch {
            val currentState = _uiState.value

            // Validasi input
            if (currentState.age.isBlank() || currentState.height.isBlank() ||
                currentState.currentWeight.isBlank() || currentState.targetWeight.isBlank() ||
                currentState.targetWeeks.isBlank()) {

                _uiState.update { it.copy(error = "Semua field harus diisi") }
                return@launch
            }

            try {
                val age = currentState.age.toInt()
                val height = currentState.height.toDouble()
                val currentWeight = currentState.currentWeight.toDouble()
                val targetWeight = currentState.targetWeight.toDouble()
                val targetWeeks = currentState.targetWeeks.toInt()

                if (age <= 0 || height <= 0 || currentWeight <= 0 || targetWeight <= 0 || targetWeeks <= 0) {
                    _uiState.update { it.copy(error = "Nilai tidak boleh 0 atau negatif") }
                    return@launch
                }

                val activityMultiplier = when(currentState.activityLevel) {
                    ActivityLevel.SEDENTARY -> Constants.ACTIVITY_SEDENTARY
                    ActivityLevel.LIGHT -> Constants.ACTIVITY_LIGHT
                    ActivityLevel.MODERATE -> Constants.ACTIVITY_MODERATE
                    ActivityLevel.ACTIVE -> Constants.ACTIVITY_ACTIVE
                    ActivityLevel.VERY_ACTIVE -> Constants.ACTIVITY_VERY_ACTIVE
                }

                val calorieResult = calculateDailyCaloriesUseCase(
                    gender = currentState.gender,
                    age = age,
                    heightCm = height,
                    currentWeightKg = currentWeight,
                    targetWeightKg = targetWeight,
                    targetWeeks = targetWeeks,
                    activityLevel = activityMultiplier
                )

                _uiState.update {
                    it.copy(
                        result = calorieResult,
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
                currentWeight = "",
                targetWeight = "",
                targetWeeks = "",
                result = null,
                error = null,
                isCalculated = false
            )
        }
    }
}

enum class ActivityLevel(val displayName: String, val description: String) {
    SEDENTARY("Tidak Aktif", "Hampir tidak ada aktivitas fisik, pekerjaan di meja"),
    LIGHT("Sedikit Aktif", "Olahraga ringan 1-3 hari/minggu"),
    MODERATE("Cukup Aktif", "Olahraga sedang 3-5 hari/minggu"),
    ACTIVE("Sangat Aktif", "Olahraga berat 6-7 hari/minggu"),
    VERY_ACTIVE("Ekstra Aktif", "Olahraga sangat berat, pekerjaan fisik, latihan 2x sehari")
}

data class DailyCalorieUiState(
    val gender: Gender = Gender.MALE,
    val age: String = "",
    val height: String = "",
    val currentWeight: String = "",
    val targetWeight: String = "",
    val targetWeeks: String = "",
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val result: CalorieResult? = null,
    val error: String? = null,
    val isCalculated: Boolean = false
)