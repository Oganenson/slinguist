package com.agon.app.data.repository

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.agon.app.data.model.LearningLanguage
import com.agon.app.data.model.ThemeMode
import com.agon.app.data.model.UserProfile
import com.agon.app.viewmodel.AppSettings
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.slinguistDataStore by preferencesDataStore(name = "user_preferences")

class PreferencesRepository(private val context: Context) {
    private object Keys {
        val name = stringPreferencesKey("profile_name")
        val email = stringPreferencesKey("profile_email")
        val avatarPath = stringPreferencesKey("profile_avatar_path")
        val darkTheme = booleanPreferencesKey("dark_theme")
        val themeMode = stringPreferencesKey("theme_mode")
        val defaultLanguage = stringPreferencesKey("default_language")
        val dailyGoal = intPreferencesKey("daily_goal")
        val showPronunciation = booleanPreferencesKey("show_pronunciation")
        val autoReveal = booleanPreferencesKey("auto_reveal")
        val shuffleTraining = booleanPreferencesKey("shuffle_training")
    }

    val profile: Flow<UserProfile> = context.slinguistDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { values ->
            UserProfile(
                name = values[Keys.name] ?: "Лингвист",
                email = values[Keys.email] ?: "",
                avatarPath = values[Keys.avatarPath] ?: "",
                darkTheme = values[Keys.themeMode] == ThemeMode.DARK.name,
            )
        }

    val themeMode: Flow<ThemeMode> = context.slinguistDataStore.data
        .map { values ->
            val modeStr = values[Keys.themeMode] ?: ThemeMode.SYSTEM.name
            try { ThemeMode.valueOf(modeStr) } catch (e: Exception) { ThemeMode.SYSTEM }
        }

    val appSettings: Flow<AppSettings> = context.slinguistDataStore.data
        .map { values ->
            AppSettings(
                themeMode = try { ThemeMode.valueOf(values[Keys.themeMode] ?: ThemeMode.SYSTEM.name) } catch (e: Exception) { ThemeMode.SYSTEM },
                defaultLanguage = try { LearningLanguage.valueOf(values[Keys.defaultLanguage] ?: LearningLanguage.ENGLISH.name) } catch (e: Exception) { LearningLanguage.ENGLISH },
                dailyGoal = values[Keys.dailyGoal] ?: 10,
                showPronunciation = values[Keys.showPronunciation] ?: true,
                autoReveal = values[Keys.autoReveal] ?: false,
                shuffleTraining = values[Keys.shuffleTraining] ?: true
            )
        }

    suspend fun updateProfile(name: String, email: String) {
        context.slinguistDataStore.edit { values ->
            values[Keys.name] = name.trim().ifBlank { "Лингвист" }
            values[Keys.email] = email.trim()
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.slinguistDataStore.edit { it[Keys.themeMode] = mode.name }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.slinguistDataStore.edit { it[Keys.darkTheme] = enabled }
    }

    suspend fun setDefaultLanguage(language: LearningLanguage) {
        context.slinguistDataStore.edit { it[Keys.defaultLanguage] = language.name }
    }

    suspend fun setDailyGoal(goal: Int) {
        context.slinguistDataStore.edit { it[Keys.dailyGoal] = goal }
    }

    suspend fun setShowPronunciation(show: Boolean) {
        context.slinguistDataStore.edit { it[Keys.showPronunciation] = show }
    }

    suspend fun setAutoReveal(auto: Boolean) {
        context.slinguistDataStore.edit { it[Keys.autoReveal] = auto }
    }

    suspend fun setShuffleTraining(shuffle: Boolean) {
        context.slinguistDataStore.edit { it[Keys.shuffleTraining] = shuffle }
    }

    suspend fun saveAvatar(uri: Uri) = withContext(Dispatchers.IO) {
        val profileDir = File(context.filesDir, "profile").apply { mkdirs() }
        val target = File(profileDir, "avatar_${System.currentTimeMillis()}.jpg")
        val temp = File(profileDir, "avatar.tmp")
        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Не удалось открыть изображение" }
            temp.outputStream().use { output -> input.copyTo(output) }
        }
        check(temp.renameTo(target)) { "Не удалось сохранить изображение" }
        profileDir.listFiles()?.filter { it != target }?.forEach { it.delete() }
        context.slinguistDataStore.edit { it[Keys.avatarPath] = target.absolutePath }
    }

    suspend fun removeAvatar() = withContext(Dispatchers.IO) {
        File(context.filesDir, "profile").listFiles()?.forEach { it.delete() }
        context.slinguistDataStore.edit { it.remove(Keys.avatarPath) }
    }
}
