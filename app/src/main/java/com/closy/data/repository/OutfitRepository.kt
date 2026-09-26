package com.closy.data.repository

import androidx.annotation.VisibleForTesting
import com.closy.data.db.ClosetGarmentEntity
import com.closy.data.db.InMemorySavedOutfitDao
import com.closy.data.db.SavedOutfitDao
import com.closy.data.db.SavedOutfitEntity
import com.closy.data.model.GarmentItem
import com.closy.data.model.Outfit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class OutfitRepository(
    savedOutfitDao: SavedOutfitDao? = null
) {
    private val activeSavedOutfitDao: SavedOutfitDao = savedOutfitDao?.also {
        globalSavedOutfitDao = it
    } ?: globalSavedOutfitDao ?: InMemorySavedOutfitDao().also {
        globalSavedOutfitDao = it
    }

    private val _favoriteOutfitIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteOutfitIds: StateFlow<Set<String>> = _favoriteOutfitIds.asStateFlow()

    init {
        loadSavedOutfitsForUser(getActiveUserEmail())
    }

    fun getActiveUserEmail(): String {
        return AuthRepository.currentUserEmail ?: "guest@closy.com"
    }

    fun loadSavedOutfitsForUser(userEmail: String = getActiveUserEmail()): Set<String> {
        val ids = runBlocking {
            withContext(Dispatchers.IO) {
                activeSavedOutfitDao.getSavedOutfitIdsForUser(userEmail).toSet()
            }
        }
        _favoriteOutfitIds.value = ids
        return ids
    }

    suspend fun isOutfitSaved(userEmail: String = getActiveUserEmail(), outfitId: String): Boolean {
        return withContext(Dispatchers.IO) {
            activeSavedOutfitDao.isOutfitSaved(userEmail, outfitId)
        }
    }

    suspend fun saveOutfit(userEmail: String = getActiveUserEmail(), outfitId: String) {
        withContext(Dispatchers.IO) {
            activeSavedOutfitDao.saveOutfit(
                SavedOutfitEntity(userEmail = userEmail, outfitId = outfitId)
            )
        }
        loadSavedOutfitsForUser(userEmail)
    }

    suspend fun removeSavedOutfit(userEmail: String = getActiveUserEmail(), outfitId: String) {
        withContext(Dispatchers.IO) {
            activeSavedOutfitDao.removeSavedOutfit(userEmail, outfitId)
        }
        loadSavedOutfitsForUser(userEmail)
    }

    fun toggleFavorite(outfitId: String, userEmail: String = getActiveUserEmail()) {
        runBlocking {
            withContext(Dispatchers.IO) {
                val isCurrentlySaved = activeSavedOutfitDao.isOutfitSaved(userEmail, outfitId)
                if (isCurrentlySaved) {
                    activeSavedOutfitDao.removeSavedOutfit(userEmail, outfitId)
                } else {
                    activeSavedOutfitDao.saveOutfit(
                        SavedOutfitEntity(userEmail = userEmail, outfitId = outfitId)
                    )
                }
            }
        }
        loadSavedOutfitsForUser(userEmail)
    }

    private val sampleOutfits = listOf(
        // --- MUJER ---
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
            isSaved = false,
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
            isSaved = false,
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
        Outfit(
            id = "outfit_6",
            title = "Conjunto Chic de Punto & Falda",
            styleCategory = "Casual",
            genderPreference = "Mujer",
            imageUrl = "https://images.unsplash.com/photo-1581044777550-4cfa60707c03?w=600",
            tags = listOf("Knitwear", "CasualChic", "Otoño"),
            itemsCount = 3,
            garments = listOf(
                GarmentItem("Suéter Tejido Camel", "Camisa/Blusa", "Zara"),
                GarmentItem("Falda Midi de Satén", "Pantalón/Falda", "Stradivarius"),
                GarmentItem("Botines de Piel Marrón", "Calzado", "Mango")
            ),
            pinterestUrl = "https://pinterest.com/pin/106",
            aspectRatio = 1.4f,
            pinterestHandle = "@streetstyle_paris",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=150",
                "https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=150",
                "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=150"
            ),
            garmentSummary = "Suéter Tejido Camel · Falda Satén · Botines de Piel",
            hashtags = listOf("#knitwear", "#casualchic", "#autumn")
        ),

        // --- HOMBRE ---
        Outfit(
            id = "outfit_7",
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
            isSaved = false,
            pinterestUrl = "https://pinterest.com/pin/107",
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
            id = "outfit_8",
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
            pinterestUrl = "https://pinterest.com/pin/108",
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
            id = "outfit_9",
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
            pinterestUrl = "https://pinterest.com/pin/109",
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
            id = "outfit_10",
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
            pinterestUrl = "https://pinterest.com/pin/110",
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
        Outfit(
            id = "outfit_11",
            title = "Chaqueta de Cuero & Denim Raw",
            styleCategory = "Urbano",
            genderPreference = "Hombre",
            imageUrl = "https://images.unsplash.com/photo-1516257984-b1b4d707412e?w=600",
            tags = listOf("Leather", "Denim", "Rock"),
            itemsCount = 3,
            garments = listOf(
                GarmentItem("Chaqueta Biker de Cuero Negro", "Camisa/Blusa", "AllSaints"),
                GarmentItem("Jeans Denim Selvedge", "Pantalón/Falda", "Levi's"),
                GarmentItem("Botas Chelsea de Cuero", "Calzado", "Red Wing")
            ),
            pinterestUrl = "https://pinterest.com/pin/111",
            aspectRatio = 1.4f,
            pinterestHandle = "@menwithstreetstyle",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=150",
                "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=150",
                "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=150"
            ),
            garmentSummary = "Chaqueta Biker Cuero · Jeans Selvedge · Botas Chelsea",
            hashtags = listOf("#leather", "#denim", "#rock")
        ),
        Outfit(
            id = "outfit_12",
            title = "Blazer Minimalista & Mocasines",
            styleCategory = "Elegante",
            genderPreference = "Hombre",
            imageUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=600",
            tags = listOf("Tailored", "SmartCasual", "Minimal"),
            itemsCount = 4,
            garments = listOf(
                GarmentItem("Blazer Desestructurado Gris", "Camisa/Blusa", "Massimo Dutti"),
                GarmentItem("Camisa Oxford Celeste", "Camisa/Blusa", "Scalpers"),
                GarmentItem("Pantalón de Vestir Marino", "Pantalón/Falda", "Zara Man"),
                GarmentItem("Mocasines Penny Leather", "Calzado", "Sebago")
            ),
            pinterestUrl = "https://pinterest.com/pin/112",
            aspectRatio = 1.5f,
            pinterestHandle = "@dapper_men",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=150",
                "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=150",
                "https://images.unsplash.com/photo-1562183241-b937e95585b6?w=150"
            ),
            garmentSummary = "Blazer Desestructurado Gris · Camisa Oxford · Mocasines Penny",
            hashtags = listOf("#smartcasual", "#tailored", "#minimal")
        ),

        // --- SIN GÉNERO ---
        Outfit(
            id = "outfit_13",
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
            isSaved = false,
            pinterestUrl = "https://pinterest.com/pin/113",
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
            id = "outfit_14",
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
            pinterestUrl = "https://pinterest.com/pin/114",
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
            id = "outfit_15",
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
            pinterestUrl = "https://pinterest.com/pin/115",
            aspectRatio = 1.35f,
            pinterestHandle = "@cosstores",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=150",
                "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=150",
                "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=150"
            ),
            garmentSummary = "Blazer Recto Doble Abotonadura · Camisa Satinada · Pantalón Fluido",
            hashtags = listOf("#tailoring", "#sartorial", "#modern")
        ),
        Outfit(
            id = "outfit_16",
            title = "Look Lino Neutro & Trench",
            styleCategory = "Verano",
            genderPreference = "Sin género",
            imageUrl = "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=600",
            tags = listOf("Lino", "Trench", "Unisex"),
            itemsCount = 3,
            garments = listOf(
                GarmentItem("Trench Coat Ligerísimo Camel", "Camisa/Blusa", "Burberry"),
                GarmentItem("Camisa de Lino Crudo", "Camisa/Blusa", "Uniqlo U"),
                GarmentItem("Pantalón Recto de Lino", "Pantalón/Falda", "Zara")
            ),
            pinterestUrl = "https://pinterest.com/pin/116",
            aspectRatio = 1.45f,
            pinterestHandle = "@unisex_style",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1544441893-675973e31985?w=150",
                "https://images.unsplash.com/photo-1598033129183-c4f50c736f10?w=150",
                "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=150"
            ),
            garmentSummary = "Trench Coat Camel · Camisa Lino Crudo · Pantalón Recto",
            hashtags = listOf("#lino", "#trench", "#neutro")
        ),
        Outfit(
            id = "outfit_17",
            title = "Set Deportivo Tonal Earth",
            styleCategory = "Urbano",
            genderPreference = "Sin género",
            imageUrl = "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=600",
            tags = listOf("Athleisure", "EarthTones", "Comfort"),
            itemsCount = 3,
            garments = listOf(
                GarmentItem("Polerón Oversize Terracota", "Camisa/Blusa", "Fear of God Essentials"),
                GarmentItem("Jogger Tonal Algodón", "Pantalón/Falda", "Adidas Originals"),
                GarmentItem("Sneakers Chunky Neutras", "Calzado", "New Balance")
            ),
            pinterestUrl = "https://pinterest.com/pin/117",
            aspectRatio = 1.3f,
            pinterestHandle = "@athleisure_co",
            garmentThumbnails = listOf(
                "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=150",
                "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=150",
                "https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=150"
            ),
            garmentSummary = "Polerón Terracota · Jogger Tonal Algodón · Sneakers Chunky",
            hashtags = listOf("#athleisure", "#earthtones", "#comfort")
        )
    )

    fun calculateMatchedGarmentsCount(outfit: Outfit, closetGarments: List<ClosetGarmentEntity>): Int {
        if (closetGarments.isEmpty() || outfit.garments.isEmpty()) return 0

        var matchCount = 0
        for (outfitGarment in outfit.garments) {
            val isMatched = closetGarments.any { closetItem ->
                isGarmentMatch(closetItem, outfitGarment)
            }
            if (isMatched) {
                matchCount++
            }
        }
        return matchCount
    }

    private fun isGarmentMatch(closetItem: ClosetGarmentEntity, outfitGarment: GarmentItem): Boolean {
        val closetName = closetItem.name.lowercase()
        val outfitName = outfitGarment.name.lowercase()
        val closetCat = closetItem.category.lowercase()
        val outfitCat = outfitGarment.category.lowercase()

        if (closetName.contains(outfitName) || outfitName.contains(closetName)) return true

        val keyWords = listOf(
            "blazer", "saco", "camisa", "top", "pantalón", "pantalon", "jeans", "denim",
            "mocasines", "sneakers", "zapatillas", "chaqueta", "reloj", "gafas", "vestido",
            "sudadera", "hoodie", "botas", "sandalias", "falda", "bolso", "corbata"
        )
        for (kw in keyWords) {
            if (closetName.contains(kw) && outfitName.contains(kw)) return true
        }

        if ((closetCat.contains("camisa") || closetCat.contains("top")) && outfitCat.contains("camisa")) return true
        if ((closetCat.contains("pantalón") || closetCat.contains("pantalon") || closetCat.contains("jean")) && outfitCat.contains("pantalón")) return true
        if (closetCat.contains("calzado") && outfitCat.contains("calzado")) return true
        if (closetCat.contains("accesorio") && outfitCat.contains("accesorio")) return true
        if (closetCat.contains("saco") && (outfitCat.contains("camisa") || outfitCat.contains("saco"))) return true

        return false
    }

    fun getOutfits(
        genderPreference: String,
        searchQuery: String = "",
        categoryFilter: String = "Todos",
        savedOnly: Boolean = false,
        userEmail: String = getActiveUserEmail(),
        ignoreGenderForSaved: Boolean = false,
        closetGarments: List<ClosetGarmentEntity> = emptyList()
    ): List<Outfit> {
        val favorites = loadSavedOutfitsForUser(userEmail)

        return sampleOutfits.filter { outfit ->
            val isSaved = favorites.contains(outfit.id)
            if (savedOnly && !isSaved) {
                return@filter false
            }

            // Strict gender matching: if "Todos", return all; otherwise match outfit.genderPreference strictly
            val genderMatches = (savedOnly && ignoreGenderForSaved) ||
                    genderPreference.equals("Todos", ignoreCase = true) ||
                    outfit.genderPreference.equals(genderPreference, ignoreCase = true)

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
            val isFav = favorites.contains(outfit.id)
            val matchedCount = calculateMatchedGarmentsCount(outfit, closetGarments)
            outfit.copy(
                isSaved = isFav,
                isFavorite = isFav,
                matchedGarmentsCount = matchedCount
            )
        }
    }

    companion object {
        @Volatile
        private var globalSavedOutfitDao: SavedOutfitDao? = null

        fun init(savedOutfitDao: SavedOutfitDao) {
            globalSavedOutfitDao = savedOutfitDao
        }

        fun getGlobalSavedOutfitDao(): SavedOutfitDao? = globalSavedOutfitDao

        @VisibleForTesting
        @Suppress("unused")
        fun resetDao() {
            globalSavedOutfitDao = null
        }
    }
}
