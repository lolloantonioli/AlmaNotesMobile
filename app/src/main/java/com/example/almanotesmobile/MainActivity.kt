package com.example.almanotesmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.almanotesmobile.ui.composables.AppBar
import com.example.almanotesmobile.ui.composables.NavigationBar
import com.example.almanotesmobile.ui.screens.ThemeViewModel
import com.example.almanotesmobile.ui.theme.AlmaNotesMobileTheme
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import com.example.almanotesmobile.data.Theme
import com.example.almanotesmobile.ui.screens.AuthViewModel
import com.example.almanotesmobile.ui.screens.RegistrationScreen
import com.example.almanotesmobile.ui.screens.ThemeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val authViewModel = koinViewModel<AuthViewModel>()
            val isRegistered by authViewModel.isRegistered.collectAsStateWithLifecycle(initialValue = null)
            when (isRegistered) {
                null -> { /* Schermata bianca o logo di caricamento */ }
                true -> {
                    // Vai alla Login o direttamente alla Home
                }
                false -> {
                    RegistrationScreen(
                        onRegisterSuccess = { username ->
                            authViewModel.register(username)
                        }
                    )
                }
            }
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val themeState by themeViewModel.state.collectAsStateWithLifecycle()
            AlmaNotesMobileTheme(
                darkTheme = when (themeState.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                },
                dynamicColor = themeState.dynamicColor
            ) {
                /*Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AppBar() },
                    bottomBar = { NavigationBar() }
                ) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }*/
                ThemeScreen(themeState, themeViewModel.actions)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AlmaNotesMobileTheme {
        Greeting("Android")
    }
}