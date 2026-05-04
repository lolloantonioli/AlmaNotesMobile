package com.example.almanotesmobile.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import com.example.almanotesmobile.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun AlmaNotesHeader() {
    // Il blu tipico di AlmaNotes (recuperato dai tuoi CSS: #003366 circa)
    val almaBlue = Color(0xFF003366)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(almaBlue)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. LOGO ALMANOTES
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo AlmaNotes",
            modifier = Modifier.height(40.dp) // Regola l'altezza in base al tuo logo
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 2. BARRA DI RICERCA (Stile arrotondato come nell'immagine)
        var searchText by remember { mutableStateOf("") }

        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            placeholder = { Text("Cerca appunti...", color = Color.Gray) },
            leadingIcon = {
                Icon(Icons.Default.Search,
                contentDescription = null, tint = Color.Gray)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(25.dp), // Angoli molto arrotondati
            singleLine = true
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 3. ICONA PROFILO (Userà poi la biometria per l'accesso)
        IconButton(onClick = { /* Azione Profilo */ }) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profilo",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
