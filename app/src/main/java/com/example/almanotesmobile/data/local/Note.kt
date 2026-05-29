package com.example.almanotesmobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val courseName: String,       // MATERIA
    val professorName: String,    // PROFESSORE
    val subject: String,          // CORSO DI LAUREA
    val filePath: String = "",
    val downloadCount: Int = 0,
    val rating: Float = 0f,
    val ratingCount: Int = 0,     // numero di voti
    val uploaderName: String,
    val uploadedAt: Long = System.currentTimeMillis()
)