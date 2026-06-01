package com.example.almanotesmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.notifications.profileImageBadgeDefinition
import com.example.almanotesmobile.data.notifications.publishBadgeIfNew
import com.example.almanotesmobile.data.repositories.AuthRepository
import com.example.almanotesmobile.data.repositories.NotificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val isRegistered: StateFlow<Boolean?> = repository.isRegistered
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isLoggedIn: StateFlow<Boolean> = repository.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val biometricEnabled: StateFlow<Boolean> = repository.biometricEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val biometricConsentAsked: StateFlow<Boolean> = repository.biometricConsentAsked
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val username: StateFlow<String> = repository.username
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val email: StateFlow<String> = repository.email
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val profileImageUri: StateFlow<String?> = repository.profileImageUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val password: StateFlow<String> = repository.password
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch { repository.saveRegistration(username, email, password) }
    }
    fun updateProfileImage(uri: String) {
        viewModelScope.launch {
            repository.updateProfileImage(uri)
            publishBadgeIfNew(repository, notificationRepository, profileImageBadgeDefinition)
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.login(email, password)
            onResult(success)
        }
    }


    fun loginWithBiometric(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.loginWithBiometric()
            onResult(success)
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setBiometricEnabled(enabled) }
    }

    fun logout() {
        viewModelScope.launch { repository.logout() }
    }
}