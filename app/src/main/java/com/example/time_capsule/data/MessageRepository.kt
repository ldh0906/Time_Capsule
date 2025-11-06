package com.example.time_capsule.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class MessageRepository(private val dao: MessageDao) {
    suspend fun all() = withContext(Dispatchers.IO) { dao.all() }
    suspend fun search(q: String) = withContext(Dispatchers.IO) { dao.search(q) }
    suspend fun add(title: String, text: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dao.upsert(
            Message(
                id = UUID.randomUUID().toString(),
                title = title.trim(),
                text = text.trim(),
                createdAt = now,
                updatedAt = now
            )
        )
    }
}
