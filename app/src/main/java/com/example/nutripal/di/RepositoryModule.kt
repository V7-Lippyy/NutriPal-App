package com.example.nutripal.di

import com.example.nutripal.data.repository.NutritionRepository
import com.example.nutripal.domain.repository.INutritionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNutritionRepository(
        nutritionRepository: NutritionRepository
    ): INutritionRepository
}