package com.example.time_capsule.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Message::class], version = 3)
abstract class AppDb : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
