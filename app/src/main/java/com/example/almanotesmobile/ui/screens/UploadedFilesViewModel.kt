package com.example.almanotesmobile.ui.screens

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
class UploadedFilesViewModel(
    private val noteRepository: NoteRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val uploadedNotes: StateFlow<List<Note>> = authRepository.username
        .flatMapLatest { username ->
            if (username.isBlank()) flowOf(emptyList())
            else noteRepository.getNotesByUploader(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}