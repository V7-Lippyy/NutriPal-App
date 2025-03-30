package com.example.nutripal.domain.repository

import com.example.nutripal.domain.model.UserData
import kotlinx.coroutines.flow.Flow

/**
 * Interface untuk repository yang mengelola data pengguna
 */
interface IUserRepository {
    /**
     * Mendapatkan data pengguna sebagai Flow
     */
    fun getUserData(): Flow<UserData>

    /**
     * Menyimpan data pengguna
     */
    suspend fun saveUserData(name: String)

    /**
     * Menandai bahwa onboarding telah selesai
     */
    suspend fun completeOnboarding()

    /**
     * Mengecek apakah pengguna telah menyelesaikan onboarding
     */
    suspend fun hasCompletedOnboarding(): Boolean
}