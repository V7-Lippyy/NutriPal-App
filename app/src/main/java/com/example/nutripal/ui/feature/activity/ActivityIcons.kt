package com.example.nutripal.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.DirectionsBike
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Activity icons using Material 3
 */
object ActivityIcons {
    // Map activity ID to Material icon
    val activityIconMap: Map<String, ImageVector> = mapOf(
        "running" to Icons.Outlined.DirectionsRun,
        "walking" to Icons.Outlined.DirectionsWalk,
        "cycling" to Icons.Outlined.DirectionsBike,
        "swimming" to Icons.Outlined.Pool,
        "yoga" to Icons.Outlined.SelfImprovement,
        "weight_training" to Icons.Outlined.FitnessCenter,
        "hiit" to Icons.Outlined.LocalFireDepartment,
        "dancing" to Icons.Outlined.Celebration,
        "basketball" to Icons.Outlined.SportsBasketball,
        "football" to Icons.Outlined.SportsSoccer
    )

    fun getIconForActivity(activityId: String): ImageVector {
        return activityIconMap[activityId] ?: Icons.Outlined.DirectionsRun
    }
}