package com.example.nutripal.data.repository

import com.example.nutripal.data.local.datastore.UserPreferences
import com.example.nutripal.domain.model.UserData
import com.example.nutripal.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi dari IUserRepository yang menggunakan UserPreferences untuk menyimpan data
 */
@Singleton
class UserRepository @Inject constructor(
    private val userPreferences: UserPreferences
) : IUserRepository {

    override fun getUserData(): Flow<UserData> {
        return userPreferences.userData
    }

    override suspend fun saveUserData(name: String) {
        userPreferences.saveUserData(
            UserData(
                name = name,
                hasCompletedOnboarding = true
            )
        )
    }

    override suspend fun completeOnboarding() {
        userPreferences.completeOnboarding()
    }

    override suspend fun hasCompletedOnboarding(): Boolean {
        return userPreferences.hasCompletedOnboarding()
    }
}