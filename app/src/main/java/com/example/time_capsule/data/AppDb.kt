// data/AppDb.kt
package com.example.timecapsule.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Message::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
