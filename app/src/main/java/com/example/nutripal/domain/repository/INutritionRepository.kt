package com.example.nutripal.domain.repository

import com.example.nutripal.domain.model.NutritionItem
import kotlinx.coroutines.flow.Flow

interface INutritionRepository {
    fun getNutritionInfo(query: String): Flow<List<NutritionItem>>
}