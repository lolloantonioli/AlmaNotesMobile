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
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.almanotesmobile.data.Theme
import com.example.almanotesmobile.ui.composables.AlmaNotesFooter
import com.example.almanotesmobile.ui.composables.AppBar
import com.example.almanotesmobile.ui.navigation.Route
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

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            AlmaNotesMobileTheme(
                darkTheme = when (themeState.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                },
                dynamicColor = themeState.dynamicColor
            ) {
                // Se non sappiamo ancora se l'utente è registrato, aspettiamo
                if (isRegistered == null) return@AlmaNotesMobileTheme

                val startDestination = if (!isRegistered!!) {
                    Route.Registration
                } else if (!isLoggedIn) {
                    Route.Login
                } else {
                    Route.Home
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { 
                        // Mostriamo la barra solo se loggati (o dove serve)
                        if (isLoggedIn) AppBar(navController = navController)
                    },
                    bottomBar = { 
                        // Mostriamo il footer solo nelle schermate principali
                        if (isLoggedIn) {
                            AlmaNotesFooter(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<Route.Registration> {
                            RegistrationScreen(
                                onRegisterSuccess = { 
                                    navController.navigate(Route.Login) {
                                        popUpTo(Route.Registration) { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = { navController.navigate(Route.Login) },
                                viewModel = authViewModel
                            )
                        }

                        composable<Route.Login> {
                            LoginScreen(
                                onLoginSuccess = { 
                                    navController.navigate(Route.Home) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = { navController.navigate(Route.Registration) },
                                onBiometricLogin = { /* TODO */ },
                                viewModel = authViewModel
                            )
                        }

                        composable<Route.Home> {
                            // Placeholder per la Home
                            Greeting(name = "AlmaNotes Home")
                        }

                        composable<Route.Search> {
                            Greeting(name = "Cerca")
                        }

                        composable<Route.Favourites> {
                            Greeting(name = "Preferiti")
                        }

                        composable<Route.Rewards> {
                            Greeting(name = "Premi")
                        }

                        composable<Route.Theme> {
                            ThemeScreen(
                                themeState = themeState,
                                themeActions = themeViewModel.actions
                            )
                        }

                        composable<Route.Profile> {
                            Greeting(name = "Profilo")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = "Benvenuto in $name!",
        modifier = modifier
    )
}
