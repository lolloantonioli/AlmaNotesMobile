package com.example.almanotesmobile

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.almanotesmobile.data.Theme
import com.example.almanotesmobile.ui.composables.AlmaNotesFooter
import com.example.almanotesmobile.ui.composables.AppBar
import com.example.almanotesmobile.ui.navigation.Route
import com.example.almanotesmobile.ui.screens.*
import com.example.almanotesmobile.ui.theme.AlmaNotesMobileTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : FragmentActivity() {
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
            val destination = navBackStackEntry?.destination
            
            // Verifichiamo se nascondere le barre (es. nel visualizzatore PDF o login)
            val isPdfViewer = destination?.hasRoute<Route.PdfViewer>() ?: false

            AlmaNotesMobileTheme(
                darkTheme = when (themeState.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                },
                dynamicColor = themeState.dynamicColor
            ) {
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
                        if (isLoggedIn && !isPdfViewer) AppBar(navController = navController)
                    },
                    bottomBar = { 
                        if (isLoggedIn && !isPdfViewer) {
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
                            HomeScreen(
                                onSearchClick = {
                                    navController.navigate(Route.Search) { launchSingleTop = true }
                                },
                                onOpenNote = { noteId -> navController.navigate(Route.PdfViewer(noteId)) }
                            )
                        }

                        composable<Route.Search> {
                            SearchScreen(
                                onOpenNote = { noteId -> navController.navigate(Route.PdfViewer(noteId)) }
                            )
                        }

                        composable<Route.Upload> {
                            UploadScreen(
                                onUploadSuccess = {
                                    navController.navigate(Route.Home) {
                                        popUpTo(Route.Home) { inclusive = false }
                                    }
                                },
                                authViewModel = authViewModel
                            )
                        }

                        composable<Route.Reviews> {
                            ReviewsScreen()
                        }

                        composable<Route.DownloadedFiles> {
                            DownloadedFilesScreen(
                                onOpenNote = { noteId -> navController.navigate(Route.PdfViewer(noteId)) }
                            )
                        }

                        composable<Route.UploadedFiles> {
                            UploadedFilesScreen(
                                onOpenNote = { noteId -> navController.navigate(Route.PdfViewer(noteId)) }
                            )
                        }

                        composable<Route.Badges> {
                            BadgesScreen()
                        }

                        composable<Route.Theme> {
                            ThemeScreen(
                                themeState = themeState,
                                themeActions = themeViewModel.actions
                            )
                        }

                        composable<Route.Profile> {
                            ProfileScreen(
                                authViewModel = authViewModel,
                                onOpenNote    = { noteId -> navController.navigate(Route.PdfViewer(noteId)) },
                                onNavigateToUploaded = { navController.navigate(Route.UploadedFiles) },
                                onNavigateToDownloaded = { navController.navigate(Route.DownloadedFiles) },
                                onNavigateToBadges = { navController.navigate(Route.Badges) }
                            )
                        }

                        composable<Route.PdfViewer> { backStackEntry ->
                            val pdfRoute: Route.PdfViewer = backStackEntry.toRoute()
                            PdfViewerScreen(
                                noteId = pdfRoute.noteId,
                                onBack = { navController.popBackStack() }
                            )
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
