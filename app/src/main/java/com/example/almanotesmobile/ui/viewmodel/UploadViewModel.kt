package com.example.almanotesmobile.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.notifications.publishCountBadgesIfNew
import com.example.almanotesmobile.data.notifications.uploadBadgeDefinitions
import com.example.almanotesmobile.data.repositories.AuthRepository
import com.example.almanotesmobile.data.repositories.NoteRepository
import com.example.almanotesmobile.data.repositories.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class UploadViewModel(
    private val repository: NoteRepository,
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun uploadNote(
        context: Context,
        uri: Uri,
        title: String,
        professor: String,
        course: String,
        uploaderName: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val filePath = saveFileToInternalStorage(context, uri, title)
            if (filePath != null) {
                val newNote = Note(
                    title = title,
                    courseName = course,
                    professorName = professor,
                    subject = course,
                    filePath = filePath,
                    uploaderName = uploaderName,
                    uploadedAt = System.currentTimeMillis()
                )
                repository.insert(newNote)
                val uploadedCount = repository.getNotesByUploader(uploaderName).first().size
                publishCountBadgesIfNew(
                    authRepository = authRepository,
                    notificationRepository = notificationRepository,
                    count = uploadedCount,
                    badges = uploadBadgeDefinitions
                )
                notificationRepository.publish(
                    title = "Upload completato",
                    message = "Hai caricato \"$title\" con successo."
                )
                onSuccess()
            }
        }
    }

    private suspend fun saveFileToInternalStorage(context: Context, uri: Uri, fileName: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(
                    context.filesDir,
                    "${fileName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
                )
                val outputStream = FileOutputStream(file)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}