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
import javax.inject.Inject

/**
 * ViewModel untuk mengelola state OnboardingScreen dan data pengguna
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {

    // UIState untuk layar onboarding
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    // State untuk data pengguna yang di-expose ke UI
    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    init {
        // Load data pengguna saat ViewModel dibuat
        viewModelScope.launch {
            userRepository.getUserData().collect { userData ->
                _userData.value = userData
            }
        }
    }

    // Fungsi untuk mengupdate nama
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    // Fungsi untuk validasi dan menyimpan data pengguna
    fun saveUserDataAndCompleteOnboarding(
        onSuccess: () -> Unit
    ) {
        val name = uiState.value.name.trim()

        // Validasi input
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nama tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            try {
                // Simpan data pengguna
                userRepository.saveUserData(name)

                // Tandai onboarding selesai
                userRepository.completeOnboarding()

                // Reset error message
                _uiState.update { it.copy(errorMessage = null) }

                // Callback untuk sukses
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Gagal menyimpan data: ${e.message}") }
            }
        }
    }

    // Fungsi untuk mengecek apakah onboarding sudah selesai
    suspend fun hasCompletedOnboarding(): Boolean {
        return userRepository.hasCompletedOnboarding()
    }
}

/**
 * Data class untuk state UI di layar onboarding
 */
data class OnboardingUiState(
    val name: String = "",
    val errorMessage: String? = null
)