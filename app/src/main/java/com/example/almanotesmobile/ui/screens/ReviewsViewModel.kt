package com.example.almanotesmobile.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.repositories.AuthRepository
import com.example.almanotesmobile.data.repositories.NoteRepository
import com.example.almanotesmobile.data.repositories.NotificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReviewsViewModel(
    private val repository: NoteRepository,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val downloadedNotes: StateFlow<List<Note>> = repository.getDownloadedNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun rateNote(noteId: Long, rating: Int) {
        viewModelScope.launch {
            repository.updateRating(noteId, rating)
            authRepository.incrementReviewCount()
            val note = repository.getNoteById(noteId)
            notificationRepository.publish(
                title = "Recensione inviata",
                message = "Hai recensito \"${note?.title ?: "appunto"}\" con $rating stelle."
            )
            note?.let {
                notificationRepository.publish(
                    title = "Push candidata: nuova recensione",
                    message = "Un utente ha recensito il tuo file \"${it.title}\".",
                    isPushCandidate = true
                )
            }
        }
    }
}
