package com.closy

import com.closy.data.db.ClosetGarmentEntity
import com.closy.data.db.InMemoryClosetGarmentDao
import com.closy.data.repository.ClosetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClosetRepositoryTest {

    private lateinit var repository: ClosetRepository

    @Before
    fun setUp() {
        val dao = InMemoryClosetGarmentDao()
        repository = ClosetRepository(garmentDao = dao)
    }

    @Test
    fun getGarmentsForUser_jose_seedsRichCollection() = runTest {
        val garments = repository.getGarmentsForUser("jose@gmail.com")

        assertTrue("Jose should have 12-15 garments pre-populated", garments.size in 12..15)

        val categories = garments.map { it.category }.toSet()
        assertTrue(categories.contains("Camisas"))
        assertTrue(categories.contains("Tops"))
        assertTrue(categories.contains("Sacos/Blazers"))
        assertTrue(categories.contains("Pantalones"))
        assertTrue(categories.contains("Jeans"))
        assertTrue(categories.contains("Calzado/Sneakers"))
        assertTrue(categories.contains("Accesorios"))

        garments.forEach { garment ->
            assertEquals("jose@gmail.com", garment.userEmail)
            assertTrue(garment.id.isNotBlank())
            assertTrue(garment.name.isNotBlank())
            assertTrue(garment.imageUrl.isNotBlank())
            assertTrue(garment.styleTag.isNotBlank())
        }
    }

    @Test
    fun getGarmentsForUser_joseCaseInsensitive_seedsRichCollection() = runTest {
        val garments = repository.getGarmentsForUser("Jose@Gmail.Com")

        assertTrue("Jose case-insensitive email should seed rich collection", garments.size in 12..15)
        assertEquals("Jose@Gmail.Com", garments.first().userEmail)
    }

    @Test
    fun getGarmentsForUser_newAccount_returnsEmptyList() = runTest {
        val garments = repository.getGarmentsForUser("maria@gmail.com")

        assertTrue("New user accounts should default to empty closet", garments.isEmpty())
        assertEquals(0, repository.getGarmentCountForUser("maria@gmail.com"))
    }

    @Test
    fun insertGarment_and_deleteGarment_and_getGarmentCountForUser() = runTest {
        val email = "user@closy.app"
        assertEquals(0, repository.getGarmentCountForUser(email))

        val newGarment = ClosetGarmentEntity(
            id = "g_101",
            userEmail = email,
            name = "Chaqueta Denim",
            category = "Abrigos",
            color = "#2886B8",
            imageUrl = "https://example.com/denim.jpg",
            styleTag = "Casual"
        )

        repository.insertGarment(newGarment)

        val garmentsAfterInsert = repository.getGarmentsForUser(email)
        assertEquals(1, garmentsAfterInsert.size)
        assertEquals("Chaqueta Denim", garmentsAfterInsert.first().name)
        assertEquals(1, repository.getGarmentCountForUser(email))

        repository.deleteGarment(newGarment)

        val garmentsAfterDelete = repository.getGarmentsForUser(email)
        assertEquals(0, garmentsAfterDelete.size)
        assertEquals(0, repository.getGarmentCountForUser(email))
    }

    @Test
    fun addGarment_and_deleteGarmentById() = runTest {
        val email = "test_add_delete@closy.app"
        assertEquals(0, repository.getGarmentCountForUser(email))

        val garment = ClosetGarmentEntity(
            id = "g_202",
            userEmail = email,
            name = "Saco Azul",
            category = "Sacos",
            color = "#486FA5",
            imageUrl = "https://example.com/saco.jpg",
            styleTag = "Formal"
        )

        repository.addGarment(garment)

        val garments = repository.getGarmentsForUser(email)
        assertEquals(1, garments.size)
        assertEquals("Saco Azul", garments.first().name)
        assertEquals(1, repository.getGarmentCountForUser(email))

        repository.deleteGarment(garmentId = "g_202", userEmail = email)

        val garmentsAfter = repository.getGarmentsForUser(email)
        assertEquals(0, garmentsAfter.size)
        assertEquals(0, repository.getGarmentCountForUser(email))
    }

    @Test
    fun updateGarment_modifiesExistingGarmentInRoomAndDao() = runTest {
        val email = "update_test@closy.app"
        val initialGarment = ClosetGarmentEntity(
            id = "g_update_1",
            userEmail = email,
            name = "Camisa Vieja",
            category = "Camisas",
            color = "#FFFFFF",
            imageUrl = "https://example.com/old.jpg",
            styleTag = "Casual"
        )
        repository.addGarment(initialGarment)

        val garmentsBefore = repository.getGarmentsForUser(email)
        assertEquals(1, garmentsBefore.size)
        assertEquals("Camisa Vieja", garmentsBefore.first().name)

        val updatedGarment = initialGarment.copy(
            name = "Camisa Nueva",
            color = "#000000",
            styleTag = "Formal"
        )
        repository.updateGarment(updatedGarment)

        val garmentsAfter = repository.getGarmentsForUser(email)
        assertEquals(1, garmentsAfter.size)
        assertEquals("Camisa Nueva", garmentsAfter.first().name)
        assertEquals("#000000", garmentsAfter.first().color)
        assertEquals("Formal", garmentsAfter.first().styleTag)
    }
}
