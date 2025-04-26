package com.example.nutripal.data.repository

import com.example.nutripal.data.local.entity.FoodEntry
import com.example.nutripal.domain.model.MealType
import com.example.nutripal.domain.repository.IFoodEntryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFoodEntryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : IFoodEntryRepository {

    // Collection paths
    private val usersCollection = "users"
    private val entriesCollection = "food_entries"

    // Helper untuk mendapatkan referensi koleksi food entries untuk user yang login
    private fun getUserEntriesRef() = auth.currentUser?.uid?.let { userId ->
        firestore.collection(usersCollection).document(userId).collection(entriesCollection)
    }

    override suspend fun addFoodEntry(foodEntry: FoodEntry): Long {
        val userEntriesRef = getUserEntriesRef() ?: throw Exception("User not authenticated")

        // Convert FoodEntry to Map
        val entryMap = mapOf(
            "name" to foodEntry.name,
            "servingSize" to foodEntry.servingSize,
            "servingUnit" to foodEntry.servingUnit,
            "calories" to foodEntry.calories,
            "protein" to foodEntry.protein,
            "carbs" to foodEntry.carbs,
            "fat" to foodEntry.fat,
            "fiber" to foodEntry.fiber,
            "sugar" to foodEntry.sugar,
            "mealType" to foodEntry.mealType,
            "date" to foodEntry.date,
            "time" to foodEntry.time,
            "notes" to foodEntry.notes,
            "createdAt" to foodEntry.createdAt,
            "updatedAt" to foodEntry.updatedAt
        )

        // Add to Firestore and get ID
        val documentRef = userEntriesRef.add(entryMap).await()

        // Use document ID as the entry ID
        val entryId = documentRef.id.hashCode().toLong()
        documentRef.update("id", entryId).await()

        return entryId
    }

    override suspend fun updateFoodEntry(foodEntry: FoodEntry) {
        val userEntriesRef = getUserEntriesRef() ?: throw Exception("User not authenticated")

        // Find the document with the matching ID
        val query = userEntriesRef.whereEqualTo("id", foodEntry.id).limit(1).get().await()

        if (query.documents.isEmpty()) {
            throw Exception("Food entry not found")
        }

        val documentId = query.documents.first().id

        // Update the document
        userEntriesRef.document(documentId).update(
            mapOf(
                "name" to foodEntry.name,
                "servingSize" to foodEntry.servingSize,
                "servingUnit" to foodEntry.servingUnit,
                "calories" to foodEntry.calories,
                "protein" to foodEntry.protein,
                "carbs" to foodEntry.carbs,
                "fat" to foodEntry.fat,
                "fiber" to foodEntry.fiber,
                "sugar" to foodEntry.sugar,
                "mealType" to foodEntry.mealType,
                "date" to foodEntry.date,
                "time" to foodEntry.time,
                "notes" to foodEntry.notes,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
    }

    override suspend fun deleteFoodEntry(foodEntry: FoodEntry) {
        val userEntriesRef = getUserEntriesRef() ?: throw Exception("User not authenticated")

        // Find the document with the matching ID
        val query = userEntriesRef.whereEqualTo("id", foodEntry.id).limit(1).get().await()

        if (query.documents.isEmpty()) {
            throw Exception("Food entry not found")
        }

        val documentId = query.documents.first().id

        // Delete the document
        userEntriesRef.document(documentId).delete().await()
    }

    override suspend fun getFoodEntryById(id: Long): FoodEntry? {
        val userEntriesRef = getUserEntriesRef() ?: throw Exception("User not authenticated")

        // Find the document with the matching ID
        val query = userEntriesRef.whereEqualTo("id", id).limit(1).get().await()

        if (query.documents.isEmpty()) {
            return null
        }

        // Convert document to FoodEntry
        return try {
            val doc = query.documents.first()
            FoodEntry(
                id = doc.getLong("id") ?: 0,
                name = doc.getString("name") ?: "",
                servingSize = doc.getDouble("servingSize") ?: 0.0,
                servingUnit = doc.getString("servingUnit") ?: "",
                calories = doc.getDouble("calories") ?: 0.0,
                protein = doc.getDouble("protein") ?: 0.0,
                carbs = doc.getDouble("carbs") ?: 0.0,
                fat = doc.getDouble("fat") ?: 0.0,
                fiber = doc.getDouble("fiber") ?: 0.0,
                sugar = doc.getDouble("sugar") ?: 0.0,
                mealType = doc.getString("mealType") ?: MealType.SNACK.name,
                date = doc.getDate("date") ?: Date(),
                time = doc.getDate("time") ?: Date(),
                notes = doc.getString("notes"),
                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    override fun getAllFoodEntries(): Flow<List<FoodEntry>> = callbackFlow {
        val userEntriesRef = getUserEntriesRef()

        if (userEntriesRef == null) {
            trySend(emptyList())
            return@callbackFlow
        }

        // Listen for real-time updates
        val listenerRegistration = userEntriesRef
            .orderBy("date", Query.Direction.DESCENDING)
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val entries = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        FoodEntry(
                            id = doc.getLong("id") ?: 0,
                            name = doc.getString("name") ?: "",
                            servingSize = doc.getDouble("servingSize") ?: 0.0,
                            servingUnit = doc.getString("servingUnit") ?: "",
                            calories = doc.getDouble("calories") ?: 0.0,
                            protein = doc.getDouble("protein") ?: 0.0,
                            carbs = doc.getDouble("carbs") ?: 0.0,
                            fat = doc.getDouble("fat") ?: 0.0,
                            fiber = doc.getDouble("fiber") ?: 0.0,
                            sugar = doc.getDouble("sugar") ?: 0.0,
                            mealType = doc.getString("mealType") ?: MealType.SNACK.name,
                            date = doc.getDate("date") ?: Date(),
                            time = doc.getDate("time") ?: Date(),
                            notes = doc.getString("notes"),
                            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                            updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(entries)
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun getFoodEntriesByDate(date: Date): Flow<List<FoodEntry>> = callbackFlow {
        val userEntriesRef = getUserEntriesRef()

        if (userEntriesRef == null) {
            trySend(emptyList())
            return@callbackFlow
        }

        // Calculate start and end of the day
        val startOfDay = getStartOfDay(date)
        val endOfDay = getEndOfDay(date)

        // Listen for real-time updates
        val listenerRegistration = userEntriesRef
            .whereGreaterThanOrEqualTo("date", startOfDay)
            .whereLessThanOrEqualTo("date", endOfDay)
            .orderBy("date", Query.Direction.ASCENDING)
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val entries = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        FoodEntry(
                            id = doc.getLong("id") ?: 0,
                            name = doc.getString("name") ?: "",
                            servingSize = doc.getDouble("servingSize") ?: 0.0,
                            servingUnit = doc.getString("servingUnit") ?: "",
                            calories = doc.getDouble("calories") ?: 0.0,
                            protein = doc.getDouble("protein") ?: 0.0,
                            carbs = doc.getDouble("carbs") ?: 0.0,
                            fat = doc.getDouble("fat") ?: 0.0,
                            fiber = doc.getDouble("fiber") ?: 0.0,
                            sugar = doc.getDouble("sugar") ?: 0.0,
                            mealType = doc.getString("mealType") ?: MealType.SNACK.name,
                            date = doc.getDate("date") ?: Date(),
                            time = doc.getDate("time") ?: Date(),
                            notes = doc.getString("notes"),
                            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                            updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(entries)
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun getFoodEntriesByDateRange(startDate: Date, endDate: Date): Flow<List<FoodEntry>> = callbackFlow {
        val userEntriesRef = getUserEntriesRef()

        if (userEntriesRef == null) {
            trySend(emptyList())
            return@callbackFlow
        }

        // Listen for real-time updates
        val listenerRegistration = userEntriesRef
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .orderBy("date", Query.Direction.ASCENDING)
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val entries = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        FoodEntry(
                            id = doc.getLong("id") ?: 0,
                            name = doc.getString("name") ?: "",
                            servingSize = doc.getDouble("servingSize") ?: 0.0,
                            servingUnit = doc.getString("servingUnit") ?: "",
                            calories = doc.getDouble("calories") ?: 0.0,
                            protein = doc.getDouble("protein") ?: 0.0,
                            carbs = doc.getDouble("carbs") ?: 0.0,
                            fat = doc.getDouble("fat") ?: 0.0,
                            fiber = doc.getDouble("fiber") ?: 0.0,
                            sugar = doc.getDouble("sugar") ?: 0.0,
                            mealType = doc.getString("mealType") ?: MealType.SNACK.name,
                            date = doc.getDate("date") ?: Date(),
                            time = doc.getDate("time") ?: Date(),
                            notes = doc.getString("notes"),
                            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                            updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(entries)
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun getFoodEntriesByMealType(mealType: MealType): Flow<List<FoodEntry>> = callbackFlow {
        val userEntriesRef = getUserEntriesRef()

        if (userEntriesRef == null) {
            trySend(emptyList())
            return@callbackFlow
        }

        // Listen for real-time updates
        val listenerRegistration = userEntriesRef
            .whereEqualTo("mealType", mealType.name)
            .orderBy("date", Query.Direction.DESCENDING)
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val entries = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        FoodEntry(
                            id = doc.getLong("id") ?: 0,
                            name = doc.getString("name") ?: "",
                            servingSize = doc.getDouble("servingSize") ?: 0.0,
                            servingUnit = doc.getString("servingUnit") ?: "",
                            calories = doc.getDouble("calories") ?: 0.0,
                            protein = doc.getDouble("protein") ?: 0.0,
                            carbs = doc.getDouble("carbs") ?: 0.0,
                            fat = doc.getDouble("fat") ?: 0.0,
                            fiber = doc.getDouble("fiber") ?: 0.0,
                            sugar = doc.getDouble("sugar") ?: 0.0,
                            mealType = doc.getString("mealType") ?: MealType.SNACK.name,
                            date = doc.getDate("date") ?: Date(),
                            time = doc.getDate("time") ?: Date(),
                            notes = doc.getString("notes"),
                            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                            updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(entries)
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun getTotalCaloriesForDate(date: Date): Flow<Double> = flow {
        val userEntriesRef = getUserEntriesRef() ?: throw Exception("User not authenticated")

        // Calculate start and end of the day
        val startOfDay = getStartOfDay(date)
        val endOfDay = getEndOfDay(date)

        // Query entries for the date
        val query = userEntriesRef
            .whereGreaterThanOrEqualTo("date", startOfDay)
            .whereLessThanOrEqualTo("date", endOfDay)
            .get()
            .await()

        // Calculate total calories
        val totalCalories = query.documents.sumOf { doc ->
            doc.getDouble("calories") ?: 0.0
        }

        emit(totalCalories)
    }

    override fun getTotalNutrientsForDate(date: Date): Flow<Map<String, Double>> = flow {
        val userEntriesRef = getUserEntriesRef() ?: throw Exception("User not authenticated")

        // Calculate start and end of the day
        val startOfDay = getStartOfDay(date)
        val endOfDay = getEndOfDay(date)

        // Query entries for the date
        val query = userEntriesRef
            .whereGreaterThanOrEqualTo("date", startOfDay)
            .whereLessThanOrEqualTo("date", endOfDay)
            .get()
            .await()

        // Calculate total nutrients
        var totalProtein = 0.0
        var totalCarbs = 0.0
        var totalFat = 0.0

        query.documents.forEach { doc ->
            totalProtein += doc.getDouble("protein") ?: 0.0
            totalCarbs += doc.getDouble("carbs") ?: 0.0
            totalFat += doc.getDouble("fat") ?: 0.0
        }

        emit(mapOf(
            "protein" to totalProtein,
            "carbs" to totalCarbs,
            "fat" to totalFat
        ))
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