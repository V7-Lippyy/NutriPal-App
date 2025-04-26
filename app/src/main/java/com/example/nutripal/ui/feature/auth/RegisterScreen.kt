package com.example.nutripal.ui.feature.auth

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutripal.ui.common.components.NutriPalButton
import com.example.nutripal.ui.common.components.NutriPalTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "RegisterScreen"

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe auth events
    LaunchedEffect(key1 = Unit) {
        Log.d(TAG, "Setting up auth events collector")
        viewModel.authEvents.collectLatest { event ->
            Log.d(TAG, "Received auth event: $event")
            when (event) {
                is AuthEvent.RegisterSuccess -> {
                    Log.d(TAG, "Registration successful, navigating to home")
                    onNavigateToHome()
                }
                is AuthEvent.AuthError -> {
                    Log.e(TAG, "Auth error: ${event.message}")
                    snackbarHostState.showSnackbar(event.message)
                }
                else -> {
                    Log.d(TAG, "Ignoring other auth event: $event")
                }
            }
        }
    }

    // Auto-reset loading state after timeout
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            Log.d(TAG, "Loading state activated, setting timeout")
            delay(10000) // 10 seconds timeout
            if (viewModel.uiState.value.isLoading) {
                Log.w(TAG, "Loading timeout reached, resetting loading state")
                viewModel.resetLoadingState()
                snackbarHostState.showSnackbar("Waktu proses habis. Silakan coba lagi.")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header
                Text(
                    text = "Buat Akun Baru",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Daftar untuk mulai menggunakan NutriPal",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Email field
                NutriPalTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = "Email",
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null)
                    },
                    isError = uiState.emailError != null,
                    errorMessage = uiState.emailError ?: "",
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Username field
                NutriPalTextField(
                    value = uiState.username,
                    onValueChange = { viewModel.updateUsername(it) },
                    label = "Username",
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    },
                    isError = uiState.usernameError != null,
                    errorMessage = uiState.usernameError ?: "",
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                NutriPalTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = "Password",
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                    },
                    isError = uiState.passwordError != null,
                    errorMessage = uiState.passwordError ?: "",
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Register button
                NutriPalButton(
                    text = "Daftar",
                    onClick = {
                        Log.d(TAG, "Register button clicked")
                        viewModel.register()
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Login link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sudah punya akun? ",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Masuk",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }

            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}