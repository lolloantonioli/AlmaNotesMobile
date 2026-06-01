package com.example.almanotesmobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.ui.viewmodel.ReviewsViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun Long.toFormattedDate(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(this))

@Composable
fun ReviewsScreen(
    viewModel: ReviewsViewModel = koinViewModel()
) {
    val downloadedNotes by viewModel.downloadedNotes.collectAsStateWithLifecycle()
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    val almaRed = Color(0xFFBB2E29)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = almaRed)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Recensisci ciò che hai scaricato",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = almaRed
            )
        }

        Spacer(Modifier.height(16.dp))

        if (downloadedNotes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Non hai ancora scaricato alcun appunto.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(downloadedNotes) { note ->
                    ReviewNoteItem(note = note, onClick = { selectedNote = note })
                }
            }
        }
    }

    selectedNote?.let { note ->
        RatingDialog(
            note = note,
            onDismiss = { selectedNote = null },
            onSendReview = { rating ->
                viewModel.rateNote(note.id, rating)
                selectedNote = null
            }
        )
    }
}

@Composable
fun ReviewNoteItem(note: Note, onClick: () -> Unit) {
    val almaRed = Color(0xFFBB2E29)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = almaRed
            )
            Text(
                text = "Prof. ${note.professorName} - ${note.courseName}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${note.downloadCount} download", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Il: ${note.uploadedAt.toFormattedDate()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun RatingDialog(
    note: Note,
    onDismiss: () -> Unit,
    onSendReview: (Int) -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }
    val almaRed = Color(0xFFBB2E29)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = almaRed, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Lascia una Recensione", color = almaRed, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = Color.Gray)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = note.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = almaRed,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Prof. ${note.professorName} - ${note.courseName}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                Text("Quanto è utile questo appunto?", fontSize = 16.sp)

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (i <= rating) Color(0xFFFFB800) else Color.LightGray,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { rating = i }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (rating > 0) {
                    val label = when(rating) {
                        1 -> "Poco utile"
                        2 -> "Sufficiente"
                        3 -> "Buono"
                        4 -> "Molto Buono"
                        5 -> "Eccellente"
                        else -> ""
                    }
                    Text(text = "$label ($rating/5)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                }

                Spacer(Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Annulla", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(
                        onClick = { if (rating > 0) onSendReview(rating) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = almaRed),
                        shape = RoundedCornerShape(8.dp),
                        enabled = rating > 0
                    ) {
                        Text("Invia Recensione")
                    }
                }
            }
        }
    }
}