package com.example.almanotesmobile.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.repositories.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _username = MutableStateFlow("")

    fun setUsername(username: String) {
        if (_username.value != username) _username.value = username
    }

    // Caricati: filtrati per il tuo username reale
    val uploadedNotes: StateFlow<List<Note>> = _username
        .flatMapLatest { u ->
            if (u.isBlank()) flowOf(emptyList())
            else repository.getNotesByUploader(u).map { it.take(3) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val uploadedCount: StateFlow<Int> = _username
        .flatMapLatest { u ->
            if (u.isBlank()) flowOf(0)
            else repository.countNotesByUploader(u)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // Scaricati: tutti i file che hanno almeno 1 download (visto che l'app è locale)
    val downloadedNotes: StateFlow<List<Note>> = repository.getDownloadedNotes()
        .map { it.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val downloadedCount: StateFlow<Int> = repository.getDownloadedNotes()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val topDownloaded: StateFlow<List<Note>> = repository.getTopDownloaded(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val topRated: StateFlow<List<Note>> = repository.getTopRated(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalPoints: StateFlow<Long> = _username
        .flatMapLatest { u ->
            if (u.isBlank()) flowOf(0L)
            else repository.getNotesByUploader(u).map { notes ->
                notes.size.toLong() * 1000L + notes.sumOf { it.downloadCount.toLong() } * 100L
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)
}
