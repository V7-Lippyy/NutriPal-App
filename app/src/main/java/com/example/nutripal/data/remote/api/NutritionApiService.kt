package com.example.nutripal.data.remote.api

import com.example.nutripal.domain.model.NutritionResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NutritionApiService {
    @GET("nutrition")
    suspend fun getNutritionInfo(
        @Header("X-Api-Key") apiKey: String,
        @Query("query") query: String
    ): NutritionResponse
}