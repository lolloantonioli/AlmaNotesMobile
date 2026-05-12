package com.example.almanotesmobile

import android.app.Application
import com.example.almanotesmobile.data.MockData
import com.example.almanotesmobile.data.repositories.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

class AlmaNotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AlmaNotesApplication)
            modules(appModule)
        }

        // Popola il DB solo al primo avvio
        CoroutineScope(Dispatchers.IO).launch {
            val repo = KoinPlatform.getKoin().get<NoteRepository>()
            if (repo.count() == 0) repo.insertAll(MockData.notes)
        }
    }
}