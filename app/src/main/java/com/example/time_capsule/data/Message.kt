package com.example.time_capsule.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    indices = [Index("createdAt"), Index("favorite")]
)
data class Message(
    @PrimaryKey val id: String,
    val title: String,
    val text: String,
    val createdAt: Long,
    val updatedAt: Long,
    val favorite: Boolean = false,
    val lastShownAt: Long? = null
)
