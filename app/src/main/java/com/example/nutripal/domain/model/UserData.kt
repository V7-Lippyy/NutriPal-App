package com.example.nutripal.domain.model

/**
 * Model data untuk menyimpan informasi pengguna
 */
data class UserData(
    val name: String = "",
    val hasCompletedOnboarding: Boolean = false
)