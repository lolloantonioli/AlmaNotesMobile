package com.example.almanotesmobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.utils.getLocallyDownloadedNoteIds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@Composable
fun DownloadedFilesScreen(
    onOpenNote: (Long) -> Unit,
    viewModel: DownloadedFilesViewModel = koinViewModel()
) {
    val downloadedNotes by viewModel.downloadedNotes.collectAsStateWithLifecycle()
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    val almaRed = Color(0xFFBB2E29)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val screenScope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner, context) {
        fun refreshDownloadedNotes() {
            screenScope.launch {
                val ids = withContext(Dispatchers.IO) { getLocallyDownloadedNoteIds(context) }
                viewModel.setDownloadedNoteIds(ids)
            }
        }

        refreshDownloadedNotes()
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshDownloadedNotes()
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Download, contentDescription = null, tint = almaRed)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "I file che hai scaricato",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = almaRed
            )
        }

        Spacer(Modifier.height(16.dp))

        if (downloadedNotes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Non hai ancora scaricato alcun appunto.", color = Color.Gray)
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
        NoteDetailDialog(
            note = note,
            onDismiss = { selectedNote = null },
            onDownload = {
                selectedNote = null
                onOpenNote(note.id)
            }
        )
    }
}