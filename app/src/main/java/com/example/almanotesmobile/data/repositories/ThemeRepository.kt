package com.example.almanotesmobile.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.almanotesmobile.data.Theme
import kotlinx.coroutines.flow.map

class ThemeRepository(
    dataStore: DataStore<Preferences>
) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
    }

    val theme = dataStore.data.map { preferences ->
        try {
            Theme.valueOf(preferences[THEME_KEY] ?: "System")
        } catch (_: Exception) {
            Theme.System
        }
    }
}
