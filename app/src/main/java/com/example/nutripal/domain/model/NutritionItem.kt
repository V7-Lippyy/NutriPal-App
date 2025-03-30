package com.example.nutripal.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NutritionResponse(
    @Json(name = "items")
    val items: List<NutritionItem>
)

@JsonClass(generateAdapter = true)
data class NutritionItem(
    @Json(name = "name")
    val name: String,

    @Json(name = "calories")
    val calories: Double,

    @Json(name = "serving_size_g")
    val servingSizeGram: Double,

    @Json(name = "fat_total_g")
    val totalFatGram: Double,

    @Json(name = "fat_saturated_g")
    val saturatedFatGram: Double,

    @Json(name = "protein_g")
    val proteinGram: Double,

    @Json(name = "sodium_mg")
    val sodiumMilligram: Double,

    @Json(name = "potassium_mg")
    val potassiumMilligram: Double,

    @Json(name = "cholesterol_mg")
    val cholesterolMilligram: Double,

    @Json(name = "carbohydrates_total_g")
    val totalCarbohydratesGram: Double,

    @Json(name = "fiber_g")
    val fiberGram: Double,

    @Json(name = "sugar_g")
    val sugarGram: Double
)