package com.example.almanotesmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.repositories.AuthRepository
import com.example.almanotesmobile.data.repositories.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val noteRepository: NoteRepository,
    private val authRepository: AuthRepository   // iniettato direttamente
) : ViewModel() {


    private val username: Flow<String> = authRepository.username


    // I file che hai caricato
    val uploadedNotes: StateFlow<List<Note>> = username
        .flatMapLatest { u ->
            if (u.isBlank()) flowOf(emptyList())
            else noteRepository.getNotesByUploader(u).map { it.take(3) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val uploadedCount: StateFlow<Int> = username
        .flatMapLatest { u ->
            if (u.isBlank()) flowOf(0)
            else noteRepository.countNotesByUploader(u)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    //  I file che hai scaricato con l'account corrente
    val downloadedNotes: StateFlow<List<Note>> = authRepository.downloadedNoteIds
        .flatMapLatest { ids ->
            if (ids.isEmpty()) flowOf(emptyList())
            else noteRepository.getNotesByIds(ids).map { it.take(3) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val downloadedCount: StateFlow<Int> = authRepository.downloadedNoteIds
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    // I più popolari
    val topDownloaded: StateFlow<List<Note>> = noteRepository.getTopDownloaded(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    //  I fan favourites
    val topRated: StateFlow<List<Note>> = noteRepository.getTopRated(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Punti: 1.000 per file caricato + 100 per ogni visualizzazione ricevuta
    val totalPoints: StateFlow<Long> = username
        .flatMapLatest { u ->
            if (u.isBlank()) flowOf(0L)
            else noteRepository.getNotesByUploader(u).map { notes ->
                notes.size.toLong() * 1_000L +
                        notes.sumOf { it.downloadCount.toLong() } * 100L
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)
}