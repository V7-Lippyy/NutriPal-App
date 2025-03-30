package com.example.nutripal.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nutripal.data.local.entity.FoodEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface FoodEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(foodEntry: FoodEntry): Long

    @Update
    suspend fun updateFoodEntry(foodEntry: FoodEntry)

    @Delete
    suspend fun deleteFoodEntry(foodEntry: FoodEntry)

    @Query("SELECT * FROM food_entries WHERE id = :id")
    suspend fun getFoodEntryById(id: Long): FoodEntry?

    @Query("SELECT * FROM food_entries ORDER BY date DESC, time DESC")
    fun getAllFoodEntries(): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY time ASC")
    fun getFoodEntriesByDateRange(startDate: Date, endDate: Date): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries WHERE mealType = :mealType ORDER BY date DESC, time DESC")
    fun getFoodEntriesByMealType(mealType: String): Flow<List<FoodEntry>>

    @Query("SELECT SUM(calories) FROM food_entries WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalCaloriesForDateRange(startDate: Date, endDate: Date): Flow<Double>

    @Query("SELECT SUM(protein) as protein, SUM(carbs) as carbs, SUM(fat) as fat FROM food_entries WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalNutrientsForDateRange(startDate: Date, endDate: Date): Flow<NutrientSummary>
}

data class NutrientSummary(
    val protein: Double?,
    val carbs: Double?,
    val fat: Double?
)