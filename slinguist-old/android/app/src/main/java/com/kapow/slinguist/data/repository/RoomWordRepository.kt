package com.kapow.slinguist.data.repository

import com.kapow.slinguist.data.local.room.WordDao
import com.kapow.slinguist.data.local.room.toDomain
import com.kapow.slinguist.data.local.room.toEntity
import com.kapow.slinguist.domain.model.Word
import com.kapow.slinguist.domain.model.WordDifficulty
import com.kapow.slinguist.domain.model.WordSort
import com.kapow.slinguist.domain.repository.WordRepository
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomWordRepository(
    private val wordDao: WordDao,
) : WordRepository {
    override fun observeWords(sort: WordSort): Flow<List<Word>> = when (sort) {
        WordSort.NEWEST -> wordDao.observeAllNewest()
        WordSort.ALPHABETICAL -> wordDao.observeAllAlphabetical()
        WordSort.DIFFICULTY -> wordDao.observeAllByDifficulty()
    }.map { words -> words.map { it.toDomain() } }

    override fun observeWordsByCategory(category: String, sort: WordSort): Flow<List<Word>> = when (sort) {
        WordSort.NEWEST -> wordDao.observeByCategory(category)
        WordSort.ALPHABETICAL -> wordDao.observeByCategoryAlphabetical(category)
        WordSort.DIFFICULTY -> wordDao.observeByCategoryDifficulty(category)
    }.map { words -> words.map { it.toDomain() } }

    override fun observeWordsByDifficulty(
        difficulty: WordDifficulty,
        sort: WordSort,
    ): Flow<List<Word>> = when (sort) {
        WordSort.NEWEST -> wordDao.observeByDifficulty(difficulty.toStorage())
        WordSort.ALPHABETICAL -> wordDao.observeByDifficultyAlphabetical(difficulty.toStorage())
        WordSort.DIFFICULTY -> wordDao.observeByDifficulty(difficulty.toStorage())
    }.map { words -> words.map { it.toDomain() } }

    override fun observeStarredWords(sort: WordSort): Flow<List<Word>> = when (sort) {
        WordSort.NEWEST -> wordDao.observeStarred()
        WordSort.ALPHABETICAL -> wordDao.observeStarredAlphabetical()
        WordSort.DIFFICULTY -> wordDao.observeStarredByDifficulty()
    }.map { words -> words.map { it.toDomain() } }

    override fun searchWords(query: String, sort: WordSort): Flow<List<Word>> = when (sort) {
        WordSort.NEWEST -> wordDao.searchNewest(query)
        WordSort.ALPHABETICAL -> wordDao.searchAlphabetical(query)
        WordSort.DIFFICULTY -> wordDao.searchByDifficulty(query)
    }.map { words -> words.map { it.toDomain() } }

    override fun observeLearnedWordCount(): Flow<Int> = wordDao.observeLearnedCount()

    override suspend fun getWord(id: String): Word? = wordDao.getById(id)?.toDomain()

    override suspend fun addWord(word: Word): Word {
        val wordToSave = word.copy(
            id = word.id.ifBlank { UUID.randomUUID().toString() },
            createdAt = word.createdAt.takeIf { it > 0 } ?: System.currentTimeMillis(),
        )
        wordDao.upsert(wordToSave.toEntity())
        return wordToSave
    }

    override suspend fun updateWord(word: Word) {
        wordDao.upsert(word.toEntity())
    }

    override suspend fun setStarred(id: String, isStarred: Boolean) {
        wordDao.setStarred(id, isStarred)
    }

    override suspend fun deleteWord(id: String) {
        wordDao.deleteById(id)
    }

    override suspend fun deleteWords(ids: Collection<String>) {
        if (ids.isNotEmpty()) wordDao.deleteByIds(ids)
    }
}
