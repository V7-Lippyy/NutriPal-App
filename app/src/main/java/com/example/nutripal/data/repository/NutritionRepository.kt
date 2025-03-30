package com.example.nutripal.data.repository

import com.example.nutripal.data.remote.api.NutritionApiService
import com.example.nutripal.domain.model.NutritionItem
import com.example.nutripal.domain.repository.INutritionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NutritionRepository @Inject constructor(
    private val apiService: NutritionApiService,
    private val apiKey: String
) : INutritionRepository {

    override fun getNutritionInfo(query: String): Flow<List<NutritionItem>> = flow {
        try {
            val response = apiService.getNutritionInfo(apiKey, query)
            emit(response.items)
        } catch (e: Exception) {
            // In a real application, you would handle errors better
            emit(emptyList())
        }
    }
}