package com.example.almanotesmobile.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY downloadCount DESC LIMIT :limit")
    fun getTopDownloaded(limit: Int): Flow<List<Note>>

    @Query("SELECT * FROM notes ORDER BY uploadedAt DESC LIMIT :limit")
    fun getLatestUploaded(limit: Int): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR professorName LIKE '%' || :query || '%' OR courseName LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE uploaderName = :username ORDER BY uploadedAt DESC")
    fun getNotesByUploader(username: String): Flow<List<Note>>

    @Query("SELECT COUNT(*) FROM notes WHERE uploaderName = :username")
    fun countNotesByUploader(username: String): Flow<Int>

    @Query("SELECT * FROM notes ORDER BY rating DESC, ratingCount DESC LIMIT :limit")
    fun getTopRated(limit: Int): Flow<List<Note>>

    @Query("UPDATE notes SET rating = ((rating * ratingCount) + :newRating) / (ratingCount + 1), ratingCount = ratingCount + 1 WHERE id = :id")
    suspend fun updateRating(id: Long, newRating: Int)

    @Query("UPDATE notes SET downloadCount = downloadCount + 1 WHERE id = :id")
    suspend fun incrementDownload(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Query("SELECT * FROM notes WHERE id IN (:ids) ORDER BY uploadedAt DESC")
    fun getNotesByIds(ids: List<Long>): Flow<List<Note>>
}
