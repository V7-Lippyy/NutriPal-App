package com.example.nutripal.data.local.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.nutripal.domain.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property untuk Context yang membuat DataStore
val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

/**
 * Class yang menyediakan akses ke DataStore untuk menyimpan dan mengambil preferensi pengguna
 */
@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // Keys untuk preferensi
    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    }

    // Flow untuk mendapatkan data pengguna dari DataStore
    val userData: Flow<UserData> = dataStore.data.map { preferences ->
        UserData(
            name = preferences[PreferencesKeys.USER_NAME] ?: "",
            hasCompletedOnboarding = preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
        )
    }

    // Fungsi untuk menyimpan data pengguna
    suspend fun saveUserData(userData: UserData) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.USER_NAME] = userData.name
                preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = userData.hasCompletedOnboarding
            }
        } catch (e: Exception) {
            Log.e("UserPreferences", "Error saving user data", e)
        }
    }

    // Fungsi untuk menyimpan status onboarding
    suspend fun completeOnboarding() {
        try {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = true
            }
        } catch (e: Exception) {
            Log.e("UserPreferences", "Error completing onboarding", e)
        }
    }

    // Fungsi untuk mengecek apakah onboarding sudah selesai
    suspend fun hasCompletedOnboarding(): Boolean {
        return try {
            dataStore.data.map { preferences ->
                preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
            }.first() // Menggunakan first() untuk mengambil nilai pertama dari Flow
        } catch (e: Exception) {
            // Log error dan return default value (false) jika terjadi error
            Log.e("UserPreferences", "Error checking onboarding status", e)
            false
        }
    }
}