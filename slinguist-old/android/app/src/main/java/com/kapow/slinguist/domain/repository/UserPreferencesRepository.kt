package com.kapow.slinguist.domain.repository

import com.kapow.slinguist.domain.model.AppLanguage
import com.kapow.slinguist.domain.model.AppTheme
import com.kapow.slinguist.domain.model.ReviewSessionConfig
import com.kapow.slinguist.domain.model.StoredUserStats
import com.kapow.slinguist.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun observeProfile(): Flow<UserProfile>
    fun observeLanguage(): Flow<AppLanguage>
    fun observeTheme(): Flow<AppTheme>
    fun observeReviewSessionConfig(): Flow<ReviewSessionConfig>
    fun observeStoredStats(): Flow<StoredUserStats>

    suspend fun updateProfile(profile: UserProfile)
    suspend fun setLanguage(language: AppLanguage)
    suspend fun setTheme(theme: AppTheme)
    suspend fun setReviewSessionConfig(config: ReviewSessionConfig)
    suspend fun updateStoredStats(stats: StoredUserStats)
}
