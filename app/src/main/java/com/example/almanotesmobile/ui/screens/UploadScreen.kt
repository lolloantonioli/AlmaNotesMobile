package com.example.almanotesmobile.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.almanotesmobile.R

@Composable
fun UploadScreen(onUploadSuccess: () -> Unit) {
    var fileName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var professor by remember { mutableStateOf("") }
    var degreeCourse by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    
    val almaRed = Color(0xFFBB2E29)
    val cardBg = Color(0xFFFAFAFA)
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Immagine di Sfondo
        Image(
            painter = painterResource(id = R.drawable.sfondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Card Centrale
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icona + Titolo "Carica"
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = null,
                        tint = almaRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Carica",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = almaRed
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Campi di input con stile personalizzato
                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    placeholder = { Text("Nome File", fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(4.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Descrizione", fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(0.dp) // Rende i campi attaccati come nell'immagine
                )

                // Dropdown Professore (Simulato)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, Color.LightGray)
                        .background(Color.White)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (professor.isEmpty()) "Professore" else professor,
                            color = if (professor.isEmpty()) Color.Gray else Color.Black,
                            fontSize = 14.sp
                        )
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = almaRed)
                    }
                }

                // Dropdown Corso di Laurea (Simulato)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, Color.LightGray)
                        .background(Color.White)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (degreeCourse.isEmpty()) "Corso di Laurea" else degreeCourse,
                            color = if (degreeCourse.isEmpty()) Color.Gray else Color.Black,
                            fontSize = 14.sp
                        )
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = almaRed)
                    }
                }

                // Picker del file
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, Color.LightGray)
                        .background(Color.White),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { filePickerLauncher.launch("application/pdf") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.fillMaxHeight().border(1.dp, Color.LightGray),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("Scegli file", color = Color.Black, fontSize = 12.sp)
                    }
                    Text(
                        text = selectedFileUri?.path?.split("/")?.last() ?: "No file chosen",
                        modifier = Modifier.padding(start = 12.dp),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bottone Carica
                Button(
                    onClick = { if (selectedFileUri != null) onUploadSuccess() },
                    colors = ButtonDefaults.buttonColors(containerColor = almaRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text("Carica", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}
