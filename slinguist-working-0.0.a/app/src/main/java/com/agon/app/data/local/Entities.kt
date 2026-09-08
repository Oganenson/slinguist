package com.agon.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    indices = [Index("language"), Index("difficulty"), Index("wordType"), Index("isFavorite")],
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phrase: String,
    val translation: String,
    val pronunciation: String,
    val language: String,
    val difficulty: String,
    val wordType: String,
    val isFavorite: Boolean,
    val lastStudiedAt: Long?,
    val createdAt: Long,
)

@Entity(tableName = "app_statistics")
data class StatisticsEntity(
    @PrimaryKey val id: Int = 1,
    val trainingCount: Int = 0,
    val currentStreak: Int = 0,
    val lastActivityAt: Long? = null,
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val completedAt: Long,
    val reviewedCount: Int,
    val knownCount: Int,
    val hardCount: Int,
    val unknownCount: Int,
)
