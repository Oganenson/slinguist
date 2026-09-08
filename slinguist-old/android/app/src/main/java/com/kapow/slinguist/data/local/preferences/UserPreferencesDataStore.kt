package com.kapow.slinguist.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

internal val Context.userPreferencesDataStore by preferencesDataStore(name = "slinguist_preferences")
