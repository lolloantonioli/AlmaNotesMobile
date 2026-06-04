package com.example.almanotesmobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val courseName: String,
    val professorName: String,
    val subject: String,
    val filePath: String = "",
    val downloadCount: Int = 0,
    val rating: Float = 0f,
    val ratingCount: Int = 0,
    val uploaderName: String,
    val uploadedAt: Long = System.currentTimeMillis()
)
