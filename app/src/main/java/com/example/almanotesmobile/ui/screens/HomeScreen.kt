package com.example.almanotesmobile.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.almanotesmobile.data.local.Note
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

// ─── Utility ─────────────────────────────────────────────────────────────────

private fun Int.toFormattedCount(): String = when {
    this >= 1_000_000 -> String.format(Locale.ITALIAN, "%.1fM", this / 1_000_000f)
    this >= 1_000     -> String.format(Locale.ITALIAN, "%.1fk", this / 1_000f)
    else              -> toString()
}

private fun Long.toRelativeTimeString(): String {
    val diff    = System.currentTimeMillis() - this
    val minutes = diff / 60_000
    val hours   = diff / 3_600_000
    val days    = diff / 86_400_000
    return when {
        diff    <  60_000       -> "Adesso"
        minutes <  60           -> "$minutes minut${if (minutes == 1L) "o" else "i"} fa"
        hours   <  24           -> "$hours or${if (hours == 1L) "a" else "e"} fa"
        days    <   7           -> "$days giorn${if (days == 1L) "o" else "i"} fa"
        else                    -> "${days / 7} settiman${if (days / 7 == 1L) "a" else "e"} fa"
    }
}

// ─── Screen ──────────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onOpenNote: (Long) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    val topDownloaded  by viewModel.topDownloaded.collectAsStateWithLifecycle()
    val latestUploaded by viewModel.latestUploaded.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item { HeroSection(onSearchClick = onSearchClick) }

        item { Spacer(Modifier.height(24.dp)) }

        item {
            SectionHeader(
                icon = Icons.Default.TrendingUp,
                title = "Più scaricati",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item { Spacer(Modifier.height(10.dp)) }
        item {
            NoteListCard(notes = topDownloaded, modifier = Modifier.padding(horizontal = 16.dp)) { note ->
                DownloadedNoteRow(note = note, onClick = { selectedNote = note })
            }
        }

        item { Spacer(Modifier.height(24.dp)) }

        item {
            SectionHeader(
                icon = Icons.Default.Schedule,
                title = "Ultimi caricati",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item { Spacer(Modifier.height(10.dp)) }
        item {
            NoteListCard(notes = latestUploaded, modifier = Modifier.padding(horizontal = 16.dp)) { note ->
                UploadedNoteRow(note = note, onClick = { selectedNote = note })
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

// ─── Hero ─────────────────────────────────────────────────────────────────────

@Composable
private fun HeroSection(onSearchClick: () -> Unit) {
    val almaRed = Color(0xFFBB2E29)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
    ) {
        // Sfondo + archi decorativi bianchi
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = almaRed)

            val stroke = Stroke(width = 2.dp.toPx())
            val white  = Color.White

            drawCircle(white.copy(alpha = 0.18f), radius = size.width * 0.90f,
                center = Offset(size.width * 1.05f,  size.height * 0.05f), style = stroke)
            drawCircle(white.copy(alpha = 0.13f), radius = size.width * 1.10f,
                center = Offset(size.width * 0.70f,  size.height * 1.20f), style = stroke)
            drawCircle(white.copy(alpha = 0.10f), radius = size.width * 0.65f,
                center = Offset(-size.width * 0.05f, size.height * 0.65f), style = stroke)
            drawCircle(white.copy(alpha = 0.08f), radius = size.width * 0.50f,
                center = Offset(size.width * 0.50f, -size.height * 0.20f), style = stroke)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 28.dp, bottom = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Titolo + sottotitolo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Gli appunti giusti\nper ogni esame",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Trova e condividi appunti specifici per ogni\ninsegnamento e professore dell'Unibo",
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }

            // Barra di ricerca (tappabile → SearchScreen)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSearchClick),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cerca",
                        tint = almaRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Cerca per esame, professore o argomento",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// ─── Componenti sezione ───────────────────────────────────────────────────────

@Composable
private fun SectionHeader(icon: ImageVector, title: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null,
            tint = Color(0xFFBB2E29), modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(8.dp))
        Text(text = title, fontWeight = FontWeight.Bold,
            fontSize = 18.sp, color = Color(0xFFBB2E29))
    }
}

@Composable
private fun NoteListCard(
    notes: List<Note>,
    modifier: Modifier = Modifier,
    rowContent: @Composable (Note) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            notes.forEachIndexed { index, note ->
                rowContent(note)
                if (index < notes.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

// ─── Righe delle note ─────────────────────────────────────────────────────────

@Composable
private fun DownloadedNoteRow(note: Note, onClick: () -> Unit) {
    val textSecondary = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = note.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface)
            Text(text = "${note.professorName} - ${note.subject}",
                fontSize = 11.sp, color = textSecondary,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Download, contentDescription = null,
                    modifier = Modifier.size(11.dp), tint = textSecondary)
                Spacer(Modifier.width(3.dp))
                Text(text = "${note.downloadCount.toFormattedCount()} download",
                    fontSize = 11.sp, color = textSecondary)
            }
        }
        Spacer(Modifier.width(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null,
                modifier = Modifier.size(12.dp), tint = Color(0xFFBB2E29).copy(alpha = 0.8f))
            Spacer(Modifier.width(2.dp))
            Text(text = String.format(Locale.ITALIAN, "%.1f", note.rating),
                fontSize = 12.sp, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
        }
    }
}

@Composable
private fun UploadedNoteRow(note: Note, onClick: () -> Unit) {
    val textSecondary = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = note.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface)
            Text(text = "${note.professorName} - ${note.subject}",
                fontSize = 11.sp, color = textSecondary,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null,
                    modifier = Modifier.size(11.dp), tint = textSecondary)
                Spacer(Modifier.width(3.dp))
                Text(text = "Caricato da ${note.uploaderName}",
                    fontSize = 11.sp, color = textSecondary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(text = note.uploadedAt.toRelativeTimeString(),
            fontSize = 12.sp, fontWeight = FontWeight.Medium,
            color = Color(0xFFBB2E29).copy(alpha = 0.85f))
    }
}