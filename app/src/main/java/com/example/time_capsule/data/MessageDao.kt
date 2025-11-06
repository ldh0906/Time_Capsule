// data/MessageDao.kt
package com.example.timecapsule.data

import androidx.room.*

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY createdAt DESC")
    suspend fun all(): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(m: Message)
}
