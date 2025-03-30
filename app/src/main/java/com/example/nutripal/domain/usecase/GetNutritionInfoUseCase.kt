package com.example.nutripal.domain.usecase

import com.example.nutripal.domain.model.NutritionItem
import com.example.nutripal.domain.repository.INutritionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNutritionInfoUseCase @Inject constructor(
    private val nutritionRepository: INutritionRepository
) {

    operator fun invoke(query: String): Flow<List<NutritionItem>> {
        return nutritionRepository.getNutritionInfo(query)
    }
}