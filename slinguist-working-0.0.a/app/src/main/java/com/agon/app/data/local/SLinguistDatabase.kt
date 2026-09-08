package com.agon.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CardEntity::class, StatisticsEntity::class, StudySessionEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class SLinguistDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun statisticsDao(): StatisticsDao
    abstract fun studySessionDao(): StudySessionDao

    companion object {
        @Volatile private var instance: SLinguistDatabase? = null

        fun getInstance(context: Context): SLinguistDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SLinguistDatabase::class.java,
                    "slinguist.db",
                ).build().also { instance = it }
            }
    }
}
