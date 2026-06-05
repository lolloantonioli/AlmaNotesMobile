package com.example.almanotesmobile.ui.composables

import androidx.compose.foundation.layout.size
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
        // HOME
        NavigationBarItem(
            icon = { Icon(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "Home",
                modifier = Modifier.size(24.dp)
            ) },
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

        // CERCA
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

        // CARICA
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.img_1),
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

        // RECENSIONI
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.img_2),
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

        // PROFILO
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
