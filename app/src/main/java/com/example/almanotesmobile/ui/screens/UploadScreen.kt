package com.example.almanotesmobile.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import org.koin.androidx.compose.koinViewModel

@Composable
fun UploadScreen(
    onUploadSuccess: () -> Unit,
    viewModel: UploadViewModel = koinViewModel()
) {
    var fileName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var professor by remember { mutableStateOf("") }
    var degreeCourse by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    
    val almaRed = Color(0xFFBB2E29)
    val cardBg = Color(0xFFFAFAFA)
    val borderColor = Color.LightGray.copy(alpha = 0.5f)
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedFileUri = uri }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Sfondo
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
                .padding(32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icona e Titolo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Upload, null, tint = almaRed, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Carica", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = almaRed)
                }

                Spacer(Modifier.height(24.dp))

                // BLOCCO CAMPI UNITI
                Column(modifier = Modifier.fillMaxWidth().border(1.dp, borderColor)) {
                    
                    InputFieldPlaceholder(value = fileName, onValueChange = { fileName = it }, placeholder = "Nome File")
                    
                    Divider(color = borderColor)
                    InputFieldPlaceholder(value = description, onValueChange = { description = it }, placeholder = "Descrizione")
                    
                    Divider(color = borderColor)
                    DropdownPlaceholder(text = if (professor.isEmpty()) "Professore" else professor, arrowColor = almaRed)
                    
                    Divider(color = borderColor)
                    DropdownPlaceholder(text = if (degreeCourse.isEmpty()) "Corso di Laurea" else degreeCourse, arrowColor = almaRed)
                    
                    Divider(color = borderColor)
                    Row(
                        modifier = Modifier.fillMaxWidth().height(45.dp).background(Color.White),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxHeight().width(100.dp).border(0.5.dp, borderColor),
                            onClick = { filePickerLauncher.launch("application/pdf") },
                            color = Color(0xFFF5F5F5)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("Scegli file", fontSize = 12.sp, color = Color.Black)
                            }
                        }
                        Text(
                            text = selectedFileUri?.path?.split("/")?.last() ?: "No file chosen",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 12.dp),
                            maxLines = 1
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Bottone Carica
                Button(
                    onClick = {
                        if (selectedFileUri != null && fileName.isNotBlank()) {
                            viewModel.uploadNote(
                                context = context,
                                uri = selectedFileUri!!,
                                title = fileName,
                                description = description,
                                professor = professor.ifEmpty { "Generico" },
                                course = degreeCourse.ifEmpty { "Generico" },
                                onSuccess = onUploadSuccess
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = almaRed),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Carica", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InputFieldPlaceholder(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth().height(45.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun DropdownPlaceholder(text: String, arrowColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().height(45.dp).background(Color.White).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text, fontSize = 14.sp, color = Color.Gray)
        Icon(Icons.Default.KeyboardArrowDown, null, tint = arrowColor)
    }
}
