package com.closy.ui.personalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closy.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PersonalizationViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalizationState())
    val uiState: StateFlow<PersonalizationState> = _uiState.asStateFlow()

    fun selectGender(gender: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedGender = gender, isLoading = true, errorMessage = null) }
            val result = repository.saveGenderPreference(gender)
            result.fold(
                onSuccess = {
                    _uiState.update { state -> state.copy(isLoading = false, isSaved = true) }
                    onComplete()
                },
                onFailure = { error ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Error al guardar preferencia"
                        )
                    }
                }
            )
        }
    }
}
