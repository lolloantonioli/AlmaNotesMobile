package com.example.almanotesmobile.data.repositories

import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.local.NoteDao
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {
    fun getTopDownloaded(limit: Int = 3): Flow<List<Note>> = dao.getTopDownloaded(limit)
    fun getLatestUploaded(limit: Int = 3): Flow<List<Note>>  = dao.getLatestUploaded(limit)
    suspend fun insert(note: Note): Long = dao.insert(note)
    suspend fun insertAll(notes: List<Note>) = dao.insertAll(notes)
    suspend fun delete(note: Note) = dao.delete(note)
    suspend fun count(): Int = dao.count()
}