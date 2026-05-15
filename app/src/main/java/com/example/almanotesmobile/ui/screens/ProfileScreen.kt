package com.example.almanotesmobile.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.almanotesmobile.data.Theme
import com.example.almanotesmobile.ui.composables.RadioListItem
import java.io.File

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    themeState: ThemeState,
    themeActions: ThemeActions
) {
    val almaRed = Color(0xFFBB2E29)
    val username by authViewModel.username.collectAsStateWithLifecycle()
    val email by authViewModel.email.collectAsStateWithLifecycle()
    val profileImageUri by authViewModel.profileImageUri.collectAsStateWithLifecycle()
    
    val context = LocalContext.current
    var showImageSourceDialog by remember { mutableStateOf(false) }

    // Launcher per la Galleria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { authViewModel.updateProfileImage(it.toString()) }
    }

    // Launcher per la Fotocamera
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let { authViewModel.updateProfileImage(it.toString()) }
        }
    }

    fun launchCamera() {
        val file = File(context.filesDir, "profile_picture.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        tempImageUri = uri
        cameraLauncher.launch(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titolo "Profilo"
        Row(
            modifier = Modifier.padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Person, null, tint = almaRed, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Text("Profilo", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = almaRed)
        }

        // ── BOX CREDENZIALI ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Parte Sinistra: Immagine Profilo
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0F0F0))
                            .border(1.dp, Color.LightGray, CircleShape)
                            .clickable { showImageSourceDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageUri != null) {
                            AsyncImage(
                                model = profileImageUri,
                                contentDescription = "Foto Profilo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddAPhoto, null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                                Text("Aggiungi\nimmagine", fontSize = 10.sp, textAlign = TextAlign.Center, color = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(Modifier.width(24.dp))

                // Parte Destra: Credenziali
                Column(modifier = Modifier.weight(1f)) {
                    CredentialRow(icon = Icons.Default.AlternateEmail, label = "Username", value = username)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))
                    CredentialRow(icon = Icons.Outlined.Email, label = "E-mail", value = email)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))
                    CredentialRow(icon = Icons.Outlined.Lock, label = "Password", value = "••••••••••••", isPassword = true)
                }
            }
        }
    }

    // Dialog per scegliere la sorgente dell'immagine
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Immagine Profilo") },
            text = { Text("Scegli da dove caricare la tua foto") },
            confirmButton = {
                TextButton(onClick = { 
                    showImageSourceDialog = false
                    galleryLauncher.launch("image/*") 
                }) {
                    Text("Galleria")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showImageSourceDialog = false
                    launchCamera()
                }) {
                    Text("Fotocamera")
                }
            }
        )
    }
}

@Composable
fun CredentialRow(icon: ImageVector, label: String, value: String, isPassword: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 14.sp, color = Color.Black)
        }
        if (isPassword) {
            Icon(Icons.Outlined.Visibility, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        }
    }
}
