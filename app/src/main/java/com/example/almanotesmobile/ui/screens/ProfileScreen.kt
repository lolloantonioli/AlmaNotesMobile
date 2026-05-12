package com.example.almanotesmobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
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
fun ProfileScreen(
    authViewModel: AuthViewModel,
    themeState: ThemeState,
    themeActions: ThemeActions
) {
    val almaRed = Color(0xFFBB2E29)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Intestazione Profilo
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = almaRed,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Il Tuo Profilo",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = almaRed
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Sezione Impostazioni Tema (integrata qui per comodità)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Personalizzazione Tema",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Theme.entries.forEach { theme ->
                    RadioListItem(
                        label = when(theme) {
                            Theme.Light -> "Chiaro"
                            Theme.Dark -> "Scuro"
                            Theme.System -> "Sistema"
                        },
                        selected = (theme == themeState.theme),
                        onClick = { themeActions.setTheme(theme) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(32.dp))

        // BOTTONE LOGOUT
        Button(
            onClick = { authViewModel.logout() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = almaRed),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Logout",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
