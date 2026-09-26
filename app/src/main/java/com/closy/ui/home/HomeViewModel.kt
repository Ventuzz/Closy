package com.closy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closy.data.db.ClosetGarmentEntity
import com.closy.data.db.ClosetItemEntity
import com.closy.data.model.Outfit
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

    private var previousUserEmail: String? = null

    init {
        // Observe current user preference if set
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                val newEmail = user?.email ?: "guest@closy.com"
                if ((previousUserEmail != null && previousUserEmail != newEmail) || user?.id == "guest_user") {
                    resetSessionState()
                }
                previousUserEmail = newEmail

                val genderPref = user?.preferences?.genderPreference
                if (!genderPref.isNullOrBlank()) {
                    _uiState.update { it.copy(activeGenderPreference = genderPref) }
                }
                _uiState.update {
                    it.copy(
                        userName = user?.name ?: "Invitado",
                        userEmail = newEmail
                    )
                }
                loadCloset()
            }
        }

        // Observe favorite IDs changes
        viewModelScope.launch {
            outfitRepository.favoriteOutfitIds.collect {
                loadOutfits()
            }
        }
    }

    fun resetSessionState() {
        _uiState.update { state ->
            state.copy(
                searchQuery = "",
                selectedCategory = "Todos",
                selectedClosetCategory = "Todos",
                selectedOutfitForDetail = null,
                selectedSegmentTab = 0,
                selectedBottomTab = 0,
                selectedGeneratorGarment = null,
                generatedCombinationOutfits = emptyList(),
                recommendation = null,
                recommendationReason = "",
                selectedStyleTags = emptySet(),
                pendingDeleteOutfit = null,
                pendingDeleteClosetItem = null,
                pendingDeleteGarment = null,
                deleteConfirmationState = null
            )
        }
    }

    fun logout() {
        resetSessionState()
        authRepository.logout()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadOutfits()
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadOutfits()
    }

    fun toggleStyleTag(tag: String) {
        _uiState.update { state ->
            val current = state.selectedStyleTags
            val updated = if (current.contains(tag)) current - tag else current + tag
            state.copy(selectedStyleTags = updated)
        }
    }

    fun selectStyleTag(tag: String) {
        _uiState.update { it.copy(selectedStyleTags = setOf(tag)) }
    }

    fun clearStyleTags() {
        _uiState.update { it.copy(selectedStyleTags = emptySet()) }
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
        if (index == 1 || index == 2) loadCloset()
    }

    fun resetBottomTab() {
        _uiState.update { it.copy(selectedBottomTab = 0) }
    }

    fun toggleFavorite(outfitId: String) {
        val currentEmail = _uiState.value.userEmail
        outfitRepository.toggleFavorite(outfitId, userEmail = currentEmail)
        loadOutfits()
    }

    fun selectOutfitForDetail(outfit: Outfit?) {
        _uiState.update { it.copy(selectedOutfitForDetail = outfit) }
    }

    fun loadOutfits() {
        val currentState = _uiState.value
        val favoriteIds = outfitRepository.favoriteOutfitIds.value
        val filtered = outfitRepository.getOutfits(
            genderPreference = currentState.activeGenderPreference,
            searchQuery = currentState.searchQuery,
            categoryFilter = currentState.selectedCategory,
            savedOnly = (currentState.selectedSegmentTab == 1),
            ignoreGenderForSaved = true,
            userEmail = currentState.userEmail,
            closetGarments = currentState.closetGarments
        )
        val savedCount = outfitRepository.getOutfits(
            genderPreference = "Todos",
            savedOnly = true,
            ignoreGenderForSaved = true,
            userEmail = currentState.userEmail
        ).size

        val updatedGenerated = currentState.generatedCombinationOutfits.map { outfit ->
            val isFav = favoriteIds.contains(outfit.id)
            outfit.copy(isSaved = isFav, isFavorite = isFav)
        }

        val updatedRecommendation = currentState.recommendation?.let { outfit ->
            val isFav = favoriteIds.contains(outfit.id)
            outfit.copy(isSaved = isFav, isFavorite = isFav)
        }

        val updatedDetail = currentState.selectedOutfitForDetail?.let { outfit ->
            val isFav = favoriteIds.contains(outfit.id)
            outfit.copy(isSaved = isFav, isFavorite = isFav)
        }

        _uiState.update {
            it.copy(
                outfits = filtered,
                savedOutfitCount = savedCount,
                generatedCombinationOutfits = updatedGenerated,
                recommendation = updatedRecommendation,
                selectedOutfitForDetail = updatedDetail
            )
        }
    }

    fun saveClosetItem(
        editing: ClosetItemEntity?,
        name: String,
        category: String,
        color: String,
        season: String,
        notes: String,
        imageUri: String,
        size: String
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
                        color = normalizeHexColor(color),
                        season = season,
                        notes = notes.trim(),
                        imageUri = imageUri,
                        size = size
                    )
                )
            } else {
                closetRepository.update(
                    editing.copy(
                        name = name.trim(), category = category,
                        color = normalizeHexColor(color),
                        season = season, notes = notes.trim(), imageUri = imageUri, size = size
                    )
                )
            }
            loadCloset()
        }
    }

    private fun normalizeHexColor(value: String): String {
        val clean = value.trim().removePrefix("#").uppercase()
        return if (clean.matches(Regex("[0-9A-F]{6}"))) "#$clean" else ""
    }

    fun deleteClosetItem(item: ClosetItemEntity) {
        viewModelScope.launch {
            closetRepository.delete(item)
            loadCloset()
        }
    }

    fun onClosetCategorySelected(category: String) {
        _uiState.update { it.copy(selectedClosetCategory = category) }
    }

    fun addGarment(
        name: String,
        category: String,
        color: String,
        imageUrl: String,
        styleTag: String
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val email = _uiState.value.userEmail
            val garment = ClosetGarmentEntity(
                id = "garment_${System.currentTimeMillis()}_${(1000..9999).random()}",
                userEmail = email,
                name = name.trim(),
                category = category,
                color = normalizeHexColor(color),
                imageUrl = imageUrl.trim(),
                styleTag = styleTag.ifBlank { "General" }
            )
            closetRepository.addGarment(garment)
            loadCloset()
        }
    }

    fun addGarment(garment: ClosetGarmentEntity) {
        viewModelScope.launch {
            closetRepository.addGarment(garment)
            loadCloset()
        }
    }

    fun updateGarment(
        garmentId: String,
        name: String,
        category: String,
        color: String,
        imageUrl: String,
        styleTag: String
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val email = _uiState.value.userEmail
            val garment = ClosetGarmentEntity(
                id = garmentId,
                userEmail = email,
                name = name.trim(),
                category = category,
                color = normalizeHexColor(color),
                imageUrl = imageUrl.trim(),
                styleTag = styleTag.ifBlank { "General" }
            )
            closetRepository.updateGarment(garment)
            loadCloset()
        }
    }

    fun updateGarment(garment: ClosetGarmentEntity) {
        viewModelScope.launch {
            closetRepository.updateGarment(garment)
            loadCloset()
        }
    }

    fun deleteGarment(garmentId: String) {
        viewModelScope.launch {
            val email = _uiState.value.userEmail
            closetRepository.deleteGarment(garmentId, email)
            loadCloset()
        }
    }

    fun deleteGarment(garment: ClosetGarmentEntity) {
        deleteGarment(garment.id)
    }

    fun requestDeleteGarment(garment: ClosetGarmentEntity) {
        _uiState.update {
            it.copy(
                pendingDeleteGarment = garment,
                deleteConfirmationState = DeleteConfirmationState(
                    title = "¿Eliminar prenda del closet?",
                    body = "¿Estás seguro/a de que deseas eliminar la prenda \"${garment.name}\" de tu closet? Esta acción no se puede deshacer.",
                    confirmButtonText = "Eliminar prenda"
                )
            )
        }
    }

    fun requestUnsaveOutfit(outfit: Outfit) {
        _uiState.update {
            it.copy(
                pendingDeleteOutfit = outfit,
                deleteConfirmationState = DeleteConfirmationState(
                    title = "¿Eliminar outfit de favoritos?",
                    body = "¿Estás seguro/a de que deseas eliminar el outfit \"${outfit.title}\" de tus favoritos? Esta acción no se puede deshacer.",
                    confirmButtonText = "Eliminar de favoritos",
                    dismissButtonText = "Cancelar"
                )
            )
        }
    }

    fun requestDeleteClosetItem(item: ClosetItemEntity) {
        _uiState.update {
            it.copy(
                pendingDeleteClosetItem = item,
                deleteConfirmationState = DeleteConfirmationState(
                    title = "¿Eliminar prenda del closet?",
                    body = "¿Estás seguro/a de que deseas eliminar la prenda \"${item.name}\" de tu closet? Esta acción no se puede deshacer.",
                    confirmButtonText = "Eliminar prenda"
                )
            )
        }
    }

    fun cancelDeletion() {
        _uiState.update {
            it.copy(
                pendingDeleteOutfit = null,
                pendingDeleteClosetItem = null,
                pendingDeleteGarment = null,
                deleteConfirmationState = null
            )
        }
    }

    fun confirmDeletion() {
        val state = _uiState.value
        state.pendingDeleteOutfit?.let { outfit ->
            toggleFavorite(outfit.id)
        }
        state.pendingDeleteClosetItem?.let { item ->
            deleteClosetItem(item)
        }
        state.pendingDeleteGarment?.let { garment ->
            deleteGarment(garment.id)
        }
        _uiState.update {
            it.copy(
                pendingDeleteOutfit = null,
                pendingDeleteClosetItem = null,
                pendingDeleteGarment = null,
                deleteConfirmationState = null
            )
        }
    }

    fun loadCloset() {
        val email = _uiState.value.userEmail
        viewModelScope.launch {
            val garments = closetRepository.getGarmentsForUser(email)
            val items = closetRepository.getItems(email)
            val currentSelected = _uiState.value.selectedGeneratorGarment
            val newSelected = if (currentSelected != null && garments.any { it.id == currentSelected.id }) {
                currentSelected
            } else {
                null
            }
            _uiState.update {
                it.copy(
                    closetGarments = garments,
                    closetItems = items,
                    selectedGeneratorGarment = newSelected
                )
            }
            loadOutfits()
        }
    }

    fun selectGeneratorGarment(garment: ClosetGarmentEntity?) {
        _uiState.update { state ->
            val updated = if (garment != null && state.selectedGeneratorGarment?.id == garment.id) null else garment
            state.copy(selectedGeneratorGarment = updated)
        }
    }

    fun pickRandomGeneratorGarment() {
        val garments = _uiState.value.closetGarments
        if (garments.isNotEmpty()) {
            val randomGarment = garments.random()
            _uiState.update { it.copy(selectedGeneratorGarment = randomGarment) }
        }
    }

    fun generateOutfitCombination(occasion: String = "Diario") {
        val state = _uiState.value
        val garment = state.selectedGeneratorGarment
        val selectedTags = state.selectedStyleTags

        val allOutfits = outfitRepository.getOutfits(
            genderPreference = state.activeGenderPreference,
            userEmail = state.userEmail,
            closetGarments = state.closetGarments
        )

        val matched = allOutfits.filter { outfit ->
            val tagMatch = selectedTags.isEmpty() || selectedTags.any { tag ->
                outfit.styleCategory.contains(tag, ignoreCase = true) ||
                        outfit.tags.any { t -> t.contains(tag, ignoreCase = true) } ||
                        outfit.hashtags.any { h -> h.contains(tag, ignoreCase = true) }
            }

            val garmentMatch = garment == null || outfit.garments.any { g ->
                g.name.contains(garment.name, ignoreCase = true) ||
                        g.category.contains(garment.category, ignoreCase = true) ||
                        garment.name.contains(g.name, ignoreCase = true) ||
                        garment.category.contains(g.category, ignoreCase = true) ||
                        garment.styleTag.contains(outfit.styleCategory, ignoreCase = true)
            }

            tagMatch && garmentMatch
        }.ifEmpty {
            if (selectedTags.isNotEmpty()) {
                allOutfits.filter { outfit ->
                    selectedTags.any { tag ->
                        outfit.styleCategory.contains(tag, ignoreCase = true) ||
                                outfit.tags.any { t -> t.contains(tag, ignoreCase = true) }
                    }
                }.ifEmpty { allOutfits }
            } else {
                allOutfits
            }
        }

        val topRecommendations = matched.sortedByDescending { it.matchedGarmentsCount }.take(4)
        val primaryOutfit = topRecommendations.firstOrNull() ?: allOutfits.firstOrNull()

        val reason = buildString {
            append("Outfits recomendados de Pinterest")
            if (selectedTags.isNotEmpty()) {
                append(" para estilo ${selectedTags.joinToString(", ")}")
            } else {
                append(" para estilo $occasion")
            }
            if (garment != null) {
                append(" combinando con tu '${garment.name}'")
            }
            append(".")
        }

        _uiState.update {
            it.copy(
                generatedCombinationOutfits = topRecommendations,
                recommendation = primaryOutfit,
                recommendationReason = reason
            )
        }
    }

    fun generateRecommendation(occasion: String) {
        generateOutfitCombination(occasion)
    }
}
