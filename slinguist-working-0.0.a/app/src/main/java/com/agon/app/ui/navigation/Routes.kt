package com.agon.app.ui.navigation

object Routes {
    const val HOME = "home"
    const val CARDS = "cards"
    const val TRAINING = "training"
    const val STATS = "stats"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    const val CARD_ID_ARG = "cardId"
    const val CARD_EDITOR_PATTERN = "card_editor/{$CARD_ID_ARG}"

    fun cardEditor(id: Long = 0L) = "card_editor/$id"

    val fullScreenRoutes = listOf(
        SETTINGS,
        CARD_EDITOR_PATTERN
    )
}
