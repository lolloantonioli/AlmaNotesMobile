package com.example.almanotesmobile.ui.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    noteId: Long,
    onBack: () -> Unit,
    viewModel: PdfViewerViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state   by viewModel.state.collectAsStateWithLifecycle()
    val screenScope = rememberCoroutineScope()

    LaunchedEffect(noteId) { viewModel.load(noteId, context) }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── AppBar ─────────────────────────────────────────────────────────
        TopAppBar(
            title = {
                Text(
                    text = (state as? PdfViewerState.Ready)?.note?.title ?: "PDF",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro"
                    )
                }
            },
            actions = {
                val ready = state is PdfViewerState.Ready
                IconButton(
                    onClick = {
                        if (ready) {
                            screenScope.launch {
                                val ok = viewModel.saveCurrentPdfToDownloads(noteId, context)
                                Toast.makeText(
                                    context,
                                    if (ok) "PDF salvato in Download/AlmaNotes" else "Impossibile salvare il PDF",
                                    if (ok) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    enabled = ready
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Scarica PDF")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // ── Contenuto ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val s = state) {
                is PdfViewerState.Loading      -> PdfLoading("Caricamento…", null)
                is PdfViewerState.Downloading  -> PdfLoading("Scaricamento… ${s.progress}%", s.progress / 100f)
                is PdfViewerState.Rendering    -> PdfLoading("Elaborazione… ${s.progress}%", s.progress / 100f)
                is PdfViewerState.Ready        -> PdfPages(s.pages)
                is PdfViewerState.Error        -> PdfError(s.message) {
                    viewModel.load(noteId, context)
                }
            }
        }
    }
}

// ─── Pagine ───────────────────────────────────────────────────────────────────

@Composable
private fun PdfPages(pages: List<Bitmap>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        itemsIndexed(pages) { _, bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

// ─── Loading ──────────────────────────────────────────────────────────────────

@Composable
private fun PdfLoading(message: String, progress: Float?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (progress != null) {
            CircularProgressIndicator(
                progress = { progress },
                color = Color(0xFFBB2E29),
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                modifier = Modifier.size(56.dp)
            )
        } else {
            CircularProgressIndicator(
                color = Color(0xFFBB2E29),
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(text = message, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
    }
}

// ─── Errore ───────────────────────────────────────────────────────────────────

@Composable
private fun PdfError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = Color(0xFFBB2E29),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB2E29))
        ) {
            Text("Riprova", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}