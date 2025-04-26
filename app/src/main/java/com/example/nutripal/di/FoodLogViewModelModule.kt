package com.example.nutripal.di

import android.content.Context
import com.example.nutripal.data.remote.api.NutritionApiService
import com.example.nutripal.domain.repository.IFoodEntryRepository
import com.example.nutripal.ui.feature.foodlog.FoodLogViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object FoodLogViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideFoodLogViewModel(
        @Named("FirebaseRepository") repository: IFoodEntryRepository,
        nutritionApiService: NutritionApiService,
        apiKey: String,
        @ApplicationContext context: Context
    ): FoodLogViewModel {
        return FoodLogViewModel(
            repository = repository,
            nutritionApiService = nutritionApiService,
            apiKey = apiKey,
            context = context
        )
    }
}
