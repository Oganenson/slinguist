package com.kapow.slinguist.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kapow.slinguist.R
import com.kapow.slinguist.presentation.create.CreateCardScreen
import com.kapow.slinguist.presentation.home.HomeScreen
import com.kapow.slinguist.presentation.profile.ProfileScreen
import com.kapow.slinguist.presentation.review.ReviewScreen
import com.kapow.slinguist.presentation.session.SessionScreen
import com.kapow.slinguist.presentation.settings.SettingsScreen
import com.kapow.slinguist.presentation.words.WordsScreen

@Composable
fun SlinguistApp() {
    val navController = rememberNavController()
    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarDestinations.map(AppDestination::route)) {
                SlinguistNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(AppDestination.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(AppDestination.Home.route) { HomeScreen() }
            composable(AppDestination.Review.route) { ReviewScreen() }
            composable(AppDestination.Words.route) { WordsScreen() }
            composable(AppDestination.Profile.route) { ProfileScreen() }
            composable(AppDestination.CreateCard.route) { CreateCardScreen() }
            composable(AppDestination.Session.route) { SessionScreen() }
            composable(AppDestination.Settings.route) { SettingsScreen() }
        }
    }
}

@Composable
private fun SlinguistNavigationBar(
    currentRoute: String?,
    onNavigate: (AppDestination) -> Unit,
) {
    val items = listOf(
        NavigationItem(AppDestination.Home, R.string.home_label, Icons.Outlined.Home),
        NavigationItem(AppDestination.Review, R.string.review_label, Icons.Outlined.School),
        NavigationItem(AppDestination.Words, R.string.words_label, Icons.Outlined.AutoStories),
        NavigationItem(AppDestination.Profile, R.string.profile_label, Icons.Outlined.Person),
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.destination.route,
                onClick = { onNavigate(item.destination) },
                icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                label = { Text(stringResource(item.labelRes)) },
            )
        }
    }
}

private data class NavigationItem(
    val destination: AppDestination,
    val labelRes: Int,
    val icon: ImageVector,
)
