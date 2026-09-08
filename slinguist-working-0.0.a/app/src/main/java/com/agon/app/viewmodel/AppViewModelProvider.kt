package com.agon.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.agon.app.SLinguistApplication
import com.agon.app.data.model.LearningLanguage
import com.agon.app.data.model.ThemeMode
import com.agon.app.data.model.TrainingConfig
import com.agon.app.data.model.TrainingPhase
import com.agon.app.data.model.TrainingRating
import com.agon.app.data.model.TrainingUiState
import com.agon.app.data.model.UserProfile
import com.agon.app.data.model.VocabularyCard
import com.agon.app.data.repository.CardRepository
import com.agon.app.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? SLinguistApplication
            AppViewModel(
                app?.cardRepository ?: throw IllegalStateException("App must be SLinguistApplication"),
                app?.preferencesRepository ?: throw IllegalStateException("App must be SLinguistApplication")
            )
        }
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? SLinguistApplication
            HomeViewModel(
                app?.cardRepository ?: throw IllegalStateException("App must be SLinguistApplication"),
                app?.preferencesRepository ?: throw IllegalStateException("App must be SLinguistApplication")
            )
        }
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? SLinguistApplication
            SettingsViewModel(
                app?.cardRepository ?: throw IllegalStateException("App must be SLinguistApplication"),
                app?.preferencesRepository ?: throw IllegalStateException("App must be SLinguistApplication")
            )
        }
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? SLinguistApplication
            ThemeViewModel(app?.preferencesRepository ?: throw IllegalStateException("App must be SLinguistApplication"))
        }
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as? SLinguistApplication
            TrainingViewModel(app?.cardRepository ?: throw IllegalStateException("App must be SLinguistApplication"))
        }
    }
}

class HomeViewModel(
    private val cardRepository: CardRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    val state: StateFlow<HomeUiState> = combine(
        preferencesRepository.profile,
        cardRepository.cards,
        cardRepository.statistics,
        preferencesRepository.appSettings
    ) { profile, cards, stats, settings ->
        HomeUiState(
            profile = profile,
            totalCards = cards.size,
            studiedCards = stats.studiedWords,
            stats = HomeStats(
                trainingsCount = stats.trainingCount,
                streakDays = stats.currentStreak,
                lastActivityAt = stats.lastActivityAt
            ),
            favoriteCards = cards.count { it.isFavorite },
            favorites = cards.filter { it.isFavorite },
            recentCards = cards.sortedByDescending { it.createdAt }.take(5),
            goalReached = stats.studiedWords >= settings.dailyGoal,
            dailyProgress = if (settings.dailyGoal > 0) stats.studiedWords.toFloat() / settings.dailyGoal else 1f,
            studiedToday = stats.studiedWords,
            settings = HomeSettings(dailyGoal = settings.dailyGoal)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun consumeError() {}
}

data class HomeUiState(
    val profile: UserProfile = UserProfile(),
    val totalCards: Int = 0,
    val studiedCards: Int = 0,
    val stats: HomeStats = HomeStats(),
    val favoriteCards: Int = 0,
    val favorites: List<VocabularyCard> = emptyList(),
    val recentCards: List<VocabularyCard> = emptyList(),
    val goalReached: Boolean = false,
    val dailyProgress: Float = 0f,
    val studiedToday: Int = 0,
    val settings: HomeSettings = HomeSettings(),
    val error: String? = null
)

data class HomeStats(val trainingsCount: Int = 0, val streakDays: Int = 0, val lastActivityAt: Long? = null)
data class HomeSettings(val dailyGoal: Int = 10)

class SettingsViewModel(
    private val cardRepository: CardRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    val state: StateFlow<SettingsUiState> = combine(
        preferencesRepository.appSettings,
        cardRepository.cards
    ) { settings, cards ->
        SettingsUiState(
            settings = settings,
            totalCards = cards.size,
            busy = false,
            message = null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    private val _message = MutableStateFlow<String?>(null)
    fun consumeMessage() { _message.value = null }

    fun setThemeMode(mode: ThemeMode) { viewModelScope.launch { preferencesRepository.setThemeMode(mode) } }
    fun setDefaultLanguage(language: LearningLanguage) { viewModelScope.launch { preferencesRepository.setDefaultLanguage(language) } }
    fun setDailyGoal(goal: Int) { viewModelScope.launch { preferencesRepository.setDailyGoal(goal) } }
    fun setShowPronunciation(show: Boolean) { viewModelScope.launch { preferencesRepository.setShowPronunciation(show) } }
    fun setAutoReveal(auto: Boolean) { viewModelScope.launch { preferencesRepository.setAutoReveal(auto) } }
    fun setShuffleTraining(shuffle: Boolean) { viewModelScope.launch { preferencesRepository.setShuffleTraining(shuffle) } }
    fun addSampleCards() { /* implementation in CardRepository would be needed */ }
    fun deleteAllCards() { viewModelScope.launch { cardRepository.clearAll() } }
    fun resetLearningProgress() { viewModelScope.launch { cardRepository.resetProgress() } }
}

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val totalCards: Int = 0,
    val busy: Boolean = false,
    val message: String? = null
)

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultLanguage: LearningLanguage = LearningLanguage.ENGLISH,
    val dailyGoal: Int = 10,
    val showPronunciation: Boolean = true,
    val autoReveal: Boolean = false,
    val shuffleTraining: Boolean = true
)

class ThemeViewModel(private val preferencesRepository: PreferencesRepository) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = preferencesRepository.themeMode.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.SYSTEM
    )
}

class TrainingViewModel(private val cardRepository: CardRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TrainingUiState())
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()
    
    val allCards: StateFlow<List<VocabularyCard>> = cardRepository.cards.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    fun updateConfig(config: TrainingConfig) {
        _uiState.value = _uiState.value.copy(config = config, setupMessage = null)
    }

    fun start() {
        val config = _uiState.value.config
        val filtered = allCards.value.filter { card ->
            (config.language == null || card.language == config.language) &&
            (config.difficulty == null || card.difficulty == config.difficulty) &&
            (config.wordType == null || card.wordType == config.wordType)
        }
        val cards = if (filtered.isEmpty()) emptyList() else filtered.shuffled().let { if (config.count > 0) it.take(config.count) else it }
        
        if (cards.isEmpty()) {
            _uiState.value = _uiState.value.copy(setupMessage = "Нет подходящих карточек")
        } else {
            _uiState.value = TrainingUiState(
                phase = TrainingPhase.ACTIVE,
                config = config,
                cards = cards
            )
        }
    }

    fun reveal() {
        _uiState.value = _uiState.value.copy(isTranslationVisible = true)
    }

    fun rate(rating: TrainingRating) {
        val state = _uiState.value
        val card = state.currentCard ?: return
        val isLast = state.currentIndex == state.cards.lastIndex
        
        val newKnown = state.knownCount + if (rating == TrainingRating.KNOW) 1 else 0
        val newHard = state.hardCount + if (rating == TrainingRating.HARD) 1 else 0
        val newUnknown = state.unknownCount + if (rating == TrainingRating.AGAIN) 1 else 0

        viewModelScope.launch {
            cardRepository.recordRating(card.id)
            if (isLast) {
                cardRepository.completeTraining(
                    reviewedCount = state.cards.size,
                    knownCount = newKnown,
                    hardCount = newHard,
                    unknownCount = newUnknown
                )
                _uiState.value = state.copy(
                    phase = TrainingPhase.COMPLETED,
                    knownCount = newKnown,
                    hardCount = newHard,
                    unknownCount = newUnknown
                )
            } else {
                _uiState.value = state.copy(
                    currentIndex = state.currentIndex + 1,
                    isTranslationVisible = false,
                    knownCount = newKnown,
                    hardCount = newHard,
                    unknownCount = newUnknown
                )
            }
        }
    }

    fun reset() {
        _uiState.value = TrainingUiState(config = _uiState.value.config)
    }
}
