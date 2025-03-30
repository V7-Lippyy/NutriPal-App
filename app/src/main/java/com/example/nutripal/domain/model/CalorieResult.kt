package com.example.nutripal.domain.model

import com.example.nutripal.util.Constants

data class CalorieResult(
    val dailyCalories: Int,
    val bmr: Double,
    val gender: Gender,
    val age: Int,
    val height: Double,
    val currentWeight: Double,
    val targetWeight: Double,
    val targetWeeks: Int,
    val goal: WeightGoal
)

enum class WeightGoal(val displayName: String) {
    LOSE(Constants.GOAL_LOSE_WEIGHT),
    MAINTAIN(Constants.GOAL_MAINTAIN_WEIGHT),
    GAIN(Constants.GOAL_GAIN_WEIGHT);

    companion object {
        fun fromWeights(currentWeight: Double, targetWeight: Double): WeightGoal {
            return when {
                targetWeight < currentWeight -> LOSE
                targetWeight > currentWeight -> GAIN
                else -> MAINTAIN
            }
        }
    }
}