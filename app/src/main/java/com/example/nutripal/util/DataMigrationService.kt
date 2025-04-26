package com.example.nutripal.util

import android.content.Context
import com.example.nutripal.data.local.database.NutriPalDatabase
import com.example.nutripal.data.local.datastore.UserPreferences
import com.example.nutripal.domain.repository.IFoodEntryRepository
import com.example.nutripal.domain.repository.IUserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Named

@Singleton
class DataMigrationService @Inject constructor(
    private val localDatabase: NutriPalDatabase,
    private val userPreferences: UserPreferences,
    @Named("FirebaseRepository") private val foodEntryRepository: IFoodEntryRepository,
    @Named("FirebaseUserRepository") private val userRepository: IUserRepository,
    private val context: Context
) {

    suspend fun migrateDataIfNeeded() {
        val migrationDone = context.getSharedPreferences("migration_prefs", Context.MODE_PRIVATE)
            .getBoolean("data_migrated", false)

        if (migrationDone) {
            return
        }

        try {
            // Migrate user preferences
            val userData = userPreferences.userData.first()
            if (userData.name.isNotEmpty()) {
                userRepository.saveUserData(userData.name)
            }

            if (userData.hasCompletedOnboarding) {
                userRepository.completeOnboarding()
            }

            // Migrate food entries from local DB to Firebase repository
            val foodEntries = localDatabase.foodEntryDao().getAllFoodEntries().first()
            for (entry in foodEntries) {
                foodEntryRepository.addFoodEntry(entry)
            }

            // Set migration flag
            context.getSharedPreferences("migration_prefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("data_migrated", true)
                .apply()

        } catch (e: Exception) {
            // Optional: handle migration errors (log/report)
        }
    }
}