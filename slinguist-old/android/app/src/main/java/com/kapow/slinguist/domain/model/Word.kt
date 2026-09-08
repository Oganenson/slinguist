package com.kapow.slinguist.domain.model

/** Native counterpart of the web application's `Word` type. */
data class Word(
    val id: String,
    val foreign: String,
    val translation: String,
    val pronunciation: String? = null,
    val difficulty: WordDifficulty,
    val category: String,
    val language: String,
    val isStarred: Boolean = false,
    val learnedAt: Long? = null,
    /** Android persistence metadata for the web app's insertion-order "Newest" sort. */
    val createdAt: Long,
)

enum class WordDifficulty {
    EASY,
    MEDIUM,
    HARD;

    companion object {
        fun fromStorage(value: String): WordDifficulty = when (value.lowercase()) {
            "easy" -> EASY
            "medium" -> MEDIUM
            "hard" -> HARD
            else -> MEDIUM
        }
    }

    fun toStorage(): String = name.lowercase()
}

enum class WordSort {
    NEWEST,
    ALPHABETICAL,
    DIFFICULTY,
}
