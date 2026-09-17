package com.closy.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = BeigeSurfaceContainer,
    onPrimaryContainer = TextPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = BeigeSurfaceVariant,
    onSecondaryContainer = TextPrimary,
    background = BeigeBackground,
    onBackground = TextPrimary,
    surface = BeigeSurface,
    onSurface = TextPrimary,
    surfaceVariant = BeigeSurfaceContainer,
    onSurfaceVariant = TextSecondary,
    outline = BeigeBorder,
    error = ErrorRed,
    onError = DarkOnPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkOnPrimary,
    onPrimary = DarkPrimary,
    primaryContainer = DarkSecondary,
    onPrimaryContainer = DarkOnPrimary,
    secondary = BeigeSurfaceVariant,
    onSecondary = TextPrimary,
    background = DarkPrimary,
    onBackground = DarkOnPrimary,
    surface = DarkSecondary,
    onSurface = DarkOnPrimary,
    surfaceVariant = DarkSecondary,
    onSurfaceVariant = BeigeSurfaceVariant,
    outline = BeigeBorder,
    error = ErrorRed,
    onError = DarkOnPrimary
)

@Composable
fun ClosyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = ClosyShapes,
        content = content
    )
}
