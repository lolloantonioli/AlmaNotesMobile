package com.example.almanotesmobile.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.repositories.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Il ViewModel riceve il Repository tramite "Dependency Injection" (es. usando Koin)
class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    // 1. LO STATO: Trasformiamo il Flow del DataStore in uno StateFlow
    // Questo permette alla UI di "osservarlo" e aggiornarsi automaticamente
    val isRegistered: StateFlow<Boolean?> = repository.isRegistered
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // 'null' significa che stiamo ancora caricando
        )

    // 2. L'AZIONE: La funzione chiamata quando l'utente clicca il bottone "Registrati"
    fun register(username: String) {
        // Usiamo viewModelScope perché il salvataggio nel DataStore
        // è un'operazione "sospesa" (suspend) e deve avvenire in background
        viewModelScope.launch {
            repository.saveRegistration(username)
        }
    }
}