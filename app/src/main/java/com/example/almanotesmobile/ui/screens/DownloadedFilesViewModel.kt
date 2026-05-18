package com.example.almanotesmobile.ui.screens

import androidx.lifecycle.ViewModel
import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.repositories.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadedFilesViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val downloadedIds = MutableStateFlow<List<Long>>(emptyList())

    val downloadedNotes: StateFlow<List<Note>> = downloadedIds
        .flatMapLatest { ids ->
            if (ids.isEmpty()) flowOf(emptyList())
            else noteRepository.getNotesByIds(ids)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setDownloadedNoteIds(ids: List<Long>) {
        downloadedIds.value = ids
    }
}