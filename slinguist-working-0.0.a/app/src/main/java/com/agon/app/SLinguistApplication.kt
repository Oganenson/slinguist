package com.agon.app

import android.app.Application
import com.agon.app.data.local.SLinguistDatabase
import com.agon.app.data.repository.CardRepository
import com.agon.app.data.repository.PreferencesRepository

class SLinguistApplication : Application() {
    val database: SLinguistDatabase by lazy { SLinguistDatabase.getInstance(this) }
    val cardRepository: CardRepository by lazy { CardRepository(database) }
    val preferencesRepository: PreferencesRepository by lazy { PreferencesRepository(this) }
}
