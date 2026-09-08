package com.kapow.slinguist.data.local.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordDaoTest {
    private lateinit var database: SlinguistDatabase
    private lateinit var dao: WordDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SlinguistDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.wordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertSearchFilterAndBulkDelete_workAsExpected() = runBlocking {
        dao.upsert(word(id = "1", foreign = "Bonjour", category = "Greeting", difficulty = "easy", starred = true, createdAt = 10))
        dao.upsert(word(id = "2", foreign = "Bibliothèque", category = "Noun", difficulty = "medium", createdAt = 20))
        dao.upsert(word(id = "3", foreign = "Comprendre", category = "Verb", difficulty = "hard", createdAt = 30))

        assertEquals(listOf("Comprendre", "Bibliothèque", "Bonjour"), dao.observeAllNewest().first().map { it.foreign })
        assertEquals(listOf("Bonjour"), dao.searchNewest("jour").first().map { it.foreign })
        assertEquals(listOf("Noun"), dao.observeByDifficulty("medium").first().map { it.category })
        assertEquals(listOf("Comprendre"), dao.observeByCategory("Verb").first().map { it.foreign })

        dao.setStarred("2", true)
        assertEquals(2, dao.observeStarred().first().size)

        dao.deleteByIds(listOf("1", "3"))
        val remaining = dao.observeAllAlphabetical().first()
        assertEquals(listOf("2"), remaining.map { it.id })
        assertTrue(remaining.single().isStarred)
    }

    private fun word(
        id: String,
        foreign: String,
        category: String,
        difficulty: String,
        starred: Boolean = false,
        createdAt: Long,
    ) = WordEntity(
        id = id,
        foreign = foreign,
        translation = "translation",
        pronunciation = null,
        difficulty = difficulty,
        category = category,
        language = "French",
        isStarred = starred,
        learnedAt = null,
        createdAt = createdAt,
    )
}
