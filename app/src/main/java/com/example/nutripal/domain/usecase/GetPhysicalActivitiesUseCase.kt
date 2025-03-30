package com.example.nutripal.domain.usecase

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nutripal.domain.model.PhysicalActivity
import javax.inject.Inject

class GetPhysicalActivitiesUseCase @Inject constructor() {

    operator fun invoke(): List<PhysicalActivity> {
        // Daftar aktivitas fisik dengan perkiraan kalori yang terbakar per jam untuk orang dengan berat 70kg
        return listOf(
            PhysicalActivity(
                id = "running",
                name = "Berlari",
                caloriesPerHour = 700.0,
                icon = Icons.Filled.DirectionsRun
            ),
            PhysicalActivity(
                id = "walking",
                name = "Berjalan",
                caloriesPerHour = 280.0,
                icon = Icons.Filled.DirectionsWalk
            ),
            PhysicalActivity(
                id = "cycling",
                name = "Bersepeda",
                caloriesPerHour = 500.0,
                icon = Icons.Filled.DirectionsBike
            ),
            PhysicalActivity(
                id = "swimming",
                name = "Berenang",
                caloriesPerHour = 550.0,
                icon = Icons.Filled.Pool
            ),
            PhysicalActivity(
                id = "yoga",
                name = "Yoga",
                caloriesPerHour = 250.0,
                icon = Icons.Filled.SelfImprovement
            ),
            PhysicalActivity(
                id = "weight_training",
                name = "Angkat Beban",
                caloriesPerHour = 450.0,
                icon = Icons.Filled.FitnessCenter
            ),
            PhysicalActivity(
                id = "hiit",
                name = "HIIT",
                caloriesPerHour = 750.0,
                icon = Icons.Filled.LocalFireDepartment
            ),
            PhysicalActivity(
                id = "dancing",
                name = "Menari",
                caloriesPerHour = 400.0,
                icon = Icons.Filled.Celebration
            ),
            PhysicalActivity(
                id = "basketball",
                name = "Basket",
                caloriesPerHour = 580.0,
                icon = Icons.Filled.SportsBasketball
            ),
            PhysicalActivity(
                id = "football",
                name = "Sepak Bola",
                caloriesPerHour = 600.0,
                icon = Icons.Filled.SportsSoccer
            )
        )
    }
}