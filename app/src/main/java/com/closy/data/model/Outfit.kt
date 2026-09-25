package com.closy.data.model

data class Outfit(
    val id: String,
    val title: String,
    val styleCategory: String, // "Casual", "Formal", "Urbano", "Verano", "Elegante"
    val genderPreference: String, // "Mujer", "Hombre", "Sin género"
    val imageUrl: String,
    val tags: List<String>,
    val itemsCount: Int,
    val garments: List<GarmentItem> = emptyList(),
    val isSaved: Boolean = false,
    val isFavorite: Boolean = isSaved,
    val source: String = "Pinterest",
    val pinterestUrl: String = "https://pinterest.com",
    val aspectRatio: Float = 1.3f,
    val pinterestHandle: String = "@pinterest",
    val garmentThumbnails: List<String> = emptyList(),
    val garmentSummary: String = "",
    val hashtags: List<String> = emptyList(),
    val matchedGarmentsCount: Int = 0
)
