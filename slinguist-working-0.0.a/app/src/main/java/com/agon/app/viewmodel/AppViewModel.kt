package com.agon.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.agon.app.data.local.SLinguistDatabase
import com.agon.app.data.model.AppStatistics
import com.agon.app.data.model.CardFilter
import com.agon.app.data.model.CardsUiState
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

class AppViewModel(
    private val cardRepository: CardRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val filters = MutableStateFlow(CardFilter())
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val allCards: StateFlow<List<VocabularyCard>> = cardRepository.cards.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList(),
    )

    val cardsUiState: StateFlow<CardsUiState> = combine(cardRepository.cards, filters) { cards, filter ->
        val filtered = cards.filter { card ->
            (filter.phraseQuery.isBlank() || card.phrase.contains(filter.phraseQuery.trim(), ignoreCase = true)) &&
                (filter.translationQuery.isBlank() || card.translation.contains(filter.translationQuery.trim(), ignoreCase = true)) &&
                (filter.language == null || card.language == filter.language) &&
                (filter.difficulty == null || card.difficulty == filter.difficulty) &&
                (filter.wordType == null || card.wordType == filter.wordType) &&
                (!filter.favoritesOnly || card.isFavorite)
        }
        CardsUiState(cards = filtered, filter = filter, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CardsUiState())

    val statistics: StateFlow<AppStatistics> = cardRepository.statistics.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AppStatistics(),
    )

    val profile: StateFlow<UserProfile> = preferencesRepository.profile.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UserProfile(),
    )

    private val _training = MutableStateFlow(TrainingUiState())
    val training: StateFlow<TrainingUiState> = _training.asStateFlow()

    fun updateFilter(filter: CardFilter) {
        filters.value = filter
    }

    fun resetFilters() {
        filters.value = CardFilter()
    }

    fun saveCard(card: VocabularyCard) = safeLaunch("Не удалось сохранить карточку") {
        require(card.phrase.isNotBlank() && card.translation.isNotBlank()) {
            "Заполните слово и перевод"
        }
        cardRepository.save(card)
    }

    fun deleteCard(id: Long) = safeLaunch("Не удалось удалить карточку") {
        cardRepository.delete(id)
    }

    fun toggleFavorite(id: Long) = safeLaunch("Не удалось изменить избранное") {
        cardRepository.toggleFavorite(id)
    }

    fun updateProfile(name: String, email: String) = safeLaunch("Не удалось сохранить профиль") {
        preferencesRepository.updateProfile(name, email)
    }

    fun setDarkTheme(enabled: Boolean) = safeLaunch("Не удалось изменить тему") {
        preferencesRepository.setThemeMode(if (enabled) ThemeMode.DARK else ThemeMode.LIGHT)
    }

    fun saveAvatar(uri: Uri) = safeLaunch("Не удалось сохранить аватар") {
        preferencesRepository.saveAvatar(uri)
    }

    fun removeAvatar() = safeLaunch("Не удалось удалить аватар") {
        preferencesRepository.removeAvatar()
    }

    fun updateTrainingConfig(config: TrainingConfig) {
        _training.value = _training.value.copy(config = config, setupMessage = null)
    }

    fun startTraining() {
        val config = _training.value.config
        val matching = allCards.value.filter { card ->
            (config.language == null || card.language == config.language) &&
                (config.difficulty == null || card.difficulty == config.difficulty) &&
                (config.wordType == null || card.wordType == config.wordType)
        }.shuffled().let { cards ->
            if (config.count <= 0) cards else cards.take(config.count)
        }
        _training.value = if (matching.isEmpty()) {
            _training.value.copy(setupMessage = "Нет карточек с выбранными параметрами")
        } else {
            TrainingUiState(
                phase = TrainingPhase.ACTIVE,
                config = config,
                cards = matching,
            )
        }
    }

    fun revealTranslation() {
        _training.value = _training.value.copy(isTranslationVisible = true)
    }

    fun rateCurrent(rating: TrainingRating) {
        val state = _training.value
        if (state.phase != TrainingPhase.ACTIVE) return
        val card = state.currentCard ?: return
        val known = state.knownCount + if (rating == TrainingRating.KNOW) 1 else 0
        val hard = state.hardCount + if (rating == TrainingRating.HARD) 1 else 0
        val unknown = state.unknownCount + if (rating == TrainingRating.AGAIN) 1 else 0
        val isLast = state.currentIndex == state.cards.lastIndex

        if (isLast) {
            _training.value = state.copy(
                phase = TrainingPhase.COMPLETED,
                isTranslationVisible = false,
                knownCount = known,
                hardCount = hard,
                unknownCount = unknown,
            )
            safeLaunch("Не удалось сохранить тренировку") {
                cardRepository.recordRating(card.id)
                cardRepository.completeTraining(state.cards.size, known, hard, unknown)
            }
        } else {
            _training.value = state.copy(
                currentIndex = state.currentIndex + 1,
                isTranslationVisible = false,
                knownCount = known,
                hardCount = hard,
                unknownCount = unknown,
            )
            safeLaunch("Не удалось сохранить результат") {
                cardRepository.recordRating(card.id)
            }
        }
    }

    fun resetTraining() {
        _training.value = TrainingUiState(config = _training.value.config)
    }

    fun dismissError() {
        _error.value = null
    }

    private fun safeLaunch(fallbackMessage: String, block: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { block() }.onFailure { error ->
                _error.value = error.message?.takeIf { it.isNotBlank() } ?: fallbackMessage
            }
        }
    }
}

class AppViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = SLinguistDatabase.getInstance(appContext)
        return AppViewModel(
            CardRepository(database),
            PreferencesRepository(appContext),
        ) as T
    }
}
