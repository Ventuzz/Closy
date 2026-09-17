package com.closy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closy.data.model.Outfit
import com.closy.data.repository.AuthRepository
import com.closy.data.repository.OutfitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val outfitRepository: OutfitRepository = OutfitRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Observe current user preference if set
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                val genderPref = user?.preferences?.genderPreference
                if (!genderPref.isNullOrBlank()) {
                    _uiState.update { it.copy(activeGenderPreference = genderPref) }
                }
                loadOutfits()
            }
        }

        // Observe favorite IDs changes
        viewModelScope.launch {
            outfitRepository.favoriteOutfitIds.collect {
                loadOutfits()
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadOutfits()
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadOutfits()
    }

    fun onGenderPreferenceSelected(gender: String) {
        _uiState.update { it.copy(activeGenderPreference = gender) }
        loadOutfits()
        viewModelScope.launch {
            authRepository.saveGenderPreference(gender)
        }
    }

    fun onSegmentTabSelected(index: Int) {
        _uiState.update { it.copy(selectedSegmentTab = index) }
        loadOutfits()
    }

    fun onBottomTabSelected(index: Int) {
        _uiState.update { it.copy(selectedBottomTab = index) }
    }

    fun toggleFavorite(outfitId: String) {
        outfitRepository.toggleFavorite(outfitId)
        // Check if selected detail outfit was toggled
        val currentDetail = _uiState.value.selectedOutfitForDetail
        if (currentDetail?.id == outfitId) {
            _uiState.update { state ->
                state.copy(
                    selectedOutfitForDetail = currentDetail.copy(isSaved = !currentDetail.isSaved)
                )
            }
        }
    }

    fun selectOutfitForDetail(outfit: Outfit?) {
        _uiState.update { it.copy(selectedOutfitForDetail = outfit) }
    }

    fun loadOutfits() {
        val currentState = _uiState.value
        val filtered = outfitRepository.getOutfits(
            genderPreference = currentState.activeGenderPreference,
            searchQuery = currentState.searchQuery,
            categoryFilter = currentState.selectedCategory,
            savedOnly = (currentState.selectedSegmentTab == 1)
        )
        _uiState.update { it.copy(outfits = filtered) }
    }
}
