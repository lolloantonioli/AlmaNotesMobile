package com.example.almanotesmobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.almanotesmobile.R
import com.example.almanotesmobile.data.local.Note
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    onOpenNote: (Long) -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val almaRed = Color(0xFFBB2E29)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 1. Barra di Ricerca (Stile come da immagine)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text("Risultato di ricerca", color = Color.Gray, fontSize = 16.sp)
                        }
                        innerTextField()
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 3. Lista Risultati
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(searchResults) { note ->
                SearchNoteItem(note = note, onClick = { onOpenNote(note.id) })
            }
        }
    }
}

@Composable
fun SearchNoteItem(note: Note, onClick: () -> Unit) {
    val almaRed = Color(0xFFBB2E29)
    val textGray = Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // PDF Icon
                Icon(
                    painter = painterResource(id = R.drawable.logo), // Usiamo il logo come segnaposto PDF
                    contentDescription = null,
                    tint = almaRed,
                    modifier = Modifier.size(32.dp)
                )
                
                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = note.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = almaRed, modifier = Modifier.size(14.dp))
                            Text(" ${note.rating}/5", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Text(
                        text = "Prof. ${note.professorName} - ${note.courseName}",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, null, tint = textGray, modifier = Modifier.size(14.dp))
                    Text(" ${note.downloadCount} download", fontSize = 11.sp, color = textGray)
                    
                    Spacer(Modifier.width(12.dp))
                    
                    Icon(Icons.Default.Person, null, tint = textGray, modifier = Modifier.size(14.dp))
                    Text(" Caricato da ${note.uploaderName}", fontSize = 11.sp, color = textGray)
                }

                Text("20 pagine", fontSize = 11.sp, color = Color.Black)
            }
        }
    }
}

// Helper per TextField senza decorazioni pesanti
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
        decorationBox = decorationBox
    )
}
