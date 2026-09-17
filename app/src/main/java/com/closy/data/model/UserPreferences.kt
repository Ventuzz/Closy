package com.closy.data.model

data class UserPreferences(
    val genderPreference: String = "",
    val stylePreferences: List<String> = emptyList(),
    val favoriteColors: List<String> = emptyList(),
    val size: String = "",
    val fitPreference: String = ""
)
