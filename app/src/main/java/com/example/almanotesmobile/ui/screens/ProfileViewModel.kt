package com.example.almanotesmobile.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.repositories.NoteRepository
import kotlinx.coroutines.flow.*

class ProfileViewModel(private val repository: NoteRepository) : ViewModel() {

    fun getUploadedNotes(username: String): StateFlow<List<Note>> = repository.getNotesByUploader(username)
        .map { it.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getUploadedCount(username: String): StateFlow<Int> = repository.countNotesByUploader(username)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
        
    fun getDownloadedNotes(): StateFlow<List<Note>> = repository.getDownloadedNotes()
        .map { it.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTopDownloaded(): StateFlow<List<Note>> = repository.getTopDownloaded(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    //fun getTopRated(): StateFlow<List<Note>> = repository.getTopRated(3)
      //  .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
