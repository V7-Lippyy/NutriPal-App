package com.example.nutripal.domain.repository

import com.example.nutripal.data.local.entity.FoodEntry
import com.example.nutripal.domain.model.MealType
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface IFoodEntryRepository {
    suspend fun addFoodEntry(foodEntry: FoodEntry): Long

    suspend fun updateFoodEntry(foodEntry: FoodEntry)

    suspend fun deleteFoodEntry(foodEntry: FoodEntry)

    suspend fun getFoodEntryById(id: Long): FoodEntry?

    fun getAllFoodEntries(): Flow<List<FoodEntry>>

    fun getFoodEntriesByDate(date: Date): Flow<List<FoodEntry>>

    fun getFoodEntriesByDateRange(startDate: Date, endDate: Date): Flow<List<FoodEntry>>

    fun getFoodEntriesByMealType(mealType: MealType): Flow<List<FoodEntry>>

    fun getTotalCaloriesForDate(date: Date): Flow<Double>

    fun getTotalNutrientsForDate(date: Date): Flow<Map<String, Double>>
}