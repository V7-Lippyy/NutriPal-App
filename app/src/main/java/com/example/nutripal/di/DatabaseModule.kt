package com.example.nutripal.di

import android.content.Context
import androidx.room.Room
import com.example.nutripal.data.local.dao.FoodEntryDao
import com.example.nutripal.data.local.database.NutriPalDatabase
import com.example.nutripal.data.repository.FoodEntryRepository
import com.example.nutripal.domain.repository.IFoodEntryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NutriPalDatabase {
        return Room.databaseBuilder(
            context,
            NutriPalDatabase::class.java,
            "nutripal_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideFoodEntryDao(database: NutriPalDatabase): FoodEntryDao {
        return database.foodEntryDao()
    }

    @Provides
    @Singleton
    @Named("LocalRepository")
    fun provideFoodEntryRepository(foodEntryDao: FoodEntryDao): IFoodEntryRepository {
        return FoodEntryRepository(foodEntryDao)
    }
}
