package com.kapow.slinguist.domain.model

data class UserProfile(
    val name: String = DEFAULT_PROFILE_NAME,
    val email: String = DEFAULT_PROFILE_EMAIL,
    /** App-private file path or a persisted content URI once the avatar picker is implemented. */
    val avatarUri: String = DEFAULT_PROFILE_AVATAR_URI,
)

data class ReviewSessionConfig(
    val length: Int = 15,
    /** Kept as web-compatible values: All, Easy, Medium, Hard. */
    val difficulty: String = "All",
    val categories: List<String> = listOf("All"),
)

data class StoredUserStats(
    val streak: Int = 1,
    val globalRank: String = "Gold",
    val lastActiveEpochDay: Long? = null,
)

enum class AppLanguage { EN, RU }

enum class AppTheme { LIGHT, DARK }

const val DEFAULT_PROFILE_NAME = "Elena Rodriguez"
const val DEFAULT_PROFILE_EMAIL = "elena.rod@linguistflow.edu"

// Preserves the current web default. The Android UI will not fetch this URL as part of Phase 2.
const val DEFAULT_PROFILE_AVATAR_URI =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuCS1ZqAi5ht-Z44VGELJj7G1eOARCPLiK8L0VWPbsUYVwg-U7jmyThnco1P8whw0GYfiyxsEQd6Zz5lrqaxtQJQ7DawksmG4Zx3f2CGM1pvMp985yykYXsKoNW2lutcipk9CbhH743f3sSNDiPz8kyjG5G0wqdLN0IyboJZZrrbaiXdMsVAkPBg2-GzJVxhbKOmLmpAhJHB_AbFJUnxF7l1LEzRj4k2piznniIfzAFn6gQ75HyjY7nUchsJAn90lHvcEz9JQcLMYNk"
