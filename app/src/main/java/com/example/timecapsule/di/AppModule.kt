package com.example.timecapsule.di

import android.content.Context
import androidx.room.Room
import com.example.timecapsule.data.AppDb

object AppModule {
    private var _db: AppDb? = null
    fun db(ctx: Context) = _db ?: Room.databaseBuilder(
        ctx.applicationContext,
        AppDb::class.java,
        "timecapsule.db"
    )
        .fallbackToDestructiveMigration()
        .build()
        .also { _db = it }
}
