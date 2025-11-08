package com.example.timecapsule.data

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

    suspend fun get(id: String) = withContext(Dispatchers.IO) { dao.findById(id) }

    suspend fun update(id: String, title: String, text: String) = withContext(Dispatchers.IO) {
        val current = dao.findById(id) ?: throw IllegalArgumentException("Message not found")
        val now = System.currentTimeMillis()
        dao.upsert(
            current.copy(
                title = title.trim(),
                text = text.trim(),
                updatedAt = now
            )
        )
    }

    suspend fun delete(id: String) = withContext(Dispatchers.IO) { dao.deleteById(id) }
}
