package com.example.nutripal.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nutripal.R
import com.example.nutripal.util.Constants

sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector? = null
) {
    object Splash : Screen(Constants.ROUTE_SPLASH, R.string.splash)

    object Onboarding : Screen(Constants.ROUTE_ONBOARDING, R.string.onboarding)

    object Home : Screen(
        Constants.ROUTE_HOME,
        R.string.home,
        Icons.Outlined.Home
    )

    object BMI : Screen(
        Constants.ROUTE_BMI,
        R.string.bmi,
        Icons.Outlined.Calculate
    )

    object DailyCalorie : Screen(
        Constants.ROUTE_DAILY_CALORIE,
        R.string.daily_calorie,
        Icons.Outlined.MonitorWeight
    )

    object PhysicalActivity : Screen(
        Constants.ROUTE_PHYSICAL_ACTIVITY,
        R.string.physical_activity,
        Icons.Outlined.FitnessCenter
    )

    object Nutrition : Screen(
        Constants.ROUTE_NUTRITION,
        R.string.nutrition,
        Icons.Outlined.Restaurant
    )

    object FoodLog : Screen(
        Constants.ROUTE_FOOD_LOG,
        R.string.food_log,
        Icons.Outlined.MenuBook
    )

    object FoodLogAdd : Screen(
        Constants.ROUTE_FOOD_LOG_ADD,
        R.string.food_log_add
    )

    object FoodLogEdit : Screen(
        Constants.ROUTE_FOOD_LOG_EDIT,
        R.string.food_log_edit
    )

    companion object {
        // Daftar item navigasi yang akan ditampilkan di bottom bar
        val bottomNavItems = listOf(
            Home, BMI, DailyCalorie, PhysicalActivity, Nutrition, FoodLog
        )
    }
}