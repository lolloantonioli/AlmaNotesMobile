package com.example.almanotesmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.notifications.publishCountBadgesIfNew
import com.example.almanotesmobile.data.notifications.reviewBadgeDefinitions
import com.example.almanotesmobile.data.repositories.AuthRepository
import com.example.almanotesmobile.data.repositories.NoteRepository
import com.example.almanotesmobile.data.repositories.NotificationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewsViewModel(
    private val repository: NoteRepository,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val downloadedNotes: StateFlow<List<Note>> = authRepository.downloadedNoteIds
        .flatMapLatest { ids ->
            if (ids.isEmpty()) flowOf(emptyList())
            else repository.getNotesByIds(ids)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun rateNote(noteId: Long, rating: Int) {
        viewModelScope.launch {
            repository.updateRating(noteId, rating)
            val reviewCount = authRepository.markNoteReviewed(noteId)
            publishCountBadgesIfNew(
                authRepository = authRepository,
                notificationRepository = notificationRepository,
                count = reviewCount,
                badges = reviewBadgeDefinitions
            )
            val note = repository.getNoteById(noteId)
            notificationRepository.publish(
                title = "Recensione inviata",
                message = "Hai recensito \"${note?.title ?: "appunto"}\" con $rating stelle."
            )
            val currentUsername = authRepository.username.first()
            if (note != null && note.uploaderName == currentUsername) {
                notificationRepository.publish(
                    title = "Nuova recensione ricevuta",
                    message = "Il tuo documento \"${note.title}\" ha ricevuto una recensione da $rating stelle.",
                    sendPush = true
                )
            }
        }
    }
}