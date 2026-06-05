package com.example.almanotesmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.repositories.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repository: NoteRepository) : ViewModel() {

    val topDownloaded: StateFlow<List<Note>> = repository.getTopDownloaded(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val latestUploaded: StateFlow<List<Note>> = repository.getLatestUploaded(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

}