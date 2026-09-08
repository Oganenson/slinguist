package com.agon.app.data.repository

import androidx.room.withTransaction
import com.agon.app.data.local.CardEntity
import com.agon.app.data.local.SLinguistDatabase
import com.agon.app.data.local.StatisticsEntity
import com.agon.app.data.local.StudySessionEntity
import com.agon.app.data.model.AppStatistics
import com.agon.app.data.model.Difficulty
import com.agon.app.data.model.LearningLanguage
import com.agon.app.data.model.VocabularyCard
import com.agon.app.data.model.WordType
import java.util.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class CardRepository(private val database: SLinguistDatabase) {
    private val cardDao = database.cardDao()
    private val statisticsDao = database.statisticsDao()
    private val sessionDao = database.studySessionDao()

    val cards: Flow<List<VocabularyCard>> = cardDao.observeAll().map { rows ->
        rows.map { it.toModel() }
    }

    val statistics: Flow<AppStatistics> = combine(
        cardDao.observeTotalCount(),
        cardDao.observeStudiedCount(),
        statisticsDao.observe(),
    ) { total, studied, stored ->
        AppStatistics(
            totalCards = total,
            studiedWords = studied,
            trainingCount = stored?.trainingCount ?: 0,
            currentStreak = effectiveStreak(
                lastActivityAt = stored?.lastActivityAt,
                storedStreak = stored?.currentStreak ?: 0,
            ),
            lastActivityAt = stored?.lastActivityAt,
        )
    }

    suspend fun save(card: VocabularyCard): Long = cardDao.upsert(card.toEntity())

    suspend fun delete(id: Long) = cardDao.deleteById(id)

    suspend fun toggleFavorite(id: Long) = cardDao.toggleFavorite(id)

    suspend fun setStudiedDate(id: Long, timestamp: Long) = cardDao.markStudied(id, timestamp)

    suspend fun recordRating(cardId: Long, timestamp: Long = System.currentTimeMillis()) {
        cardDao.markStudied(cardId, timestamp)
    }

    suspend fun clearAll() = database.withTransaction {
        cardDao.deleteAll()
        statisticsDao.clear()
        sessionDao.deleteAll()
    }

    suspend fun resetProgress() = database.withTransaction {
        cardDao.resetProgress()
        statisticsDao.clear()
        sessionDao.deleteAll()
    }

    suspend fun completeTraining(
        reviewedCount: Int,
        knownCount: Int,
        hardCount: Int,
        unknownCount: Int,
        timestamp: Long = System.currentTimeMillis(),
    ) = database.withTransaction {
        val previous = statisticsDao.get() ?: StatisticsEntity()
        val streak = calculateStreak(previous.lastActivityAt, timestamp, previous.currentStreak)
        statisticsDao.save(
            previous.copy(
                trainingCount = previous.trainingCount + 1,
                currentStreak = streak,
                lastActivityAt = timestamp,
            ),
        )
        sessionDao.insert(
            StudySessionEntity(
                completedAt = timestamp,
                reviewedCount = reviewedCount,
                knownCount = knownCount,
                hardCount = hardCount,
                unknownCount = unknownCount,
            ),
        )
    }

    private fun effectiveStreak(lastActivityAt: Long?, storedStreak: Int): Int {
        if (lastActivityAt == null) return 0
        val currentDay = dayStart(System.currentTimeMillis())
        val lastDay = dayStart(lastActivityAt)
        if (lastDay == currentDay) return storedStreak
        val yesterday = Calendar.getInstance().apply {
            timeInMillis = currentDay
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis
        return if (lastDay == yesterday) storedStreak else 0
    }

    private fun calculateStreak(previous: Long?, now: Long, oldStreak: Int): Int {
        if (previous == null) return 1
        val previousDay = dayStart(previous)
        val currentDay = dayStart(now)
        if (previousDay == currentDay) return oldStreak.coerceAtLeast(1)
        val yesterday = Calendar.getInstance().apply {
            timeInMillis = currentDay
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis
        return if (previousDay == yesterday) oldStreak.coerceAtLeast(0) + 1 else 1
    }

    private fun dayStart(timestamp: Long): Long = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun CardEntity.toModel(): VocabularyCard = VocabularyCard(
    id = id,
    phrase = phrase,
    translation = translation,
    pronunciation = pronunciation,
    language = enumValueOrDefault(language, LearningLanguage.ENGLISH),
    difficulty = enumValueOrDefault(difficulty, Difficulty.MEDIUM),
    wordType = enumValueOrDefault(wordType, WordType.OTHER),
    isFavorite = isFavorite,
    lastStudiedAt = lastStudiedAt,
    createdAt = createdAt,
)

private fun VocabularyCard.toEntity(): CardEntity = CardEntity(
    id = id,
    phrase = phrase.trim(),
    translation = translation.trim(),
    pronunciation = pronunciation.trim(),
    language = language.name,
    difficulty = difficulty.name,
    wordType = wordType.name,
    isFavorite = isFavorite,
    lastStudiedAt = lastStudiedAt,
    createdAt = createdAt,
)

private inline fun <reified T : Enum<T>> enumValueOrDefault(value: String, default: T): T =
    runCatching { enumValueOf<T>(value) }.getOrDefault(default)
