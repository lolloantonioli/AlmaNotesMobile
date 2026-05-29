package com.example.almanotesmobile.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import com.example.almanotesmobile.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.almanotesmobile.ui.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavController, showBack: Boolean, onBack: () -> Unit) {
    val backgroundColor = Color(0xFFBB2E29)
    val merriweatherSans = FontFamily(
        Font(R.font.merriweathersans_variablefont_wght, FontWeight.Normal),
    )

    CenterAlignedTopAppBar(
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro",
                        tint = backgroundColor
                    )
                }
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo AlmaNotes",
                    modifier = Modifier.height(32.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "AlmaNotes",
                    color = backgroundColor,
                    fontFamily = merriweatherSans,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate(Route.Theme) {
                launchSingleTop = true
                restoreState = true
            } }) {
                Icon(
                    painter = painterResource(R.drawable.theme),
                    contentDescription = "Theme",
                    tint = backgroundColor
                )
            }
            IconButton(onClick = {
                navController.navigate(Route.Notifications) {
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifiche",
                    tint = backgroundColor
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}