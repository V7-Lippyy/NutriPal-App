package com.example.nutripal.domain.usecase

import com.example.nutripal.domain.model.BMICategory
import com.example.nutripal.domain.model.BMIResult
import com.example.nutripal.domain.model.Gender
import javax.inject.Inject

class CalculateBMIUseCase @Inject constructor() {

    operator fun invoke(
        gender: Gender,
        age: Int,
        heightCm: Double,
        weightKg: Double
    ): BMIResult {
        // Konversi tinggi dari cm ke meter untuk perhitungan BMI
        val heightM = heightCm / 100

        // Rumus BMI: berat (kg) / (tinggi (m) * tinggi (m))
        val bmiValue = weightKg / (heightM * heightM)

        // Menentukan kategori BMI berdasarkan nilai
        val category = BMICategory.fromBMI(bmiValue)

        return BMIResult(
            bmiValue = bmiValue,
            category = category,
            gender = gender,
            age = age,
            height = heightCm,
            weight = weightKg
        )
    }
}