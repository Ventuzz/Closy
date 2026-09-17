package com.closy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.closy.navigation.AppNavigation
import com.closy.ui.theme.ClosyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkModeOverride by remember { mutableStateOf<Boolean?>(null) }
            ClosyTheme(darkTheme = darkModeOverride ?: isSystemInDarkTheme()) {
                AppNavigation(
                    modifier = Modifier.fillMaxSize(),
                    onDarkModeChange = { darkModeOverride = it }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    ClosyTheme {
        AppNavigation()
    }
}
