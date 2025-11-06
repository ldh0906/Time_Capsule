package com.example.time_capsule.data

import androidx.room.*

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY createdAt DESC")
    suspend fun all(): List<Message>

    @Query("SELECT * FROM messages WHERE text LIKE '%' || :q || '%' ORDER BY createdAt DESC")
    suspend fun search(q: String): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(m: Message)
}
