package com.closy.ui.home

import com.closy.data.db.ClosetGarmentEntity
import com.closy.data.db.ClosetItemEntity
import com.closy.data.model.Outfit

data class DeleteConfirmationState(
    val title: String,
    val body: String,
    val confirmButtonText: String,
    val dismissButtonText: String = "Cancelar"
)

data class HomeUiState(
    val outfits: List<Outfit> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "Todos",
    val selectedClosetCategory: String = "Todos",
    val activeGenderPreference: String = "Mujer",
    val selectedOutfitForDetail: Outfit? = null,
    val isLoading: Boolean = false,
    val selectedSegmentTab: Int = 0, // 0 = "Para Ti", 1 = "Favoritos"
    val selectedBottomTab: Int = 0, // 0 = Inicio, 1 = Closet, 2 = Generar, 3 = Perfil
    val categories: List<String> = listOf("Todos", "Casual", "Formal", "Urbano", "Verano", "Elegante"),
    val availableGenderOptions: List<String> = listOf("Mujer", "Hombre", "Sin género"),
    val availableStyleTags: List<String> = listOf("Casual", "Urbano", "Formal", "Verano", "Fiesta", "Cita", "Trabajo", "Minimalista", "Deportivo", "Elegante"),
    val selectedStyleTags: Set<String> = emptySet(),
    val closetItems: List<ClosetItemEntity> = emptyList(),
    val closetGarments: List<ClosetGarmentEntity> = emptyList(),
    val selectedGeneratorGarment: ClosetGarmentEntity? = null,
    val generatedCombinationOutfits: List<Outfit> = emptyList(),
    val userName: String = "Invitado",
    val userEmail: String = "guest@closy.com",
    val recommendation: Outfit? = null,
    val recommendationReason: String = "",
    val savedOutfitCount: Int = 0,
    val pendingDeleteOutfit: Outfit? = null,
    val pendingDeleteClosetItem: ClosetItemEntity? = null,
    val pendingDeleteGarment: ClosetGarmentEntity? = null,
    val deleteConfirmationState: DeleteConfirmationState? = null
) {
    val closetGarmentsCount: Int get() = closetGarments.size
    val favoriteOutfitsCount: Int get() = savedOutfitCount
    val savedOutfitsCount: Int get() = savedOutfitCount

    val filteredClosetGarments: List<ClosetGarmentEntity>
        get() {
            val filter = selectedClosetCategory
            return closetGarments.filter { item ->
                val cat = item.category.lowercase()
                when (filter) {
                    "Todos" -> true
                    "Camisas" -> cat.contains("camisa") || cat.contains("blusa")
                    "Tops" -> cat == "tops" || cat.contains("top") || cat.contains("parte superior") || cat.contains("camiseta")
                    "Sacos" -> cat.contains("saco") || cat.contains("blazer") || cat.contains("abrigo") || cat.contains("chaqueta")
                    "Pantalones" -> cat.contains("pantal") || cat.contains("jean") || cat.contains("parte inferior") || cat.contains("short")
                    "Calzado" -> cat.contains("calzado") || cat.contains("sneaker") || cat.contains("zapato") || cat.contains("mocasin")
                    "Accesorios" -> cat.contains("accesor") || cat.contains("reloj") || cat.contains("gafa")
                    else -> item.category.equals(filter, ignoreCase = true)
                }
            }
        }
}
