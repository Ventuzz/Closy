package com.closy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.closy.ui.auth.AuthScreen
import com.closy.ui.auth.AuthViewModel
import com.closy.ui.home.HomeScreen
import com.closy.ui.personalization.PersonalizationScreen
import kotlinx.serialization.Serializable

@Serializable
data object AuthKey : NavKey

@Serializable
data object PersonalizationKey : NavKey

@Serializable
data object HomeKey : NavKey

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    onDarkModeChange: (Boolean) -> Unit = {}
) {
    val backStack = rememberNavBackStack(AuthKey)
    val authViewModel: AuthViewModel = viewModel()

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryProvider = { key ->
            NavEntry(key) {
                when (key) {
                    is AuthKey -> {
                        AuthScreen(
                            viewModel = authViewModel,
                            onAuthSuccess = { hasSavedPreference ->
                                if (hasSavedPreference) {
                                    backStack.add(HomeKey)
                                } else {
                                    backStack.add(PersonalizationKey)
                                }
                            }
                        )
                    }
                    is PersonalizationKey -> {
                        PersonalizationScreen(
                            onComplete = {
                                backStack.add(HomeKey)
                            }
                        )
                    }
                    is HomeKey -> {
                        HomeScreen(
                            onDarkModeChange = onDarkModeChange,
                            onNavigateToPersonalization = {
                                backStack.add(PersonalizationKey)
                            },
                            onLogout = {
                                authViewModel.logout()
                                backStack.clear()
                                backStack.add(AuthKey)
                            }
                        )
                    }
                    else -> {
                        AuthScreen(
                            viewModel = authViewModel,
                            onAuthSuccess = { hasSavedPreference ->
                                if (hasSavedPreference) {
                                    backStack.add(HomeKey)
                                } else {
                                    backStack.add(PersonalizationKey)
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}
