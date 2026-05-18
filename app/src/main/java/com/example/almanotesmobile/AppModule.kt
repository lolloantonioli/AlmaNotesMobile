package com.example.almanotesmobile

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.almanotesmobile.data.local.NoteDatabase
import com.example.almanotesmobile.data.repositories.AuthRepository
import com.example.almanotesmobile.data.repositories.NoteRepository
import com.example.almanotesmobile.data.repositories.ThemeRepository
import com.example.almanotesmobile.ui.screens.*
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("theme")

val appModule = module {
    // DataStore
    single { get<Context>().dataStore }

    // Theme
    single { ThemeRepository(get()) }
    viewModel { ThemeViewModel(get()) }

    // Auth
    single { AuthRepository(get()) }
    viewModel { AuthViewModel(get()) }

    // Room
    single {
        Room.databaseBuilder(
            get<Context>().applicationContext,
            NoteDatabase::class.java,
            "almanotes.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<NoteDatabase>().noteDao() }
    single { NoteRepository(get()) }
    
    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { PdfViewerViewModel(get()) }
    viewModel { UploadViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { ReviewsViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
}
