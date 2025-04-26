package com.example.nutripal.ui.feature.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutripal.domain.model.AuthResult
import com.example.nutripal.domain.model.User
import com.example.nutripal.domain.repository.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _authEvents = MutableSharedFlow<AuthEvent>()
    val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        Log.d(TAG, "ViewModel initialized")
        // Observe current user
        authRepository.getCurrentUser().onEach { user ->
            Log.d(TAG, "Current user updated: ${user?.username ?: "null"}")
            _currentUser.value = user
        }.launchIn(viewModelScope)

        // Observe authentication state
        authRepository.isUserAuthenticated().onEach { isAuthenticated ->
            Log.d(TAG, "Authentication state updated: $isAuthenticated")
            _uiState.update { it.copy(isAuthenticated = isAuthenticated) }
        }.launchIn(viewModelScope)
    }

    // UI State updaters
    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username, usernameError = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun updateEmailOrUsername(emailOrUsername: String) {
        _uiState.update { it.copy(emailOrUsername = emailOrUsername, emailOrUsernameError = null) }
    }

    // Form validation
    private fun validateRegisterForm(): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        val email = uiState.value.email
        val username = uiState.value.username
        val password = uiState.value.password

        Log.d(TAG, "Validating register form: email=$email, username=$username, password=***")

        return when {
            !email.matches(emailRegex.toRegex()) -> {
                Log.d(TAG, "Email tidak valid: $email")
                _uiState.update { it.copy(emailError = "Email tidak valid") }
                false
            }
            username.length < 3 -> {
                Log.d(TAG, "Username terlalu pendek: $username")
                _uiState.update { it.copy(usernameError = "Username minimal 3 karakter") }
                false
            }
            password.length < 6 -> {
                Log.d(TAG, "Password terlalu pendek")
                _uiState.update { it.copy(passwordError = "Password minimal 6 karakter") }
                false
            }
            else -> {
                Log.d(TAG, "Form validasi sukses")
                true
            }
        }
    }

    private fun validateLoginForm(): Boolean {
        val emailOrUsername = uiState.value.emailOrUsername
        val password = uiState.value.password

        Log.d(TAG, "Validating login form: emailOrUsername=$emailOrUsername, password=***")

        return when {
            emailOrUsername.isBlank() -> {
                Log.d(TAG, "Email/username kosong")
                _uiState.update { it.copy(emailOrUsernameError = "Email atau username tidak boleh kosong") }
                false
            }
            password.isBlank() -> {
                Log.d(TAG, "Password kosong")
                _uiState.update { it.copy(passwordError = "Password tidak boleh kosong") }
                false
            }
            else -> {
                Log.d(TAG, "Form validasi sukses")
                true
            }
        }
    }

    private fun validateForgotPasswordForm(): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        val email = uiState.value.email

        Log.d(TAG, "Validating forgot password form: email=$email")

        return when {
            !email.matches(emailRegex.toRegex()) -> {
                Log.d(TAG, "Email tidak valid: $email")
                _uiState.update { it.copy(emailError = "Email tidak valid") }
                false
            }
            else -> {
                Log.d(TAG, "Form validasi sukses")
                true
            }
        }
    }

    // Authentication actions
    fun register() {
        if (!validateRegisterForm()) {
            Log.d(TAG, "Register form validation failed")
            return
        }

        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting registration process")
                _uiState.update { it.copy(isLoading = true, error = null) }

                val email = uiState.value.email
                val username = uiState.value.username
                val password = uiState.value.password

                val result = authRepository.register(
                    email = email,
                    username = username,
                    password = password
                )

                Log.d(TAG, "Registration process completed with result: $result")

                when (result) {
                    is AuthResult.Success -> {
                        Log.d(TAG, "Registration success for user: ${result.data?.username}")
                        _uiState.update { it.copy(isLoading = false) }
                        _authEvents.emit(AuthEvent.RegisterSuccess)
                    }
                    is AuthResult.Error -> {
                        Log.e(TAG, "Registration error: ${result.message}")
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                        _authEvents.emit(AuthEvent.AuthError(result.message ?: "Terjadi kesalahan"))
                    }
                    else -> {
                        Log.d(TAG, "Unexpected registration result")
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during registration: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
                _authEvents.emit(AuthEvent.AuthError(e.message ?: "Terjadi kesalahan tidak terduga"))
            }
        }
    }

    fun login() {
        if (!validateLoginForm()) {
            Log.d(TAG, "Login form validation failed")
            return
        }

        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting login process")
                _uiState.update { it.copy(isLoading = true, error = null) }

                val result = authRepository.login(
                    emailOrUsername = uiState.value.emailOrUsername,
                    password = uiState.value.password
                )

                Log.d(TAG, "Login process completed with result: $result")

                when (result) {
                    is AuthResult.Success -> {
                        Log.d(TAG, "Login success for user: ${result.data?.username}")
                        _uiState.update { it.copy(isLoading = false) }
                        _authEvents.emit(AuthEvent.LoginSuccess)
                    }
                    is AuthResult.Error -> {
                        Log.e(TAG, "Login error: ${result.message}")
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                        _authEvents.emit(AuthEvent.AuthError(result.message ?: "Terjadi kesalahan"))
                    }
                    else -> {
                        Log.d(TAG, "Unexpected login result")
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during login: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
                _authEvents.emit(AuthEvent.AuthError(e.message ?: "Terjadi kesalahan tidak terduga"))
            }
        }
    }

    fun sendPasswordResetEmail() {
        if (!validateForgotPasswordForm()) {
            Log.d(TAG, "Forgot password form validation failed")
            return
        }

        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting password reset process")
                _uiState.update { it.copy(isLoading = true, error = null) }

                val result = authRepository.sendPasswordResetEmail(uiState.value.email)

                Log.d(TAG, "Password reset process completed with result: $result")

                when (result) {
                    is AuthResult.Success -> {
                        Log.d(TAG, "Password reset email sent successfully")
                        _uiState.update { it.copy(isLoading = false) }
                        _authEvents.emit(AuthEvent.PasswordResetEmailSent)
                    }
                    is AuthResult.Error -> {
                        Log.e(TAG, "Password reset error: ${result.message}")
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                        _authEvents.emit(AuthEvent.AuthError(result.message ?: "Terjadi kesalahan"))
                    }
                    else -> {
                        Log.d(TAG, "Unexpected password reset result")
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during password reset: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
                _authEvents.emit(AuthEvent.AuthError(e.message ?: "Terjadi kesalahan tidak terduga"))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting logout process")
                _uiState.update { it.copy(isLoading = true) }

                val result = authRepository.logout()

                Log.d(TAG, "Logout process completed with result: $result")

                when (result) {
                    is AuthResult.Success -> {
                        Log.d(TAG, "Logout success")
                        _uiState.update { it.copy(isLoading = false) }
                        _authEvents.emit(AuthEvent.LogoutSuccess)
                    }
                    is AuthResult.Error -> {
                        Log.e(TAG, "Logout error: ${result.message}")
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                        _authEvents.emit(AuthEvent.AuthError(result.message ?: "Terjadi kesalahan"))
                    }
                    else -> {
                        Log.d(TAG, "Unexpected logout result")
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during logout: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, error = e.message) }
                _authEvents.emit(AuthEvent.AuthError(e.message ?: "Terjadi kesalahan tidak terduga"))
            }
        }
    }

    // Reset errors
    fun resetErrors() {
        Log.d(TAG, "Resetting all errors")
        _uiState.update {
            it.copy(
                emailError = null,
                usernameError = null,
                passwordError = null,
                emailOrUsernameError = null,
                error = null
            )
        }
    }

    // Function to manually reset loading state (useful for timeouts)
    fun resetLoadingState() {
        Log.d(TAG, "Manually resetting loading state")
        _uiState.update { it.copy(isLoading = false) }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val emailOrUsername: String = "",
    val emailError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val emailOrUsernameError: String? = null,
    val error: String? = null
)

sealed class AuthEvent {
    object RegisterSuccess : AuthEvent()
    object LoginSuccess : AuthEvent()
    object LogoutSuccess : AuthEvent()
    object PasswordResetEmailSent : AuthEvent()
    data class AuthError(val message: String) : AuthEvent()
}