package com.example.nutripal.domain.model

enum class MealType(val displayName: String) {
    BREAKFAST("Sarapan"),
    LUNCH("Makan Siang"),
    DINNER("Makan Malam"),
    SNACK("Cemilan");

    companion object {
        fun fromString(value: String): MealType {
            return values().find { it.name == value } ?: SNACK
        }
    }
}