package com.example.almanotesmobile.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.repositories.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReviewsViewModel(private val repository: NoteRepository) : ViewModel() {

    val downloadedNotes: StateFlow<List<Note>> = repository.getDownloadedNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun rateNote(noteId: Long, rating: Int) {
        viewModelScope.launch {
            repository.updateRating(noteId, rating)
        }
    }
}
