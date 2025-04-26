package com.example.nutripal.ui.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutripal.domain.model.UserData
import com.example.nutripal.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Named

/**
 * ViewModel untuk mengelola state OnboardingScreen dan data pengguna
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    @Named("LocalUserRepository") private val userRepository: IUserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.getUserData().collect { userData ->
                _userData.value = userData
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun saveUserDataAndCompleteOnboarding(
        onSuccess: () -> Unit
    ) {
        val name = uiState.value.name.trim()

        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nama tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            try {
                // Show a loading indicator
                _uiState.update { it.copy(isLoading = true) }

                // Save user data first
                userRepository.saveUserData(name)

                // Then complete onboarding in a separate call
                userRepository.completeOnboarding()

                // Update UI state
                _uiState.update { it.copy(errorMessage = null, isLoading = false) }

                // Add a small delay to ensure data is committed
                delay(300)

                // Finally call the success callback
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Gagal menyimpan data: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    suspend fun hasCompletedOnboarding(): Boolean {
        return userRepository.hasCompletedOnboarding()
    }
}

/**
 * Data class untuk state UI di layar onboarding
 */
data class OnboardingUiState(
    val name: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)