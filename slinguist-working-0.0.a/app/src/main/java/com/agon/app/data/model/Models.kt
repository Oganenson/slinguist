package com.agon.app.data.model

enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    val title: String
        get() = when (this) {
            SYSTEM -> "Системная"
            LIGHT -> "Светлая"
            DARK -> "Темная"
        }

    companion object {
        val all = entries
    }
}

enum class LearningLanguage(val title: String) {
    ENGLISH("Английский"),
    GERMAN("Немецкий"),
    FRENCH("Французский"),
    SPANISH("Испанский"),
    ITALIAN("Итальянский"),
    PORTUGUESE("Португальский"),
    DUTCH("Нидерландский"),
    POLISH("Польский"),
    CZECH("Чешский"),
    SLOVAK("Словацкий"),
    UKRAINIAN("Украинский"),
    KAZAKH("Казахский"),
    RUSSIAN("Русский"),
    CHINESE("Китайский"),
    JAPANESE("Японский"),
    KOREAN("Корейский"),
    ARABIC("Арабский"),
    TURKISH("Турецкий"),
    GREEK("Греческий"),
    SWEDISH("Шведский"),
    NORWEGIAN("Норвежский"),
    DANISH("Датский"),
    FINNISH("Финский"),
    HUNGARIAN("Венгерский"),
    ROMANIAN("Румынский"),
    BULGARIAN("Болгарский"),
    SERBIAN("Сербский"),
    HINDI("Хинди"),
    VIETNAMESE("Вьетнамский"),
    LATIN("Латинский"),
}

enum class Difficulty(val title: String) {
    EASY("Легко"),
    MEDIUM("Средне"),
    HARD("Сложно"),
}

enum class WordType(val title: String) {
    NOUN("Существительное"),
    VERB("Глагол"),
    ADJECTIVE("Прилагательное"),
    ADVERB("Наречие"),
    PRONOUN("Местоимение"),
    PREPOSITION("Предлог"),
    CONJUNCTION("Союз"),
    PARTICLE("Частица"),
    NUMERAL("Числительное"),
    INTERJECTION("Междометие"),
    PHRASEOLOGISM("Фразеологизм"),
    IDIOM("Идиома"),
    EXPRESSION("Выражение"),
    OTHER("Другое"),
}

data class VocabularyCard(
    val id: Long = 0,
    val phrase: String,
    val translation: String,
    val pronunciation: String = "",
    val language: LearningLanguage = LearningLanguage.ENGLISH,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val wordType: WordType = WordType.OTHER,
    val isFavorite: Boolean = false,
    val lastStudiedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

data class CardFilter(
    val phraseQuery: String = "",
    val translationQuery: String = "",
    val language: LearningLanguage? = null,
    val difficulty: Difficulty? = null,
    val wordType: WordType? = null,
    val favoritesOnly: Boolean = false,
) {
    val activeCount: Int
        get() = listOf(
            phraseQuery.isNotBlank(), translationQuery.isNotBlank(), language != null,
            difficulty != null, wordType != null, favoritesOnly,
        ).count { it }
}

data class UserProfile(
    val name: String = "Лингвист",
    val email: String = "",
    val avatarPath: String = "",
    val darkTheme: Boolean = false,
)

data class AppStatistics(
    val totalCards: Int = 0,
    val studiedWords: Int = 0,
    val trainingCount: Int = 0,
    val currentStreak: Int = 0,
    val lastActivityAt: Long? = null,
)

data class CardsUiState(
    val cards: List<VocabularyCard> = emptyList(),
    val filter: CardFilter = CardFilter(),
    val isLoading: Boolean = true,
)

data class TrainingConfig(
    val language: LearningLanguage? = null,
    val difficulty: Difficulty? = null,
    val wordType: WordType? = null,
    val count: Int = 10,
)

enum class TrainingPhase { SETUP, ACTIVE, COMPLETED }
enum class TrainingRating { AGAIN, HARD, KNOW }

data class TrainingUiState(
    val phase: TrainingPhase = TrainingPhase.SETUP,
    val config: TrainingConfig = TrainingConfig(),
    val cards: List<VocabularyCard> = emptyList(),
    val currentIndex: Int = 0,
    val isTranslationVisible: Boolean = false,
    val knownCount: Int = 0,
    val hardCount: Int = 0,
    val unknownCount: Int = 0,
    val setupMessage: String? = null,
) {
    val currentCard: VocabularyCard? get() = cards.getOrNull(currentIndex)
    val progress: Float get() = if (cards.isEmpty()) 0f else currentIndex.toFloat() / cards.size.toFloat()
}
