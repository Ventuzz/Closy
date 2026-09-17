package com.closy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closy.data.model.Outfit
import com.closy.data.db.ClosetItemEntity
import com.closy.data.repository.AuthRepository
import com.closy.data.repository.ClosetRepository
import com.closy.data.repository.OutfitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val outfitRepository: OutfitRepository = OutfitRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val closetRepository: ClosetRepository = ClosetRepository()
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
                _uiState.update {
                    it.copy(
                        userName = user?.name ?: "Invitado",
                        userEmail = user?.email ?: "invitado@closy.app"
                    )
                }
                loadCloset()
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
        if (index == 1) loadCloset()
    }

    fun resetBottomTab() {
        _uiState.update { it.copy(selectedBottomTab = 0) }
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
            savedOnly = (currentState.selectedSegmentTab == 1),
            ignoreGenderForSaved = true
        )
        val savedCount = outfitRepository.getOutfits(
            genderPreference = "Todos",
            savedOnly = true,
            ignoreGenderForSaved = true
        ).size
        _uiState.update { it.copy(outfits = filtered, savedOutfitCount = savedCount) }
    }

    fun saveClosetItem(
        editing: ClosetItemEntity?,
        name: String,
        category: String,
        color: String,
        season: String,
        notes: String
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val email = _uiState.value.userEmail
            if (editing == null) {
                closetRepository.create(
                    ClosetItemEntity(
                        userEmail = email,
                        name = name.trim(),
                        category = category,
                        color = color.trim().ifBlank { "Sin especificar" },
                        season = season,
                        notes = notes.trim()
                    )
                )
            } else {
                closetRepository.update(
                    editing.copy(
                        name = name.trim(), category = category,
                        color = color.trim().ifBlank { "Sin especificar" },
                        season = season, notes = notes.trim()
                    )
                )
            }
            loadCloset()
        }
    }

    fun deleteClosetItem(item: ClosetItemEntity) {
        viewModelScope.launch {
            closetRepository.delete(item)
            loadCloset()
        }
    }

    fun loadCloset() {
        val email = _uiState.value.userEmail
        viewModelScope.launch {
            _uiState.update { it.copy(closetItems = closetRepository.getItems(email)) }
        }
    }

    fun generateRecommendation(occasion: String) {
        val state = _uiState.value
        val category = when (occasion) {
            "Trabajo", "Evento" -> "Formal"
            "Fin de semana" -> "Casual"
            else -> "Todos"
        }
        val candidates = outfitRepository.getOutfits(
            genderPreference = state.activeGenderPreference,
            categoryFilter = category
        ).ifEmpty {
            outfitRepository.getOutfits(genderPreference = state.activeGenderPreference)
        }
        val selected = candidates.randomOrNull()
        val closetHint = state.closetItems.take(3).joinToString { it.name }
        val reason = when {
            selected == null -> "Agrega más prendas o cambia tus preferencias para generar una combinación."
            closetHint.isBlank() -> "Elegido para $occasion según tu estilo ${state.activeGenderPreference.lowercase()}. Agrega prendas al closet para personalizarlo más."
            else -> "Para $occasion, combina esta idea con prendas de tu closet como: $closetHint."
        }
        _uiState.update { it.copy(recommendation = selected, recommendationReason = reason) }
    }
}
