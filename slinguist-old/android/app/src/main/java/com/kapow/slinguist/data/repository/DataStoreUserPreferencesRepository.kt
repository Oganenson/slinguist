package com.kapow.slinguist.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.kapow.slinguist.data.local.preferences.userPreferencesDataStore
import com.kapow.slinguist.domain.model.AppLanguage
import com.kapow.slinguist.domain.model.AppTheme
import com.kapow.slinguist.domain.model.ReviewSessionConfig
import com.kapow.slinguist.domain.model.StoredUserStats
import com.kapow.slinguist.domain.model.UserProfile
import com.kapow.slinguist.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreUserPreferencesRepository(
    context: Context,
) : UserPreferencesRepository {
    private val dataStore = context.applicationContext.userPreferencesDataStore

    override fun observeProfile(): Flow<UserProfile> = dataStore.data.map { preferences ->
        UserProfile(
            name = preferences[Keys.profileName] ?: UserProfile().name,
            email = preferences[Keys.profileEmail] ?: UserProfile().email,
            avatarUri = preferences[Keys.profileAvatarUri] ?: UserProfile().avatarUri,
        )
    }

    override fun observeLanguage(): Flow<AppLanguage> = dataStore.data.map { preferences ->
        preferences[Keys.appLanguage].asLanguage()
    }

    override fun observeTheme(): Flow<AppTheme> = dataStore.data.map { preferences ->
        preferences[Keys.appTheme].asTheme()
    }

    override fun observeReviewSessionConfig(): Flow<ReviewSessionConfig> = dataStore.data.map { preferences ->
        ReviewSessionConfig(
            length = preferences[Keys.sessionLength] ?: 15,
            difficulty = preferences[Keys.sessionDifficulty] ?: "All",
            categories = preferences[Keys.sessionCategories]?.toList() ?: listOf("All"),
        )
    }

    override fun observeStoredStats(): Flow<StoredUserStats> = dataStore.data.map { preferences ->
        StoredUserStats(
            streak = preferences[Keys.streak] ?: 1,
            globalRank = preferences[Keys.globalRank] ?: "Gold",
            lastActiveEpochDay = preferences[Keys.lastActiveEpochDay],
        )
    }

    override suspend fun updateProfile(profile: UserProfile) {
        dataStore.edit { preferences ->
            preferences[Keys.profileName] = profile.name
            preferences[Keys.profileEmail] = profile.email
            preferences[Keys.profileAvatarUri] = profile.avatarUri
        }
    }

    override suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { it[Keys.appLanguage] = language.name }
    }

    override suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { it[Keys.appTheme] = theme.name }
    }

    override suspend fun setReviewSessionConfig(config: ReviewSessionConfig) {
        dataStore.edit { preferences ->
            preferences[Keys.sessionLength] = config.length
            preferences[Keys.sessionDifficulty] = config.difficulty
            preferences[Keys.sessionCategories] = config.categories.toSet()
        }
    }

    override suspend fun updateStoredStats(stats: StoredUserStats) {
        dataStore.edit { preferences ->
            preferences[Keys.streak] = stats.streak
            preferences[Keys.globalRank] = stats.globalRank
            stats.lastActiveEpochDay?.let { preferences[Keys.lastActiveEpochDay] = it }
                ?: preferences.remove(Keys.lastActiveEpochDay)
        }
    }

    private object Keys {
        val profileName = stringPreferencesKey("profile_name")
        val profileEmail = stringPreferencesKey("profile_email")
        val profileAvatarUri = stringPreferencesKey("profile_avatar_uri")
        val appLanguage = stringPreferencesKey("app_language")
        val appTheme = stringPreferencesKey("app_theme")
        val sessionLength = androidx.datastore.preferences.core.intPreferencesKey("session_length")
        val sessionDifficulty = stringPreferencesKey("session_difficulty")
        val sessionCategories = stringSetPreferencesKey("session_categories")
        val streak = androidx.datastore.preferences.core.intPreferencesKey("streak")
        val globalRank = stringPreferencesKey("global_rank")
        val lastActiveEpochDay = longPreferencesKey("last_active_epoch_day")
    }
}

private fun String?.asLanguage(): AppLanguage = runCatching {
    AppLanguage.valueOf(this ?: AppLanguage.EN.name)
}.getOrDefault(AppLanguage.EN)

private fun String?.asTheme(): AppTheme = runCatching {
    AppTheme.valueOf(this ?: AppTheme.LIGHT.name)
}.getOrDefault(AppTheme.LIGHT)
