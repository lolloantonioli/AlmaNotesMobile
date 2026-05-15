package com.example.almanotesmobile.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository(private val dataStore: DataStore<Preferences>) {
    private object Keys {
        val USERNAME          = stringPreferencesKey("username")
        val EMAIL             = stringPreferencesKey("email")
        val PASSWORD          = stringPreferencesKey("password")
        val IS_REGISTERED     = booleanPreferencesKey("is_registered")
        val IS_LOGGED_IN      = booleanPreferencesKey("is_logged_in")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri")
    }

    val isRegistered: Flow<Boolean> = dataStore.data.map { it[Keys.IS_REGISTERED] ?: false }
    val isLoggedIn: Flow<Boolean>   = dataStore.data.map { it[Keys.IS_LOGGED_IN]  ?: false }
    val biometricEnabled: Flow<Boolean> = dataStore.data.map { it[Keys.BIOMETRIC_ENABLED] ?: false }

    val username: Flow<String> = dataStore.data.map { it[Keys.USERNAME] ?: "" }
    val email: Flow<String> = dataStore.data.map { it[Keys.EMAIL] ?: "" }
    val profileImageUri: Flow<String?> = dataStore.data.map { it[Keys.PROFILE_IMAGE_URI] }

    suspend fun saveRegistration(username: String, email: String, password: String) {
        dataStore.edit { prefs ->
            prefs[Keys.USERNAME]      = username
            prefs[Keys.EMAIL]         = email
            prefs[Keys.PASSWORD]      = password
            prefs[Keys.IS_REGISTERED] = true
        }
    }

    suspend fun updateProfileImage(uri: String) {
        dataStore.edit { it[Keys.PROFILE_IMAGE_URI] = uri }
    }

    suspend fun saveProfileImage(path: String) {
        dataStore.edit { it[Keys.PROFILE_IMAGE_URI] = path }
    }

    suspend fun login(email: String, password: String): Boolean {
        var success = false
        dataStore.edit { prefs ->
            if (prefs[Keys.EMAIL] == email && prefs[Keys.PASSWORD] == password) {
                prefs[Keys.IS_LOGGED_IN] = true
                success = true
            }
        }
        return success
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun loginWithBiometric(): Boolean {
        var isReg = false
        dataStore.edit { prefs ->
            isReg = prefs[Keys.IS_REGISTERED] ?: false
            if (isReg) prefs[Keys.IS_LOGGED_IN] = true
        }
        return isReg
    }

    suspend fun logout() {
        dataStore.edit { it[Keys.IS_LOGGED_IN] = false }
    }
}
