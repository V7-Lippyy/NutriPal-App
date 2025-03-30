package com.example.nutripal.data.repository

import com.example.nutripal.data.local.dao.FoodEntryDao
import com.example.nutripal.data.local.entity.FoodEntry
import com.example.nutripal.domain.model.MealType
import com.example.nutripal.domain.repository.IFoodEntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class FoodEntryRepository @Inject constructor(
    private val foodEntryDao: FoodEntryDao
) : IFoodEntryRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override suspend fun addFoodEntry(foodEntry: FoodEntry): Long {
        println("Repository: Adding food entry - Date=${foodEntry.date}, Name=${foodEntry.name}")
        // Log standardized date in repository
        println("Repository: Food entry time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(foodEntry.date)}")

        // Penting: pastikan kita tidak mengubah tanggal lagi disini
        return foodEntryDao.insertFoodEntry(foodEntry)
    }

    override suspend fun updateFoodEntry(foodEntry: FoodEntry) {
        println("Repository: Updating food entry - ID=${foodEntry.id}, Date=${foodEntry.date}")
        // Log standardized date in repository
        println("Repository: Food entry time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(foodEntry.date)}")

        foodEntryDao.updateFoodEntry(foodEntry)
    }

    override suspend fun deleteFoodEntry(foodEntry: FoodEntry) {
        foodEntryDao.deleteFoodEntry(foodEntry)
    }

    override suspend fun getFoodEntryById(id: Long): FoodEntry? {
        return foodEntryDao.getFoodEntryById(id)
    }

    override fun getAllFoodEntries(): Flow<List<FoodEntry>> {
        return foodEntryDao.getAllFoodEntries()
    }

    override fun getFoodEntriesByDate(date: Date): Flow<List<FoodEntry>> {
        // Format date as start and end of day to compare with database entries
        val startDate = getStartOfDay(date)
        val endDate = getEndOfDay(date)

        println("Repository: Getting entries for date: ${dateFormat.format(date)}")
        println("Repository: Start date: ${dateFormat.format(startDate)} ${startDate.time}")
        println("Repository: End date: ${dateFormat.format(endDate)} ${endDate.time}")

        // Get entries for the specified date
        return foodEntryDao.getFoodEntriesByDateRange(startDate, endDate)
    }

    override fun getFoodEntriesByDateRange(startDate: Date, endDate: Date): Flow<List<FoodEntry>> {
        val start = getStartOfDay(startDate)
        val end = getEndOfDay(endDate)
        return foodEntryDao.getFoodEntriesByDateRange(start, end)
    }

    override fun getFoodEntriesByMealType(mealType: MealType): Flow<List<FoodEntry>> {
        return foodEntryDao.getFoodEntriesByMealType(mealType.name)
    }

    override fun getTotalCaloriesForDate(date: Date): Flow<Double> {
        val startDate = getStartOfDay(date)
        val endDate = getEndOfDay(date)

        println("Repository: Getting total calories for date: ${dateFormat.format(date)}")
        println("Repository: Start date: ${dateFormat.format(startDate)}")
        println("Repository: End date: ${dateFormat.format(endDate)}")

        return foodEntryDao.getTotalCaloriesForDateRange(startDate, endDate)
    }

    override fun getTotalNutrientsForDate(date: Date): Flow<Map<String, Double>> {
        val startDate = getStartOfDay(date)
        val endDate = getEndOfDay(date)

        // Get total nutrients for the specified day
        return foodEntryDao.getTotalNutrientsForDateRange(startDate, endDate).map { nutrients ->
            mapOf(
                "protein" to (nutrients.protein ?: 0.0),
                "carbs" to (nutrients.carbs ?: 0.0),
                "fat" to (nutrients.fat ?: 0.0)
            )
        }
    }

    // Helper method to get the start of a day (00:00:00)
    private fun getStartOfDay(date: Date): Date {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.time
    }

    // Helper method to get the end of a day (23:59:59)
    private fun getEndOfDay(date: Date): Date {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        return calendar.time
    }
}