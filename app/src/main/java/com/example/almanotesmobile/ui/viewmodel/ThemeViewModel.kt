package com.example.almanotesmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.almanotesmobile.data.Theme
import com.example.almanotesmobile.data.repositories.ThemeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ThemeState(
    val theme: Theme,
    val dynamicColor: Boolean
)

data class ThemeActions(
    val setTheme: (Theme) -> Unit,
    val setDynamicColor: (Boolean) -> Unit
)

class ThemeViewModel(repository: ThemeRepository) : ViewModel() {
    val state = combine(
        repository.theme,
        repository.dynamicColor
    ) { theme, dynamicColor -> ThemeState(theme, dynamicColor) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ThemeState(Theme.System, false)
        )

    val actions = ThemeActions(
        setTheme = { theme ->
            viewModelScope.launch { repository.setTheme(theme)  }
        },
        setDynamicColor = { enabled ->
            viewModelScope.launch { repository.setDynamicColor(enabled) }
        }
    )
}
