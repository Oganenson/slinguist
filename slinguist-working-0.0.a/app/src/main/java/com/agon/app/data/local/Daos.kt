package com.agon.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CardEntity>>

    @Upsert
    suspend fun upsert(card: CardEntity): Long

    @Delete
    suspend fun delete(card: CardEntity)

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE cards SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleFavorite(id: Long)

    @Query("UPDATE cards SET lastStudiedAt = :timestamp WHERE id = :id")
    suspend fun markStudied(id: Long, timestamp: Long)

    @Query("SELECT COUNT(*) FROM cards")
    fun observeTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cards WHERE lastStudiedAt IS NOT NULL")
    fun observeStudiedCount(): Flow<Int>

    @Query("DELETE FROM cards")
    suspend fun deleteAll()

    @Query("UPDATE cards SET lastStudiedAt = NULL")
    suspend fun resetProgress()
}

@Dao
interface StatisticsDao {
    @Query("SELECT * FROM app_statistics WHERE id = 1")
    fun observe(): Flow<StatisticsEntity?>

    @Query("SELECT * FROM app_statistics WHERE id = 1")
    suspend fun get(): StatisticsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(statistics: StatisticsEntity)

    @Query("DELETE FROM app_statistics")
    suspend fun clear()
}

@Dao
interface StudySessionDao {
    @Insert
    suspend fun insert(session: StudySessionEntity)

    @Query("DELETE FROM study_sessions")
    suspend fun deleteAll()
}
