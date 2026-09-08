package com.kapow.slinguist.data.local.room

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.kapow.slinguist.domain.model.Word
import com.kapow.slinguist.domain.model.WordDifficulty

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "foreign_word") val foreign: String,
    val translation: String,
    val pronunciation: String?,
    val difficulty: String,
    val category: String,
    val language: String,
    val isStarred: Boolean,
    val learnedAt: Long?,
    val createdAt: Long,
)

fun WordEntity.toDomain(): Word = Word(
    id = id,
    foreign = foreign,
    translation = translation,
    pronunciation = pronunciation,
    difficulty = WordDifficulty.fromStorage(difficulty),
    category = category,
    language = language,
    isStarred = isStarred,
    learnedAt = learnedAt,
    createdAt = createdAt,
)

fun Word.toEntity(): WordEntity = WordEntity(
    id = id,
    foreign = foreign,
    translation = translation,
    pronunciation = pronunciation,
    difficulty = difficulty.toStorage(),
    category = category,
    language = language,
    isStarred = isStarred,
    learnedAt = learnedAt,
    createdAt = createdAt,
)
