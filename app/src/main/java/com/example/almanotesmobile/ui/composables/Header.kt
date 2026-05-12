package com.example.almanotesmobile.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
fun AppBar(navController: NavController) {
    val backgroundColor = Color(0xFFBB2E29)
    val merriweatherSans = FontFamily(
        Font(R.font.merriweathersans_variablefont_wght, FontWeight.Normal),
    )

    CenterAlignedTopAppBar(
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
            IconButton(onClick = { navController.navigate(Route.Rewards) {
                launchSingleTop = true
                restoreState = true
            } }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Theme",
                    tint = backgroundColor
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
