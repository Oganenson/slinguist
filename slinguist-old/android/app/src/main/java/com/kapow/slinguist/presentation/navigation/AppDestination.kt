package com.kapow.slinguist.presentation.navigation

sealed class AppDestination(val route: String) {
    data object Home : AppDestination("home")
    data object Review : AppDestination("review")
    data object Words : AppDestination("words")
    data object Profile : AppDestination("profile")
    data object CreateCard : AppDestination("create")
    data object Session : AppDestination("session")
    data object Settings : AppDestination("settings")
}

val bottomBarDestinations = listOf(
    AppDestination.Home,
    AppDestination.Review,
    AppDestination.Words,
    AppDestination.Profile,
)
