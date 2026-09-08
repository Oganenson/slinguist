package com.kapow.slinguist.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    fun observeAllNewest(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words ORDER BY foreign_word COLLATE NOCASE ASC")
    fun observeAllAlphabetical(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words ORDER BY CASE difficulty WHEN 'easy' THEN 0 WHEN 'medium' THEN 1 ELSE 2 END, foreign_word COLLATE NOCASE ASC")
    fun observeAllByDifficulty(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE category = :category ORDER BY createdAt DESC")
    fun observeByCategory(category: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE category = :category ORDER BY foreign_word COLLATE NOCASE ASC")
    fun observeByCategoryAlphabetical(category: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE category = :category ORDER BY CASE difficulty WHEN 'easy' THEN 0 WHEN 'medium' THEN 1 ELSE 2 END, foreign_word COLLATE NOCASE ASC")
    fun observeByCategoryDifficulty(category: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE difficulty = :difficulty ORDER BY createdAt DESC")
    fun observeByDifficulty(difficulty: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE difficulty = :difficulty ORDER BY foreign_word COLLATE NOCASE ASC")
    fun observeByDifficultyAlphabetical(difficulty: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isStarred = 1 ORDER BY createdAt DESC")
    fun observeStarred(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isStarred = 1 ORDER BY foreign_word COLLATE NOCASE ASC")
    fun observeStarredAlphabetical(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isStarred = 1 ORDER BY CASE difficulty WHEN 'easy' THEN 0 WHEN 'medium' THEN 1 ELSE 2 END, foreign_word COLLATE NOCASE ASC")
    fun observeStarredByDifficulty(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE (foreign_word LIKE '%' || :query || '%' COLLATE NOCASE OR translation LIKE '%' || :query || '%' COLLATE NOCASE) ORDER BY createdAt DESC")
    fun searchNewest(query: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE (foreign_word LIKE '%' || :query || '%' COLLATE NOCASE OR translation LIKE '%' || :query || '%' COLLATE NOCASE) ORDER BY foreign_word COLLATE NOCASE ASC")
    fun searchAlphabetical(query: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE (foreign_word LIKE '%' || :query || '%' COLLATE NOCASE OR translation LIKE '%' || :query || '%' COLLATE NOCASE) ORDER BY CASE difficulty WHEN 'easy' THEN 0 WHEN 'medium' THEN 1 ELSE 2 END, foreign_word COLLATE NOCASE ASC")
    fun searchByDifficulty(query: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): WordEntity?

    @Query("SELECT COUNT(*) FROM words WHERE learnedAt IS NOT NULL")
    fun observeLearnedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(word: WordEntity)

    @Query("UPDATE words SET isStarred = :isStarred WHERE id = :id")
    suspend fun setStarred(id: String, isStarred: Boolean)

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM words WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: Collection<String>)
}
