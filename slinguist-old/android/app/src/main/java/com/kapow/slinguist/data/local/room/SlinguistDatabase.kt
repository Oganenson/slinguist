package com.kapow.slinguist.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WordEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class SlinguistDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        fun create(context: Context): SlinguistDatabase = Room.databaseBuilder(
            context.applicationContext,
            SlinguistDatabase::class.java,
            "slinguist.db",
        ).build()
    }
}
