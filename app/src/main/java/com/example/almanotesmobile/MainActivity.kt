package com.example.almanotesmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.almanotesmobile.data.Theme
import com.example.almanotesmobile.ui.composables.AppBar
import com.example.almanotesmobile.ui.screens.*
import com.example.almanotesmobile.ui.theme.AlmaNotesMobileTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val authViewModel = koinViewModel<AuthViewModel>()
            val themeViewModel = koinViewModel<ThemeViewModel>()

            val isRegistered by authViewModel.isRegistered.collectAsStateWithLifecycle()
            val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
            val themeState by themeViewModel.state.collectAsStateWithLifecycle()

            // Stato per forzare la visualizzazione del Login anche se non registrati (o viceversa)
            var showLoginOverride by remember { mutableStateOf<Boolean?>(null) }

            AlmaNotesMobileTheme(
                darkTheme = when (themeState.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                },
                dynamicColor = themeState.dynamicColor
            ) {
                // Determiniamo quale schermata mostrare basandoci sul DataStore o sull'override manuale
                val showLogin = showLoginOverride ?: (isRegistered == true)

                when {
                    isRegistered == null -> { /* Caricamento... */ }

                    isLoggedIn -> {
                        // APP PRINCIPALE

                        ThemeScreen(
                            themeState = themeState,
                            themeActions = themeViewModel.actions
                        )

                    }

                    !showLogin -> {
                        RegistrationScreen(
                            onRegisterSuccess = {
                                // Una volta registrati, lo stato isRegistered cambierà nel DataStore
                                // e verremo mandati al Login. Resettiamo l'override.
                                showLoginOverride = null
                            },
                            onNavigateToLogin = { showLoginOverride = true },
                            viewModel = authViewModel
                        )
                    }

                    else -> {
                        LoginScreen(
                            onLoginSuccess = { /* isLoggedIn diventerà true */ },
                            onNavigateToRegister = { showLoginOverride = false },
                            onBiometricLogin = { /* TODO */ },
                            viewModel = authViewModel
                        )
                    }
                }
            }
        }
    }
}