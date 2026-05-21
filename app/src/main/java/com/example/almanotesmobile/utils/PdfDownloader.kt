package com.example.almanotesmobile.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/** Scarica il PDF nella cartella privata dell'app (non visibile in Downloads). */

private const val DOWNLOADED_NOTES_PREFS = "downloaded_notes"
private const val DOWNLOADED_NOTE_IDS_KEY = "downloaded_note_ids"

suspend fun downloadPdfToInternalStorage(
    context: Context,
    url: String,
    noteId: Long,
    onProgress: (Int) -> Unit
): File? = withContext(Dispatchers.IO) {
    try {
        val pdfDir = File(context.filesDir, "pdfs").apply { mkdirs() }
        val dest   = File(pdfDir, "$noteId.pdf")

        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = 15_000
        conn.readTimeout    = 30_000
        conn.connect()

        val total = conn.contentLength.toLong()
        var done  = 0L

        conn.inputStream.use { input ->
            FileOutputStream(dest).use { output ->
                val buf = ByteArray(8 * 1024)
                var read: Int
                while (input.read(buf).also { read = it } != -1) {
                    output.write(buf, 0, read)
                    done += read
                    if (total > 0) onProgress((done * 100 / total).toInt())
                }
            }
        }
        markNoteAsDownloaded(context, noteId)
        dest
    } catch (e: Exception) {
        null
    }
}

/** Restituisce il File in cache per un dato noteId. */
fun getPdfFile(context: Context, noteId: Long): File =
    File(context.filesDir, "pdfs/$noteId.pdf")


fun markNoteAsDownloaded(context: Context, noteId: Long) {
    val prefs = context.getSharedPreferences(DOWNLOADED_NOTES_PREFS, Context.MODE_PRIVATE)
    val updatedIds = prefs.getStringSet(DOWNLOADED_NOTE_IDS_KEY, emptySet())
        .orEmpty()
        .toMutableSet()
        .apply { add(noteId.toString()) }

    prefs.edit()
        .putStringSet(DOWNLOADED_NOTE_IDS_KEY, updatedIds)
        .commit()
}

/** Restituisce gli ID delle note di cui l'utente ha un PDF in cache locale. */
fun getLocallyDownloadedNoteIds(context: Context): List<Long> {
    val prefs = context.getSharedPreferences(DOWNLOADED_NOTES_PREFS, Context.MODE_PRIVATE)
    val persistedIds = prefs.getStringSet(DOWNLOADED_NOTE_IDS_KEY, emptySet())
        .orEmpty()
        .mapNotNull { it.toLongOrNull() }
    val pdfDir = File(context.filesDir, "pdfs")
    val cachedIds = if (!pdfDir.exists()) {
        emptyList()
    } else {
        pdfDir.listFiles()
            ?.mapNotNull { it.nameWithoutExtension.toLongOrNull() }
            ?: emptyList()
    }

    return (persistedIds + cachedIds).distinct()
}