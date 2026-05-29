package com.example.almanotesmobile.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.repositories.AuthRepository
import com.example.almanotesmobile.data.repositories.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class BadgeProgress(
    val uploadedCount: Int = 0,
    val downloadedCount: Int = 0,
    val reviewCount: Int = 0,
    val hasProfileImage: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class BadgesViewModel(
    private val noteRepository: NoteRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val progress: StateFlow<BadgeProgress> = authRepository.username
        .flatMapLatest { username ->
            if (username.isBlank()) {
                flowOf(BadgeProgress())
            } else {
                combine(
                    noteRepository.countNotesByUploader(username),
                    authRepository.profileImageUri,
                    authRepository.reviewCount,
                    authRepository.downloadedNoteIds
                ) { uploadedCount, profileImageUri, reviewCount, downloadedIds ->
                    BadgeProgress(
                        uploadedCount = uploadedCount,
                        downloadedCount = downloadedIds.size,
                        reviewCount = reviewCount,
                        hasProfileImage = !profileImageUri.isNullOrBlank()
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BadgeProgress())
}