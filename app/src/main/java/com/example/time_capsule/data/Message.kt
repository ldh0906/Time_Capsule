package com.example.timecapsule.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String,
    val text: String,
    val createdAt: Long,
    val updatedAt: Long,
    val favorite: Boolean = false,
    val lastShownAt: Long? = null
)