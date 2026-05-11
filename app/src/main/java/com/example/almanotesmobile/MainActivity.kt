package com.example.almanotesmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.almanotesmobile.data.Theme
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

            AlmaNotesMobileTheme(
                darkTheme = when (themeState.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                },
                dynamicColor = themeState.dynamicColor
            ) {
                // Navigazione basata sullo stato
                when {
                    isRegistered == null -> { /* Splash Screen o Caricamento */ }

                    isRegistered == false -> {
                        RegistrationScreen(
                            onRegisterSuccess = { /* Il VM aggiorna isRegistered */ },
                            onNavigateToLogin = { /* Opzionale */ },
                            viewModel = authViewModel
                        )
                    }

                    !isLoggedIn -> {
                        LoginScreen(
                            onLoginSuccess = { /* Il VM aggiorna isLoggedIn */ },
                            onNavigateToRegister = { /* Torna a registrazione */ },
                            onBiometricLogin = { /* Implementare Biometria */ },
                            viewModel = authViewModel
                        )
                    }

                    else -> {
                        // APP PRINCIPALE (Dopo il login)
                        ThemeScreen(
                            themeState = themeState,
                            themeActions = themeViewModel.actions,
                        )
                    }
                }
            }
        }
    }
}