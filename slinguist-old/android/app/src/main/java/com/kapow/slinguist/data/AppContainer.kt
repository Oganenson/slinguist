package com.kapow.slinguist.data

import android.content.Context
import com.kapow.slinguist.data.local.room.SlinguistDatabase
import com.kapow.slinguist.data.repository.DataStoreUserPreferencesRepository
import com.kapow.slinguist.data.repository.RoomWordRepository
import com.kapow.slinguist.domain.repository.UserPreferencesRepository
import com.kapow.slinguist.domain.repository.WordRepository

/** Manual dependency container; Hilt is intentionally deferred. */
class AppContainer(context: Context) {
    private val database = SlinguistDatabase.create(context)

    val wordRepository: WordRepository = RoomWordRepository(database.wordDao())
    val userPreferencesRepository: UserPreferencesRepository =
        DataStoreUserPreferencesRepository(context)
}
