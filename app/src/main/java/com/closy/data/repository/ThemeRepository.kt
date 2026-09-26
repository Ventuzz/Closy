package com.closy.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeRepository {
    companion object {
        private val _isDarkMode = MutableStateFlow(false)
        val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

        fun setDarkMode(enabled: Boolean) {
            _isDarkMode.value = enabled
        }

        fun toggleDarkMode() {
            _isDarkMode.value = !_isDarkMode.value
        }
    }

    val isDarkMode: StateFlow<Boolean> get() = Companion.isDarkMode

    fun setDarkMode(enabled: Boolean) {
        Companion.setDarkMode(enabled)
    }

    fun toggleDarkMode() {
        Companion.toggleDarkMode()
    }
}
