package com.example.nutripal.ui.feature.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutripal.domain.model.ActivityResult
import com.example.nutripal.domain.model.PhysicalActivity
import com.example.nutripal.domain.usecase.CalculateActivityCaloriesUseCase
import com.example.nutripal.domain.usecase.GetPhysicalActivitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhysicalActivityViewModel @Inject constructor(
    private val getPhysicalActivitiesUseCase: GetPhysicalActivitiesUseCase,
    private val calculateActivityCaloriesUseCase: CalculateActivityCaloriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhysicalActivityUiState())
    val uiState: StateFlow<PhysicalActivityUiState> = _uiState.asStateFlow()

    init {
        loadActivities()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            val activities = getPhysicalActivitiesUseCase()
            _uiState.update {
                it.copy(
                    activities = activities,
                    selectedActivity = activities.firstOrNull() ?: it.selectedActivity
                )
            }
        }
    }

    fun onActivitySelected(activity: PhysicalActivity) {
        _uiState.update { it.copy(selectedActivity = activity) }
    }

    fun onDurationChanged(duration: String) {
        _uiState.update { it.copy(duration = duration) }
    }

    fun onWeightChanged(weight: String) {
        _uiState.update { it.copy(weight = weight) }
    }

    fun calculateCaloriesBurned() {
        viewModelScope.launch {
            val currentState = _uiState.value

            // Validasi input
            if (currentState.duration.isBlank() || currentState.weight.isBlank() || currentState.selectedActivity == null) {
                _uiState.update { it.copy(error = "Semua field harus diisi") }
                return@launch
            }

            try {
                val duration = currentState.duration.toInt()
                val weight = currentState.weight.toDouble()
                val activity = currentState.selectedActivity

                if (duration <= 0 || weight <= 0) {
                    _uiState.update { it.copy(error = "Nilai tidak boleh 0 atau negatif") }
                    return@launch
                }

                val activityResult = calculateActivityCaloriesUseCase(
                    activity = activity,
                    durationMinutes = duration,
                    weightKg = weight
                )

                _uiState.update {
                    it.copy(
                        result = activityResult,
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
                duration = "",
                weight = "",
                result = null,
                error = null,
                isCalculated = false
            )
        }
    }
}

data class PhysicalActivityUiState(
    val activities: List<PhysicalActivity> = emptyList(),
    val selectedActivity: PhysicalActivity? = null,
    val duration: String = "",
    val weight: String = "",
    val result: ActivityResult? = null,
    val error: String? = null,
    val isCalculated: Boolean = false
)