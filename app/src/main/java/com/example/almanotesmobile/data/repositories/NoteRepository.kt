package com.example.almanotesmobile.data.repositories

import com.example.almanotesmobile.data.local.Note
import com.example.almanotesmobile.data.local.NoteDao
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {
    fun getTopDownloaded(limit: Int = 3): Flow<List<Note>> = dao.getTopDownloaded(limit)
    fun getLatestUploaded(limit: Int = 3): Flow<List<Note>>  = dao.getLatestUploaded(limit)
    suspend fun getNoteById(id: Long): Note? = dao.getNoteById(id)
    fun searchNotes(query: String): Flow<List<Note>> = dao.searchNotes(query)
    fun getDownloadedNotes(): Flow<List<Note>> = dao.getDownloadedNotes()
    fun getNotesByUploader(username: String): Flow<List<Note>> = dao.getNotesByUploader(username)
    fun countNotesByUploader(username: String): Flow<Int> = dao.countNotesByUploader(username)

    fun getTopRated(limit: Int = 3): Flow<List<Note>>   = dao.getTopRated(limit)
    fun getNotesByIds(ids: List<Long>): Flow<List<Note>> = dao.getNotesByIds(ids)
    
    suspend fun updateRating(id: Long, rating: Int) = dao.updateRating(id, rating)
    suspend fun insert(note: Note): Long  = dao.insert(note)
    suspend fun insertAll(notes: List<Note>) = dao.insertAll(notes)
    suspend fun delete(note: Note)          = dao.delete(note)
    suspend fun count(): Int                = dao.count()
    suspend fun incrementDownload(id: Long) = dao.incrementDownload(id)
}
