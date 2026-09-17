package com.closy.data.repository

import com.closy.data.model.GarmentItem
import com.closy.data.model.Outfit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OutfitRepository {

    private val _favoriteOutfitIds = MutableStateFlow<Set<String>>(setOf("outfit_1", "outfit_4"))
    val favoriteOutfitIds: StateFlow<Set<String>> = _favoriteOutfitIds.asStateFlow()

    private val sampleOutfits = listOf(
        // Mujer
        Outfit(
            id = "outfit_1",
            title = "Oversize Blazer & Wide Leg",
            styleCategory = "Casual",
            genderPreference = "Mujer",
            imageUrl = "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=600",
            tags = listOf("Minimalista", "Oficina", "Oversize"),
            itemsCount = 4,
            garments = listOf(
                GarmentItem("Blazer Oversize Beige", "Camisa/Blusa", "Zara"),
                GarmentItem("Top Básico Blanco", "Camisa/Blusa", "Mango"),
                GarmentItem("Pantalón Wide Leg Negro", "Pantalón/Falda", "H&M"),
                GarmentItem("Mocasines de Cuero", "Calzado", "Stradivarius"),
                GarmentItem("Bolso Crossbody", "Accesorios", "Parfois")
            ),
            isSaved = true,
            pinterestUrl = "https://pinterest.com/pin/101",
            aspectRatio = 1.35f,
            pinterestHandle = "@vogue_spain",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=150",
                "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=150",
                "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=150"
            ),
            garmentSummary = "Blazer Oversize Beige · Top Básico Blanco · Pantalón Wide Leg Negro",
            hashtags = listOf("#minimal", "#neutros", "#oficina")
        ),
        Outfit(
            id = "outfit_2",
            title = "Vestido Satinado Elegante",
            styleCategory = "Elegante",
            genderPreference = "Mujer",
            imageUrl = "https://images.unsplash.com/photo-1539109136881-3be0616acf4b?w=600",
            tags = listOf("Noche", "Satin", "Fiesta"),
            itemsCount = 3,
            garments = listOf(
                GarmentItem("Vestido Midi Satinado Champagne", "Camisa/Blusa", "Massimo Dutti"),
                GarmentItem("Sandalias de Tacón Finas", "Calzado", "Zara"),
                GarmentItem("Pendientes Dorados Minimalistas", "Accesorios", "Bimba y Lola")
            ),
            pinterestUrl = "https://pinterest.com/pin/102",
            aspectRatio = 1.6f,
            pinterestHandle = "@elle_magazine",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=150",
                "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=150",
                "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=150"
            ),
            garmentSummary = "Vestido Satinado Champagne · Sandalias Tacón Finas · Pendientes Dorados",
            hashtags = listOf("#elegante", "#fiesta", "#satin")
        ),
        Outfit(
            id = "outfit_3",
            title = "Streetwear Denim & Crop Top",
            styleCategory = "Urbano",
            genderPreference = "Mujer",
            imageUrl = "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=600",
            tags = listOf("Streetstyle", "Denim", "Sneakers"),
            itemsCount = 4,
            garments = listOf(
                GarmentItem("Crop Top Algodón Blanco", "Camisa/Blusa", "Bershka"),
                GarmentItem("Jeans Cargo Tiro Alto", "Pantalón/Falda", "Pull&Bear"),
                GarmentItem("Zapatillas Retro Chunky", "Calzado", "Nike"),
                GarmentItem("Gorra Urbana Negra", "Accesorios", "New Era")
            ),
            pinterestUrl = "https://pinterest.com/pin/103",
            aspectRatio = 1.2f,
            pinterestHandle = "@hypebae",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=150",
                "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=150",
                "https://images.unsplash.com/photo-1552346154-21d32810aba3?w=150"
            ),
            garmentSummary = "Crop Top Blanco · Jeans Cargo Tiro Alto · Sneakers Retro",
            hashtags = listOf("#urbano", "#streetstyle", "#denim")
        ),
        Outfit(
            id = "outfit_4",
            title = "Look Verano Lino Fresco",
            styleCategory = "Verano",
            genderPreference = "Mujer",
            imageUrl = "https://images.unsplash.com/photo-1496747611176-843222e1e57c?w=600",
            tags = listOf("Lino", "Playa", "Frescura"),
            itemsCount = 4,
            garments = listOf(
                GarmentItem("Blusa de Lino Blanco", "Camisa/Blusa", "Oysho"),
                GarmentItem("Falda Midi Plisada Beige", "Pantalón/Falda", "Mango"),
                GarmentItem("Sandalias de Esparto", "Calzado", "Castañer"),
                GarmentItem("Sombrero de Paja", "Accesorios", "Zara")
            ),
            isSaved = true,
            pinterestUrl = "https://pinterest.com/pin/104",
            aspectRatio = 1.45f,
            pinterestHandle = "@fashiongram",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1598033129183-c4f50c736f10?w=150",
                "https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=150",
                "https://images.unsplash.com/photo-1603808033192-082d6919d3e1?w=150"
            ),
            garmentSummary = "Blusa Lino Blanco · Falda Midi Beige · Sandalias Esparto",
            hashtags = listOf("#verano", "#lino", "#fresco")
        ),
        Outfit(
            id = "outfit_5",
            title = "Traje Ejecutivo Monocromático",
            styleCategory = "Formal",
            genderPreference = "Mujer",
            imageUrl = "https://images.unsplash.com/photo-1485230895905-ec40ba36b9bc?w=600",
            tags = listOf("Elegante", "Ejecutivo", "Tailored"),
            itemsCount = 4,
            garments = listOf(
                GarmentItem("Saco Estructurado Marfil", "Camisa/Blusa", "Massimo Dutti"),
                GarmentItem("Pantalón de Vestir Recto", "Pantalón/Falda", "Massimo Dutti"),
                GarmentItem("Stilettos de Piel", "Calzado", "Uterqüe"),
                GarmentItem("Reloj Dorado Delgado", "Accesorios", "Michael Kors")
            ),
            pinterestUrl = "https://pinterest.com/pin/105",
            aspectRatio = 1.5f,
            pinterestHandle = "@harpersbazaares",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=150",
                "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=150",
                "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=150"
            ),
            garmentSummary = "Saco Estructurado Marfil · Pantalón Vestir Recto · Stilettos",
            hashtags = listOf("#formal", "#ejecutivo", "#monocromo")
        ),

        // Hombre
        Outfit(
            id = "outfit_6",
            title = "Casual Chic con Sobrecamisa",
            styleCategory = "Casual",
            genderPreference = "Hombre",
            imageUrl = "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?w=600",
            tags = listOf("Layering", "Casual", "Otoño"),
            itemsCount = 4,
            garments = listOf(
                GarmentItem("Sobrecamisa de Cuadros Lana", "Camisa/Blusa", "Zara Man"),
                GarmentItem("Camiseta Básica Blanca", "Camisa/Blusa", "H&M"),
                GarmentItem("Pantalón Chino Beige", "Pantalón/Falda", "Dockers"),
                GarmentItem("Zapatillas Bajas Blancas", "Calzado", "Adidas")
            ),
            pinterestUrl = "https://pinterest.com/pin/106",
            aspectRatio = 1.4f,
            pinterestHandle = "@mrporter",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=150",
                "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=150",
                "https://images.unsplash.com/photo-1562183241-b937e95585b6?w=150"
            ),
            garmentSummary = "Sobrecamisa Lana · Camiseta Blanca · Pantalón Chino Beige",
            hashtags = listOf("#casual", "#layering", "#otoño")
        ),
        Outfit(
            id = "outfit_7",
            title = "Urbano Hoodie & Cargo",
            styleCategory = "Urbano",
            genderPreference = "Hombre",
            imageUrl = "https://images.unsplash.com/photo-1552374196-1ab2a1c593e8?w=600",
            tags = listOf("Hoodie", "Streetwear", "Comfort"),
            itemsCount = 4,
            garments = listOf(
                GarmentItem("Sudadera Hoodie Gris Oversize", "Camisa/Blusa", "Uniqlo"),
                GarmentItem("Pantalón Cargo Verde Oliva", "Pantalón/Falda", "Pull&Bear"),
                GarmentItem("Sneakers Altas", "Calzado", "Nike Air Jordan"),
                GarmentItem("Mochila Táctica Negra", "Accesorios", "Herschel")
            ),
            pinterestUrl = "https://pinterest.com/pin/107",
            aspectRatio = 1.25f,
            pinterestHandle = "@hypebeast",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=150",
                "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=150",
                "https://images.unsplash.com/photo-1552346154-21d32810aba3?w=150"
            ),
            garmentSummary = "Hoodie Gris Oversize · Pantalón Cargo Verde · Sneakers Altas",
            hashtags = listOf("#streetwear", "#hoodie", "#urbano")
        ),
        Outfit(
            id = "outfit_8",
            title = "Traje Azul Marino Slim Fit",
            styleCategory = "Formal",
            genderPreference = "Hombre",
            imageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=600",
            tags = listOf("Saco", "Boda", "Elegante"),
            itemsCount = 4,
            garments = listOf(
                GarmentItem("Saco y Pantalón Azul Marino", "Camisa/Blusa", "SuitSupply"),
                GarmentItem("Camisa Vestir Blanca Cotton", "Camisa/Blusa", "Brooks Brothers"),
                GarmentItem("Zapatos Oxford Cuero Café", "Calzado", "Lottusse"),
                GarmentItem("Corbata Seda Azul Oscuro", "Accesorios", "Massimo Dutti")
            ),
            pinterestUrl = "https://pinterest.com/pin/108",
            aspectRatio = 1.55f,
            pinterestHandle = "@gq",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=150",
                "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=150",
                "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=150"
            ),
            garmentSummary = "Traje Azul Marino Slim · Camisa Algodón Blanca · Zapatos Oxford",
            hashtags = listOf("#formal", "#slimfit", "#sartorial")
        ),
        Outfit(
            id = "outfit_9",
            title = "Verano Polo & Short Lino",
            styleCategory = "Verano",
            genderPreference = "Hombre",
            imageUrl = "https://images.unsplash.com/photo-1480455624313-e29b44bbfde1?w=600",
            tags = listOf("Lino", "Frescura", "Sol"),
            itemsCount = 3,
            garments = listOf(
                GarmentItem("Polo Tejido Azul Claro", "Camisa/Blusa", "Mango Man"),
                GarmentItem("Short de Lino Arena", "Pantalón/Falda", "Zara Man"),
                GarmentItem("Alpargatas de Lona", "Calzado", "TOMs"),
                GarmentItem("Gafas de Sol Wayfarer", "Accesorios", "Ray-Ban")
            ),
            pinterestUrl = "https://pinterest.com/pin/109",
            aspectRatio = 1.3f,
            pinterestHandle = "@mensfashionpost",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1625910513413-562725e1a172?w=150",
                "https://images.unsplash.com/photo-1591195853828-11db59a44f6b?w=150",
                "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=150"
            ),
            garmentSummary = "Polo Azul Claro · Short Lino Arena · Alpargatas Lona",
            hashtags = listOf("#verano", "#polo", "#lino")
        ),

        // Sin género
        Outfit(
            id = "outfit_10",
            title = "Estilo Minimalista Neutro",
            styleCategory = "Casual",
            genderPreference = "Sin género",
            imageUrl = "https://images.unsplash.com/photo-1529139574466-a303027c1d8b?w=600",
            tags = listOf("Genderless", "Minimal", "EarthTones"),
            itemsCount = 4,
            garments = listOf(
                GarmentItem("Suéter Tejido Oversize Crema", "Camisa/Blusa", "Uniqlo U"),
                GarmentItem("Pantalón Recto Plisado Beige", "Pantalón/Falda", "COS"),
                GarmentItem("Zapatillas Minimalistas Blancas", "Calzado", "Veja"),
                GarmentItem("Tote Bag de Lienzo", "Accesorios", "MUJI")
            ),
            pinterestUrl = "https://pinterest.com/pin/110",
            aspectRatio = 1.4f,
            pinterestHandle = "@minimalism_outfits",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=150",
                "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=150",
                "https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=150"
            ),
            garmentSummary = "Suéter Oversize Crema · Pantalón Plisado Beige · Zapatillas Blancas",
            hashtags = listOf("#minimal", "#neutros", "#genderless")
        ),
        Outfit(
            id = "outfit_11",
            title = "Urbano Monocromo Oversized",
            styleCategory = "Urbano",
            genderPreference = "Sin género",
            imageUrl = "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?w=600",
            tags = listOf("Monochrome", "AllBlack", "Street"),
            itemsCount = 4,
            garments = listOf(
                GarmentItem("Chaqueta Bomber Negra", "Camisa/Blusa", "Alpha Industries"),
                GarmentItem("Polera Algodón Heavyweight", "Camisa/Blusa", "Acne Studios"),
                GarmentItem("Pantalón Ancho con Pinzas", "Pantalón/Falda", "COS"),
                GarmentItem("Botines Cuero Plataforma", "Calzado", "Dr. Martens")
            ),
            pinterestUrl = "https://pinterest.com/pin/111",
            aspectRatio = 1.6f,
            pinterestHandle = "@streetwear_daily",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1544441893-675973e31985?w=150",
                "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=150",
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=150"
            ),
            garmentSummary = "Chaqueta Bomber Negra · Polera Heavyweight · Pantalón Pinzas",
            hashtags = listOf("#allblack", "#monocromo", "#oversized")
        ),
        Outfit(
            id = "outfit_12",
            title = "Sartorial Moderno Unisex",
            styleCategory = "Elegante",
            genderPreference = "Sin género",
            imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600",
            tags = listOf("Tailoring", "Modern", "Sartorial"),
            itemsCount = 4,
            garments = listOf(
                GarmentItem("Blazer Recto Doble Abotonadura", "Camisa/Blusa", "COS"),
                GarmentItem("Camisa Satinada Gris Perla", "Camisa/Blusa", "Zara"),
                GarmentItem("Pantalón Formal de Caída Fluida", "Pantalón/Falda", "Mango"),
                GarmentItem("Mocasines Destalonados", "Calzado", "Gucci")
            ),
            pinterestUrl = "https://pinterest.com/pin/112",
            aspectRatio = 1.35f,
            pinterestHandle = "@cosstores",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=150",
                "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=150",
                "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=150"
            ),
            garmentSummary = "Blazer Recto Doble Abotonadura · Camisa Satinada · Pantalón Fluido",
            hashtags = listOf("#tailoring", "#sartorial", "#modern")
        )
    )

    fun getOutfits(
        genderPreference: String,
        searchQuery: String = "",
        categoryFilter: String = "Todos",
        savedOnly: Boolean = false
    ): List<Outfit> {
        val favorites = _favoriteOutfitIds.value

        return sampleOutfits.filter { outfit ->
            val isSaved = favorites.contains(outfit.id)
            if (savedOnly && !isSaved) {
                return@filter false
            }

            // Gender match: if preference matches outfit, or if outfit or preference is "Sin género"
            val genderMatches = genderPreference.equals("Todos", ignoreCase = true) ||
                    outfit.genderPreference.equals(genderPreference, ignoreCase = true) ||
                    outfit.genderPreference.equals("Sin género", ignoreCase = true) ||
                    genderPreference.equals("Sin género", ignoreCase = true)

            // Category match
            val categoryMatches = categoryFilter.equals("Todos", ignoreCase = true) ||
                    outfit.styleCategory.equals(categoryFilter, ignoreCase = true)

            // Search query match
            val searchMatches = searchQuery.isBlank() ||
                    outfit.title.contains(searchQuery, ignoreCase = true) ||
                    outfit.styleCategory.contains(searchQuery, ignoreCase = true) ||
                    outfit.pinterestHandle.contains(searchQuery, ignoreCase = true) ||
                    outfit.garmentSummary.contains(searchQuery, ignoreCase = true) ||
                    outfit.tags.any { it.contains(searchQuery, ignoreCase = true) } ||
                    outfit.hashtags.any { it.contains(searchQuery, ignoreCase = true) } ||
                    outfit.garments.any {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                                it.category.contains(searchQuery, ignoreCase = true)
                    }

            genderMatches && categoryMatches && searchMatches
        }.map { outfit ->
            outfit.copy(isSaved = favorites.contains(outfit.id))
        }
    }

    fun toggleFavorite(outfitId: String) {
        _favoriteOutfitIds.update { current ->
            if (current.contains(outfitId)) {
                current - outfitId
            } else {
                current + outfitId
            }
        }
    }
}
