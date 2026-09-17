package com.closy.data.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val preferences: UserPreferences = UserPreferences()
)
