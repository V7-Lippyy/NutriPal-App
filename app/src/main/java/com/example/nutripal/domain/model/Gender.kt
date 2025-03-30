package com.example.nutripal.domain.model

import com.example.nutripal.util.Constants

enum class Gender(val displayName: String) {
    MALE(Constants.GENDER_MALE),
    FEMALE(Constants.GENDER_FEMALE);

    companion object {
        fun fromString(value: String): Gender {
            return when (value) {
                Constants.GENDER_MALE -> MALE
                Constants.GENDER_FEMALE -> FEMALE
                else -> MALE
            }
        }
    }
}