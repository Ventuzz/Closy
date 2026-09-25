package com.closy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.closy.data.repository.ThemeRepository
import com.closy.navigation.AppNavigation
import com.closy.ui.theme.ClosyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by ThemeRepository.isDarkMode.collectAsStateWithLifecycle()
            ClosyTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    modifier = Modifier.fillMaxSize(),
                    onDarkModeChange = { ThemeRepository.setDarkMode(it) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    val isDarkMode by ThemeRepository.isDarkMode.collectAsStateWithLifecycle()
    ClosyTheme(darkTheme = isDarkMode) {
        AppNavigation()
    }
}
