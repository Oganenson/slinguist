package com.kapow.slinguist.domain.repository

import com.kapow.slinguist.domain.model.Word
import com.kapow.slinguist.domain.model.WordDifficulty
import com.kapow.slinguist.domain.model.WordSort
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun observeWords(sort: WordSort = WordSort.NEWEST): Flow<List<Word>>
    fun observeWordsByCategory(category: String, sort: WordSort = WordSort.NEWEST): Flow<List<Word>>
    fun observeWordsByDifficulty(
        difficulty: WordDifficulty,
        sort: WordSort = WordSort.NEWEST,
    ): Flow<List<Word>>
    fun observeStarredWords(sort: WordSort = WordSort.NEWEST): Flow<List<Word>>
    fun searchWords(query: String, sort: WordSort = WordSort.NEWEST): Flow<List<Word>>
    fun observeLearnedWordCount(): Flow<Int>

    suspend fun getWord(id: String): Word?
    suspend fun addWord(word: Word): Word
    suspend fun updateWord(word: Word)
    suspend fun setStarred(id: String, isStarred: Boolean)
    suspend fun deleteWord(id: String)
    suspend fun deleteWords(ids: Collection<String>)
}
