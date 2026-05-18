package com.example.almanotesmobile.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.repositories.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _username      = MutableStateFlow("")
    private val _downloadedIds = MutableStateFlow<List<Long>>(emptyList())

    fun setUsername(username: String) {
        if (_username.value != username) _username.value = username
    }

    fun setDownloadedNoteIds(ids: List<Long>) {
        _downloadedIds.value = ids
    }

    // ── File caricati dall'utente ──────────────────────────────────────────────
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

    // ── File scaricati (PDF in cache locale) ──────────────────────────────────
    val downloadedNotes: StateFlow<List<Note>> = _downloadedIds
        .flatMapLatest { ids ->
            if (ids.isEmpty()) flowOf(emptyList())
            else repository.getNotesByIds(ids).map { it.take(3) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val downloadedCount: StateFlow<Int> = _downloadedIds
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // ── I più popolari ────────────────────────────────────────────────────────
    val topDownloaded: StateFlow<List<Note>> = repository.getTopDownloaded(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── I fan favourites (top rated) ──────────────────────────────────────────
    val topRated: StateFlow<List<Note>> = repository.getTopRated(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Punti: 1.000 per file caricato + 100 per ogni visualizzazione ricevuta ─
    val totalPoints: StateFlow<Long> = _username
        .flatMapLatest { u ->
            if (u.isBlank()) flowOf(0L)
            else repository.getNotesByUploader(u).map { notes ->
                notes.size.toLong() * 1_000L +
                        notes.sumOf { it.downloadCount.toLong() } * 100L
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)
}