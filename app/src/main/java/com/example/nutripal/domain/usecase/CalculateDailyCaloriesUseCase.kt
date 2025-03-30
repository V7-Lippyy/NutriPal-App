package com.example.nutripal.domain.usecase

import com.example.nutripal.domain.model.CalorieResult
import com.example.nutripal.domain.model.Gender
import com.example.nutripal.domain.model.WeightGoal
import com.example.nutripal.util.Constants
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

class CalculateDailyCaloriesUseCase @Inject constructor() {

    operator fun invoke(
        gender: Gender,
        age: Int,
        heightCm: Double,
        currentWeightKg: Double,
        targetWeightKg: Double,
        targetWeeks: Int,
        activityLevel: Double = Constants.ACTIVITY_MODERATE
    ): CalorieResult {
        // Hitung Basal Metabolic Rate (BMR) menggunakan rumus Harris-Benedict
        val bmr = when (gender) {
            Gender.MALE -> {
                // Untuk laki-laki: BMR = 88.362 + (13.397 × berat dalam kg) + (4.799 × tinggi dalam cm) - (5.677 × usia dalam tahun)
                88.362 + (13.397 * currentWeightKg) + (4.799 * heightCm) - (5.677 * age)
            }
            Gender.FEMALE -> {
                // Untuk perempuan: BMR = 447.593 + (9.247 × berat dalam kg) + (3.098 × tinggi dalam cm) - (4.330 × usia dalam tahun)
                447.593 + (9.247 * currentWeightKg) + (3.098 * heightCm) - (4.330 * age)
            }
        }

        // Hitung Total Daily Energy Expenditure (TDEE) berdasarkan level aktivitas
        val tdee = bmr * activityLevel

        // Hitung defisit/surplus kalori harian yang dibutuhkan berdasarkan target
        // 1 kg lemak ~= 7700 kalori
        val weightDifferenceKg = targetWeightKg - currentWeightKg
        val totalCalorieDifference = weightDifferenceKg * 7700
        val dailyCalorieDifference = totalCalorieDifference / (targetWeeks * 7)

        // Hitung kebutuhan kalori harian
        val dailyCalories = (tdee + dailyCalorieDifference).roundToInt()

        // Identifikasi tujuan berdasarkan berat saat ini dan target
        val goal = WeightGoal.fromWeights(currentWeightKg, targetWeightKg)

        return CalorieResult(
            dailyCalories = dailyCalories,
            bmr = bmr,
            gender = gender,
            age = age,
            height = heightCm,
            currentWeight = currentWeightKg,
            targetWeight = targetWeightKg,
            targetWeeks = targetWeeks,
            goal = goal
        )
    }
}