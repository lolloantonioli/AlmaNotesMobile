package com.example.almanotesmobile.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository(private val dataStore: DataStore<Preferences>) {
    private object Keys {
        val USERNAME = stringPreferencesKey("username")
        val IS_REGISTERED = booleanPreferencesKey("is_registered")
    }

    // Flusso che indica se l'utente ha già effettuato la registrazione
    val isRegistered: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.IS_REGISTERED] ?: false
    }

    suspend fun saveRegistration(username: String) {
        dataStore.edit { prefs ->
            prefs[Keys.USERNAME] = username
            prefs[Keys.IS_REGISTERED] = true
        }
    }
}