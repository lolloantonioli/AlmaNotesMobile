package com.example.almanotesmobile.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.repositories.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    val isRegistered: StateFlow<Boolean?> = repository.isRegistered
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isLoggedIn: StateFlow<Boolean> = repository.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val biometricEnabled: StateFlow<Boolean> = repository.biometricEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val username: StateFlow<String> = repository.username
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val email: StateFlow<String> = repository.email
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val profileImageUri: StateFlow<String?> = repository.profileImageUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch { repository.saveRegistration(username, email, password) }
    }

    fun registerWithProvider(provider: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.registerWithProvider(provider)
            onResult(true)
        }
    }

    fun updateProfileImage(uri: String) {
        viewModelScope.launch { repository.updateProfileImage(uri) }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.login(email, password)
            if (success) repository.setBiometricEnabled(true) // abilita biometria al primo login
            onResult(success)
        }
    }

    fun loginWithProvider(provider: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.loginWithProvider(provider)
            onResult(success)
        }
    }

    fun loginWithGoogle(
        googleId: String,
        displayName: String,
        email: String,
        photoUrl: String?,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.loginWithGoogle(googleId, displayName, email, photoUrl)
            if (success) repository.setBiometricEnabled(true)
            onResult(success)
        }
    }

    fun loginWithBiometric(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.loginWithBiometric()
            onResult(success)
        }
    }

    fun logout() {
        viewModelScope.launch { repository.logout() }
    }
}
