package com.example.nutripal.domain.repository

import com.example.nutripal.domain.model.AuthResult
import com.example.nutripal.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    // Register dengan email, username, dan password
    suspend fun register(email: String, username: String, password: String): AuthResult<User>

    // Login dengan email/username dan password
    suspend fun login(emailOrUsername: String, password: String): AuthResult<User>

    // Mengirim email reset password
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit>

    // Mendapatkan pengguna yang sedang login
    fun getCurrentUser(): Flow<User?>

    // Logout
    suspend fun logout(): AuthResult<Unit>

    // Memeriksa apakah pengguna sudah login
    fun isUserAuthenticated(): Flow<Boolean>
}