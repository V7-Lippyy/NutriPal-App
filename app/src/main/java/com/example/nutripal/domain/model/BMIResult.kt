package com.example.nutripal.domain.model

import com.example.nutripal.util.Constants

data class BMIResult(
    val bmiValue: Double,
    val category: BMICategory,
    val gender: Gender,
    val age: Int,
    val height: Double,
    val weight: Double
)

enum class BMICategory(val displayName: String) {
    UNDERWEIGHT("Kurus"),
    NORMAL("Normal"),
    OVERWEIGHT("Gemuk"),
    OBESE("Obesitas");

    companion object {
        fun fromBMI(bmi: Double): BMICategory {
            return when {
                bmi < Constants.UNDERWEIGHT_THRESHOLD -> UNDERWEIGHT
                bmi < Constants.NORMAL_WEIGHT_THRESHOLD -> NORMAL
                bmi < Constants.OVERWEIGHT_THRESHOLD -> OVERWEIGHT
                else -> OBESE
            }
        }
    }
}