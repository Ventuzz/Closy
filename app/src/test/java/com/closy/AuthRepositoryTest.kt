package com.closy

import com.closy.data.db.ClosetGarmentEntity
import com.closy.data.db.InMemoryClosetGarmentDao
import com.closy.data.db.InMemorySavedOutfitDao
import com.closy.data.db.InMemoryUserDao
import com.closy.data.db.SavedOutfitEntity
import com.closy.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userDao: InMemoryUserDao
    private lateinit var savedOutfitDao: InMemorySavedOutfitDao
    private lateinit var garmentDao: InMemoryClosetGarmentDao
    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userDao = InMemoryUserDao()
        savedOutfitDao = InMemorySavedOutfitDao()
        garmentDao = InMemoryClosetGarmentDao()
        repository = AuthRepository(userDao, savedOutfitDao, garmentDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSuccessfulRegistration() = runTest {
        val result = repository.signUp("María García", "maria@closy.app", "123456")

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("María García", user?.name)
        assertEquals("maria@closy.app", user?.email)
        assertEquals(user, repository.currentUser.value)
    }

    @Test
    fun testDuplicateEmailRejection() = runTest {
        val firstSignUp = repository.signUp("María García", "maria@closy.app", "123456")
        assertTrue(firstSignUp.isSuccess)

        val secondSignUp = repository.signUp("María Copia", "maria@closy.app", "654321")
        assertTrue(secondSignUp.isFailure)
        val exception = secondSignUp.exceptionOrNull()
        assertNotNull(exception)
        assertEquals(
            "El correo electrónico ya está registrado. Por favor inicia sesión.",
            exception?.message
        )
    }

    @Test
    fun testInvalidCredentialLoginRejection() = runTest {
        // Attempting login with unregistered email
        val loginUnregistered = repository.login("desconocido@closy.app", "123456")
        assertTrue(loginUnregistered.isFailure)
        assertEquals(
            "Usuario no registrado o contraseña incorrecta.",
            loginUnregistered.exceptionOrNull()?.message
        )

        // Register user and attempt login with wrong password
        repository.signUp("Carlos", "carlos@closy.app", "123456")
        val loginWrongPass = repository.login("carlos@closy.app", "wrong_password")
        assertTrue(loginWrongPass.isFailure)
        assertEquals(
            "Usuario no registrado o contraseña incorrecta.",
            loginWrongPass.exceptionOrNull()?.message
        )
    }

    @Test
    fun testSuccessfulLogin() = runTest {
        repository.signUp("Carlos", "carlos@closy.app", "123456")
        repository.logout()

        val loginResult = repository.login("carlos@closy.app", "123456")
        assertTrue(loginResult.isSuccess)
        val user = loginResult.getOrNull()
        assertNotNull(user)
        assertEquals("carlos@closy.app", user?.email)
        assertEquals("Carlos", user?.name)
        assertEquals(user, repository.currentUser.value)
    }

    @Test
    fun testGuestLogin() = runTest {
        val result = repository.loginAsGuest()

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("Invitado", user?.name)
        assertEquals("guest@closy.com", user?.email)
        assertEquals("guest_user", user?.id)
        assertEquals(user, repository.currentUser.value)
    }

    @Test
    fun testUpdateGenderPreference() = runTest {
        repository.signUp("Laura", "laura@closy.app", "123456")

        val updateResult = repository.updateGenderPreference("Mujer")
        assertTrue(updateResult.isSuccess)

        val currentUser = repository.currentUser.value
        assertNotNull(currentUser)
        assertEquals("Mujer", currentUser?.preferences?.genderPreference)
    }

    @Test
    fun testAutomaticGuestDataPurgeOnLogout() = runTest {
        repository.loginAsGuest()
        val guestEmail = "guest@closy.com"

        savedOutfitDao.saveOutfit(SavedOutfitEntity(guestEmail, "outfit_guest_1"))
        garmentDao.insertGarment(
            ClosetGarmentEntity("g1", guestEmail, "Camiseta", "Tops", "#FFFFFF", "", "Casual")
        )

        assertTrue(savedOutfitDao.getSavedOutfitIdsForUser(guestEmail).isNotEmpty())
        assertTrue(garmentDao.getGarmentCountForUser(guestEmail) > 0)

        repository.logout()

        assertNull(repository.currentUser.value)
        assertTrue(savedOutfitDao.getSavedOutfitIdsForUser(guestEmail).isEmpty())
        assertEquals(0, garmentDao.getGarmentCountForUser(guestEmail))
    }
}
