package com.example.almanotesmobile.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository(private val dataStore: DataStore<Preferences>) {
    private object Keys {
        val USERNAME = stringPreferencesKey("username")
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD = stringPreferencesKey("password")
        val IS_REGISTERED = booleanPreferencesKey("is_registered")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val isRegistered: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.IS_REGISTERED] ?: false
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.IS_LOGGED_IN] ?: false
    }

    suspend fun saveRegistration(username: String, email: String, password: String) {
        dataStore.edit { prefs ->
            prefs[Keys.USERNAME] = username
            prefs[Keys.EMAIL] = email
            prefs[Keys.PASSWORD] = password
            prefs[Keys.IS_REGISTERED] = true
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        var success = false
        dataStore.edit { prefs ->
            val savedEmail = prefs[Keys.EMAIL]
            val savedPassword = prefs[Keys.PASSWORD]
            if (savedEmail == email && savedPassword == password) {
                prefs[Keys.IS_LOGGED_IN] = true
                success = true
            }
        }
        return success
    }

    suspend fun logout() {
        dataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = false
        }
    }
}
