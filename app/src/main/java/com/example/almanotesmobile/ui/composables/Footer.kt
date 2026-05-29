package com.example.almanotesmobile.ui.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.almanotesmobile.R
import com.example.almanotesmobile.ui.navigation.Route

@Composable
fun AlmaNotesFooter(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        // 1. HOME
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentDestination?.route?.contains("Home") == true,
            onClick = {
                navController.navigate(Route.Home) {
                    popUpTo(Route.Home) { inclusive = false }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // 2. CERCA
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Cerca",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Cerca") },
            selected = currentDestination?.route?.contains("Search") == true,
            onClick = {
                navController.navigate(Route.Search) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // 3. CARICA (+)
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Carica",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Carica") },
            selected = currentDestination?.route?.contains("Upload") == true,
            onClick = {
                navController.navigate(Route.Upload) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // 4. RECENSIONI
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.favourites),
                    contentDescription = "Recensioni",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Recensioni") },
            selected = currentDestination?.route?.contains("Reviews") == true,
            onClick = {
                navController.navigate(Route.Reviews) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // 5. PROFILO
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profilo",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Profilo") },
            selected = currentDestination?.route?.contains("Profile") == true,
            onClick = {
                navController.navigate(Route.Profile) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}
