package com.example.nutripal.domain.model

data class User(
    val userId: String = "",
    val email: String = "",
    val username: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)