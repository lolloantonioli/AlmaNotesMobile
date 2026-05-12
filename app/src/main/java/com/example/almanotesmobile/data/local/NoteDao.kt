package com.example.almanotesmobile.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY downloadCount DESC LIMIT :limit")
    fun getTopDownloaded(limit: Int): Flow<List<Note>>

    @Query("SELECT * FROM notes ORDER BY uploadedAt DESC LIMIT :limit")
    fun getLatestUploaded(limit: Int): Flow<List<Note>>

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun count(): Int

    @Query("UPDATE notes SET downloadCount = downloadCount + 1 WHERE id = :id")
    suspend fun incrementDownload(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<Note>)

    @Delete
    suspend fun delete(note: Note)
}