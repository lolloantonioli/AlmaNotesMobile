package com.example.almanotesmobile.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.repositories.NoteRepository
import com.example.almanotesmobile.utils.downloadPdfToInternalStorage
import com.example.almanotesmobile.utils.getPdfFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import com.example.almanotesmobile.utils.markNoteAsDownloaded

sealed class PdfViewerState {
    data object Loading : PdfViewerState()
    data class Downloading(val progress: Int) : PdfViewerState()
    data class Rendering(val progress: Int) : PdfViewerState()
    data class Ready(val pages: List<Bitmap>, val note: Note) : PdfViewerState()
    data class Error(val message: String) : PdfViewerState()
}

class PdfViewerViewModel(private val repository: NoteRepository) : ViewModel() {

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
                _state.value = PdfViewerState.Ready(pages, note)
            }
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
