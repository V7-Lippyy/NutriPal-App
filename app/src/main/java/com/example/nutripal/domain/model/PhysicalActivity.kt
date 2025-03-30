package com.example.nutripal.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class PhysicalActivity(
    val id: String,
    val name: String,
    val caloriesPerHour: Double,
    val icon: ImageVector? = null
)

data class ActivityResult(
    val activity: PhysicalActivity,
    val durationMinutes: Int,
    val caloriesBurned: Double
)