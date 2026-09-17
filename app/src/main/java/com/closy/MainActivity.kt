package com.closy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.closy.navigation.AppNavigation
import com.closy.ui.theme.ClosyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClosyTheme {
                AppNavigation(modifier = Modifier.fillMaxSize())
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
