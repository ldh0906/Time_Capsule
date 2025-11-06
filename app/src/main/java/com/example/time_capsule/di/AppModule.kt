// di/AppModule.kt
package com.example.timecapsule.di

import android.content.Context
import androidx.room.Room
import com.example.timecapsule.data.AppDb

object AppModule {
    fun db(ctx: Context) = Room.databaseBuilder(
        ctx,
        AppDb::class.java,
        "timecapsule.db"
    ).build()
}
