package com.example.almanotesmobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.almanotesmobile.data.Theme
import com.example.almanotesmobile.ui.composables.RadioListItem

@Composable
fun ThemeScreen(
    themeState: ThemeState,
    themeActions: ThemeActions
) {
    // Usiamo i colori del tema di Material 3 per lo sfondo
    val backgroundColor = MaterialTheme.colorScheme.background
    // Definiamo il colore "AlmaNotes" per gli accenti (titolo, icona, ecc.)
    val almaRed = Color(0xFFBB2E29)

    Scaffold(
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
        ) {

            // Intestazione "Tema" con Icona (rimane invariata dal design precedente)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = almaRed,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Tema",
                    color = almaRed,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                    // fontFamily = merriweatherSans // Se hai configurato il font
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            // Gruppo di Radio Button STANDARD
            Column(modifier = Modifier.selectableGroup()) {
                Theme.entries.forEach { theme ->
                    RadioListItem(
                        label = theme.toString(),
                        selected = (theme == themeState.theme),
                        onClick = { themeActions.setTheme(theme) }
                    )
                }
            }
        }
    }
}