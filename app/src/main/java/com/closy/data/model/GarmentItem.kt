package com.closy.data.model

data class GarmentItem(
    val name: String,
    val category: String, // e.g., "Camisa/Blusa", "Pantalón/Falda", "Calzado", "Accesorios"
    val brandOrNote: String? = null
)
