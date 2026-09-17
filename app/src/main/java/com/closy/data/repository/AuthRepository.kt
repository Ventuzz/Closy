package com.closy.data.repository

import androidx.annotation.VisibleForTesting
import com.closy.data.db.InMemoryUserDao
import com.closy.data.db.UserDao
import com.closy.data.db.UserEntity
import com.closy.data.model.User
import com.closy.data.model.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AuthRepository(
    userDao: UserDao? = null,
) {
    private val activeUserDao: UserDao = userDao?.also {
        globalUserDao = it
        resetSession()
    } ?: globalUserDao ?: InMemoryUserDao().also {
        globalUserDao = it
    }

    private val _currentUser get() = _sharedCurrentUser
    val currentUser: StateFlow<User?> get() = _sharedCurrentUser.asStateFlow()

    suspend fun login(email: String, pass: String): Result<User> {
        if (email.isBlank() || pass.isBlank()) {
            return Result.failure(IllegalArgumentException("Por favor completa todos los campos"))
        }

        val entity = activeUserDao.getUserByEmailAndPassword(email, pass)
            ?: return Result.failure(IllegalStateException("Usuario no registrado o contraseña incorrecta."))

        val user = User(
            id = entity.id.toString(),
            email = entity.email,
            name = entity.name,
            preferences = UserPreferences(genderPreference = entity.genderPreference ?: "")
        )
        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun signUp(name: String, email: String, pass: String): Result<User> {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            return Result.failure(IllegalArgumentException("Por favor completa todos los campos"))
        }

        val existingUser = activeUserDao.getUserByEmail(email)
        if (existingUser != null) {
            return Result.failure(IllegalStateException("El correo electrónico ya está registrado. Por favor inicia sesión."))
        }

        val entity = UserEntity(
            name = name,
            email = email,
            password = pass,
            genderPreference = null
        )
        activeUserDao.insertUser(entity)

        val registeredEntity = activeUserDao.getUserByEmail(email) ?: entity
        val user = User(
            id = registeredEntity.id.toString(),
            email = registeredEntity.email,
            name = registeredEntity.name,
            preferences = UserPreferences(genderPreference = registeredEntity.genderPreference ?: "")
        )
        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun loginWithGoogle(): Result<User> {
        val googleEmail = "usuario.google@gmail.com"
        val existingUser = activeUserDao.getUserByEmail(googleEmail)
        val entity = if (existingUser == null) {
            val newEntity = UserEntity(
                name = "Usuario Google",
                email = googleEmail,
                password = "google_auth_dummy_pass",
                genderPreference = null
            )
            activeUserDao.insertUser(newEntity)
            activeUserDao.getUserByEmail(googleEmail) ?: newEntity
        } else {
            existingUser
        }

        val user = User(
            id = entity.id.toString(),
            email = entity.email,
            name = entity.name,
            preferences = UserPreferences(genderPreference = entity.genderPreference ?: "")
        )
        _currentUser.value = user
        return Result.success(user)
    }

    fun loginAsGuest(): Result<User> {
        val user = User(
            id = "guest_user",
            email = "invitado@closy.app",
            name = "Invitado"
        )
        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun updateGenderPreference(preference: String): Result<Boolean> {
        val user = _currentUser.value
        val email = user?.email ?: currentUserEmail
        if (!email.isNullOrBlank() && user?.id != "guest_user") {
            withContext(Dispatchers.IO) {
                activeUserDao.updateGenderPreference(email, preference)
            }
        }
        val currentPrefs = user?.preferences ?: UserPreferences()
        val updatedPrefs = currentPrefs.copy(genderPreference = preference)
        val updatedUser = user?.copy(preferences = updatedPrefs)
            ?: User(id = "guest_user", email = "invitado@closy.app", name = "Invitado", preferences = updatedPrefs)
        _currentUser.value = updatedUser
        return Result.success(true)
    }

    suspend fun saveGenderPreference(gender: String): Result<Boolean> {
        return updateGenderPreference(gender)
    }

    @VisibleForTesting
    @Suppress("unused")
    suspend fun savePreferences(preferences: UserPreferences): Result<Boolean> {
        val user = _currentUser.value
        val email = user?.email ?: currentUserEmail
        if ((!email.isNullOrBlank()) && user?.id != "guest_user") {
            withContext(Dispatchers.IO) {
                activeUserDao.updateGenderPreference(email, preferences.genderPreference)
            }
        }
        val updatedUser = user?.copy(preferences = preferences)
            ?: User(id = "guest_user", email = "invitado@closy.app", name = "Invitado", preferences = preferences)
        _currentUser.value = updatedUser
        return Result.success(true)
    }

    fun hasSavedGenderPreference(): Boolean {
        val pref = _currentUser.value?.preferences?.genderPreference
        return !pref.isNullOrEmpty()
    }

    fun logout() {
        _currentUser.value = null
    }

    companion object {
        @Volatile
        private var globalUserDao: UserDao? = null

        private var _sharedCurrentUser = MutableStateFlow<User?>(null)

        @VisibleForTesting
        @Suppress("unused")
        val sharedCurrentUser: StateFlow<User?> get() = _sharedCurrentUser.asStateFlow()

        val currentUserEmail: String?
            get() = _sharedCurrentUser.value?.email

        fun init(userDao: UserDao) {
            globalUserDao = userDao
        }

        fun resetSession() {
            _sharedCurrentUser = MutableStateFlow(null)
        }
    }
}
