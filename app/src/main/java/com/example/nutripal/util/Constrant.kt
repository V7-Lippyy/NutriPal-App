package com.example.nutripal.util

object Constants {
    // API Constants
    const val BASE_URL = "https://api.calorieninjas.com/v1/"
    const val API_KEY_HEADER = "X-Api-Key"

    // BMI Constants
    const val UNDERWEIGHT_THRESHOLD = 18.5
    const val NORMAL_WEIGHT_THRESHOLD = 24.9
    const val OVERWEIGHT_THRESHOLD = 29.9
    // Above 30 is considered obese

    // Gender Constants
    const val GENDER_MALE = "Laki-laki"
    const val GENDER_FEMALE = "Perempuan"

    // Physical Activity Constants
    const val ACTIVITY_SEDENTARY = 1.2
    const val ACTIVITY_LIGHT = 1.375
    const val ACTIVITY_MODERATE = 1.55
    const val ACTIVITY_ACTIVE = 1.725
    const val ACTIVITY_VERY_ACTIVE = 1.9

    // Calorie Goals Constants
    const val GOAL_LOSE_WEIGHT = "Menurunkan Berat Badan"
    const val GOAL_MAINTAIN_WEIGHT = "Mempertahankan Berat Badan"
    const val GOAL_GAIN_WEIGHT = "Menaikkan Berat Badan"

    // Navigation Routes
    const val ROUTE_SPLASH = "splash"
    const val ROUTE_ONBOARDING = "onboarding" // Tambahkan rute onboarding
    const val ROUTE_HOME = "home"
    const val ROUTE_BMI = "bmi"
    const val ROUTE_DAILY_CALORIE = "daily_calorie"
    const val ROUTE_PHYSICAL_ACTIVITY = "physical_activity"
    const val ROUTE_NUTRITION = "nutrition"
    const val ROUTE_FOOD_LOG = "food_log"
    const val ROUTE_FOOD_LOG_ADD = "food_log_add"
    const val ROUTE_FOOD_LOG_EDIT = "food_log_edit"
}