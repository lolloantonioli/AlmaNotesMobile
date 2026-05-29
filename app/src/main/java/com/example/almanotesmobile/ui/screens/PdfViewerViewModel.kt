package com.example.almanotesmobile.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.notifications.downloadBadgeDefinitions
import com.example.almanotesmobile.data.notifications.publishCountBadgesIfNew
import com.example.almanotesmobile.data.repositories.AuthRepository
import com.example.almanotesmobile.data.repositories.NoteRepository
import com.example.almanotesmobile.data.repositories.NotificationRepository
import com.example.almanotesmobile.utils.downloadPdfToInternalStorage
import com.example.almanotesmobile.utils.getPdfFile
import com.example.almanotesmobile.utils.markNoteAsDownloaded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi


sealed class PdfViewerState {
    data object Loading : PdfViewerState()
    data class Downloading(val progress: Int) : PdfViewerState()
    data class Rendering(val progress: Int) : PdfViewerState()
    data class Ready(val pages: List<Bitmap>, val note: Note) : PdfViewerState()
    data class Error(val message: String) : PdfViewerState()
}

class PdfViewerViewModel(
    private val repository: NoteRepository,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow<PdfViewerState>(PdfViewerState.Loading)
    val state: StateFlow<PdfViewerState> = _state.asStateFlow()

    fun load(noteId: Long, context: Context) {
        viewModelScope.launch {
            _state.value = PdfViewerState.Loading

            val note = repository.getNoteById(noteId) ?: run {
                _state.value = PdfViewerState.Error("Nota non trovata")
                return@launch
            }

            // 1. Controlliamo se il file esiste già localmente (es. caricato dall'utente)
            val localFile = File(note.filePath)
            val pdfFile = if (localFile.exists() && localFile.isFile) {
                localFile
            } else {
                // Altrimenti usiamo il percorso standard della cache per i download
                getPdfFile(context, noteId)
            }

            val needsDownload = !pdfFile.exists()

            // ── Download (solo se non presente localmente) ──────────────────
            if (needsDownload) {
                if (note.filePath.isBlank() || !note.filePath.startsWith("http")) {
                    _state.value = PdfViewerState.Error("Nessun file disponibile localmente e URL non valido")
                    return@launch
                }

                _state.value = PdfViewerState.Downloading(0)
                val downloaded = downloadPdfToInternalStorage(
                    context    = context,
                    url        = note.filePath,
                    noteId     = noteId,
                    onProgress = { p -> _state.value = PdfViewerState.Downloading(p) }
                )
                if (downloaded == null) {
                    _state.value = PdfViewerState.Error("Download fallito.\nControlla la connessione e riprova.")
                    return@launch
                }
            }

            // ── Rendering pagine ─────────────────────────────────────────────
            _state.value = PdfViewerState.Rendering(0)
            val pages = renderPages(pdfFile) { p -> _state.value = PdfViewerState.Rendering(p) }

            if (pages == null) {
                _state.value = PdfViewerState.Error("Impossibile aprire il file PDF")
            } else {
                // Conteggiamo l'apertura come un download/visualizzazione in ogni caso,
                // anche se il file è stato caricato dall'utente.
                repository.incrementDownload(noteId)
                markNoteAsDownloaded(context, noteId)
                val downloadedCount = authRepository.markNoteAsDownloaded(noteId)
                publishCountBadgesIfNew(
                    authRepository = authRepository,
                    notificationRepository = notificationRepository,
                    count = downloadedCount,
                    badges = downloadBadgeDefinitions
                )
                notificationRepository.publish(
                    title = "Download registrato",
                    message = "Hai scaricato/aperto \"${note.title}\"."
                )
                val currentUsername = authRepository.username.first()
                if (note.uploaderName == currentUsername) {
                    notificationRepository.publish(
                        title = "Nuovo download ricevuto",
                        message = "Il tuo documento \"${note.title}\" è stato scaricato.",
                        sendPush = true
                    )
                }
                _state.value = PdfViewerState.Ready(pages, note)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun saveCurrentPdfToDownloads(noteId: Long, context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            val note = repository.getNoteById(noteId) ?: return@withContext false

            val localFile = File(note.filePath)
            val pdfFile = if (localFile.exists() && localFile.isFile) localFile else getPdfFile(context, noteId)
            if (!pdfFile.exists()) return@withContext false

            val resolver = context.contentResolver
            val safeTitle = note.title.replace(Regex("[^a-zA-Z0-9._-]"), "_").ifBlank { "appunto_$noteId" }
            val fileName = if (safeTitle.endsWith(".pdf", ignoreCase = true)) safeTitle else "$safeTitle.pdf"

            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Downloads.RELATIVE_PATH, "Download/AlmaNotes")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return@withContext false

            resolver.openOutputStream(uri)?.use { output ->
                pdfFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            } ?: return@withContext false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val complete = ContentValues().apply { put(MediaStore.Downloads.IS_PENDING, 0) }
                resolver.update(uri, complete, null, null)
            }

            repository.incrementDownload(noteId)
            markNoteAsDownloaded(context, noteId)
            val downloadedCount = authRepository.markNoteAsDownloaded(noteId)
            publishCountBadgesIfNew(
                authRepository = authRepository,
                notificationRepository = notificationRepository,
                count = downloadedCount,
                badges = downloadBadgeDefinitions
            )
            true
        } catch (_: Exception) {
            false
        }
    }
    private suspend fun renderPages(
        file: File,
        onProgress: (Int) -> Unit
    ): List<Bitmap>? = withContext(Dispatchers.Default) {
        try {
            val fd       = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(fd)
            val pages    = mutableListOf<Bitmap>()

            for (i in 0 until renderer.pageCount) {
                val page  = renderer.openPage(i)
                val scale = 1080f / page.width          // target ~1080px per la qualità
                val bmp   = Bitmap.createBitmap(
                    (page.width  * scale).toInt(),
                    (page.height * scale).toInt(),
                    Bitmap.Config.ARGB_8888
                ).also { it.eraseColor(Color.WHITE) }   // sfondo bianco

                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                pages.add(bmp)
                onProgress((i + 1) * 100 / renderer.pageCount)
            }

            renderer.close()
            fd.close()
            pages
        } catch (e: Exception) {
            null
        }
    }
}