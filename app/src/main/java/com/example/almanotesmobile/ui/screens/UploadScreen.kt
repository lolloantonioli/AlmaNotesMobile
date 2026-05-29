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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.almanotesmobile.R
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.Text


@Composable
fun UploadScreen(
    onUploadSuccess: () -> Unit,
    authViewModel: AuthViewModel,
    viewModel: UploadViewModel = koinViewModel()
) {
    var fileName by remember { mutableStateOf("") }
    var professor by remember { mutableStateOf("") }
    var degreeCourse by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    val uploaderName by authViewModel.username.collectAsStateWithLifecycle()

    val almaRed = Color(0xFFBB2E29)
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = Color.LightGray.copy(alpha = 0.5f)
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedFileUri = uri }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.sfondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Upload, null, tint = almaRed, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Carica", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = almaRed)
                }

                Spacer(Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth().border(1.dp, borderColor)) {
                    InputFieldPlaceholder(value = fileName, onValueChange = { fileName = it }, placeholder = "Nome Appunto")
                    HorizontalDivider(color = borderColor)
                    InputFieldPlaceholder(value = professor, onValueChange = { professor = it }, placeholder = "Professore (Es. Rossi)")
                    HorizontalDivider(color = borderColor)
                    InputFieldPlaceholder(value = degreeCourse, onValueChange = { degreeCourse = it }, placeholder = "Corso di Laurea (Es. Informatica)")
                    HorizontalDivider(color = borderColor)

                    Row(
                        modifier = Modifier.fillMaxWidth().height(45.dp).background(MaterialTheme.colorScheme.surface),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxHeight().width(100.dp).border(0.5.dp, borderColor),
                            onClick = { filePickerLauncher.launch("application/pdf") },
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("Scegli file", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        Text(
                            text = selectedFileUri?.path?.split("/")?.last() ?: "Nessun file selezionato",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 12.dp),
                            maxLines = 1
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (selectedFileUri != null && fileName.isNotBlank()) {
                            viewModel.uploadNote(
                                context = context,
                                uri = selectedFileUri!!,
                                title = fileName,
                                professor = professor.ifEmpty { "Generico" },
                                course = degreeCourse.ifEmpty { "Generico" },
                                uploaderName = uploaderName, // Username REALE
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
fun InputFieldPlaceholder(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        singleLine = true
    )
}