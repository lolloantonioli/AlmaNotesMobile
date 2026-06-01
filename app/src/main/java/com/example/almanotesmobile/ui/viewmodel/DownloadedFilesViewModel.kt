package com.example.almanotesmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.repositories.AuthRepository
import com.example.almanotesmobile.data.repositories.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadedFilesViewModel(
    private val noteRepository: NoteRepository,
    authRepository: AuthRepository
) : ViewModel() {

    val downloadedNotes: StateFlow<List<Note>> = authRepository.downloadedNoteIds
        .flatMapLatest { ids ->
            if (ids.isEmpty()) flowOf(emptyList())
            else noteRepository.getNotesByIds(ids)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

}