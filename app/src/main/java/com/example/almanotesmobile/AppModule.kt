package com.example.almanotesmobile

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.almanotesmobile.data.repositories.AuthRepository
import com.example.almanotesmobile.data.repositories.ThemeRepository
import com.example.almanotesmobile.ui.screens.AuthViewModel
import com.example.almanotesmobile.ui.screens.ThemeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("theme")

val appModule = module {
    single { get<Context>().dataStore }

    single { ThemeRepository(get()) }

    viewModel { ThemeViewModel(get()) }

    single { AuthRepository(get()) }

    viewModel { AuthViewModel(get()) }
}