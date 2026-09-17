package com.closy.ui.personalization

data class PersonalizationState(
    val selectedGender: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)
