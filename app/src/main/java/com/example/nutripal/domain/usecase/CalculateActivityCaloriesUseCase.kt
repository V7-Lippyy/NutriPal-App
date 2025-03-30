package com.example.nutripal.domain.usecase

import com.example.nutripal.domain.model.ActivityResult
import com.example.nutripal.domain.model.PhysicalActivity
import javax.inject.Inject
import kotlin.math.roundToInt

class CalculateActivityCaloriesUseCase @Inject constructor() {

    operator fun invoke(
        activity: PhysicalActivity,
        durationMinutes: Int,
        weightKg: Double
    ): ActivityResult {
        // Estimasi kalori yang terbakar berdasarkan MET (Metabolic Equivalent of Task)
        // Rumus: Kalori = MET * 3.5 * berat badan (kg) * durasi (jam) / 200
        // Di sini kita menggunakan caloriesPerHour sebagai dasar

        // Konversi menit ke jam
        val durationHours = durationMinutes / 60.0

        // Hitung kalori yang terbakar, disesuaikan dengan berat badan
        // Faktor 70 adalah berat badan referensi (kg) untuk nilai caloriesPerHour
        val caloriesBurned = activity.caloriesPerHour * (weightKg / 70.0) * durationHours

        return ActivityResult(
            activity = activity,
            durationMinutes = durationMinutes,
            caloriesBurned = caloriesBurned
        )
    }
}