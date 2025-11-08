package com.example.timecapsule.data

import androidx.room.*

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY createdAt DESC")
    suspend fun all(): List<Message>

    @Query("SELECT * FROM messages WHERE text LIKE '%' || :q || '%' ORDER BY createdAt DESC")
    suspend fun search(q: String): List<Message>

    @Query("SELECT * FROM messages WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): Message?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(m: Message)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteById(id: String)
}
