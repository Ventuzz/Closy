package com.closy.ui.home

import com.closy.data.model.Outfit

data class HomeUiState(
    val outfits: List<Outfit> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "Todos",
    val activeGenderPreference: String = "Mujer",
    val selectedOutfitForDetail: Outfit? = null,
    val isLoading: Boolean = false,
    val selectedSegmentTab: Int = 0, // 0 = "Para Ti", 1 = "Guardados"
    val selectedBottomTab: Int = 0, // 0 = Inicio, 1 = Closet, 2 = Generar, 3 = Perfil
    val categories: List<String> = listOf("Todos", "Casual", "Formal", "Urbano", "Verano", "Elegante"),
    val availableGenderOptions: List<String> = listOf("Mujer", "Hombre", "Sin género")
)
