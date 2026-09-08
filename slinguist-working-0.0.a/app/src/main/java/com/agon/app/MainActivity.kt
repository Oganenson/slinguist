package com.agon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.agon.app.data.model.ThemeMode
import com.agon.app.ui.navigation.Routes
import com.agon.app.ui.screens.CardEditorScreen
import com.agon.app.ui.screens.CardsScreen
import com.agon.app.ui.screens.HomeScreen
import com.agon.app.ui.screens.ProfileScreen
import com.agon.app.ui.screens.SettingsScreen
import com.agon.app.ui.screens.StatisticsScreen
import com.agon.app.ui.screens.TrainingScreen
import com.agon.app.ui.theme.SLinguistTheme
import com.agon.app.viewmodel.AppViewModel
import com.agon.app.viewmodel.AppViewModelProvider
import com.agon.app.viewmodel.ThemeViewModel
import com.agon.app.viewmodel.TrainingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemDark
            }
            SLinguistTheme(darkTheme = darkTheme) {
                SLinguistApp()
            }
        }
    }
}

private data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val navItems = listOf(
    NavItem(Routes.HOME, "Главная", Icons.Default.Home),
    NavItem(Routes.CARDS, "Карточки", Icons.Default.Style),
    NavItem(Routes.TRAINING, "Тренировка", Icons.Default.School),
    NavItem(Routes.STATS, "Статистика", Icons.Default.BarChart),
    NavItem(Routes.PROFILE, "Профиль", Icons.Default.Person),
)

@Composable
fun SLinguistApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val appViewModel: AppViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val trainingViewModel: TrainingViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val statistics by appViewModel.statistics.collectAsStateWithLifecycle()
    val allCards by appViewModel.allCards.collectAsStateWithLifecycle()
    val cardsUiState by appViewModel.cardsUiState.collectAsStateWithLifecycle()
    val profile by appViewModel.profile.collectAsStateWithLifecycle()
    val trainingState by trainingViewModel.uiState.collectAsStateWithLifecycle()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute != null && currentRoute !in Routes.fullScreenRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = if (showBottomBar) Modifier else Modifier.navigationBarsPadding(),
            )
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNav(navController = navController, currentRoute = currentRoute)
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    snackbarHostState = snackbarHostState,
                    onOpenCards = { navController.navigateTab(Routes.CARDS) },
                    onOpenTraining = { navController.navigateTab(Routes.TRAINING) },
                    onOpenStats = { navController.navigateTab(Routes.STATS) },
                    onOpenProfile = { navController.navigateTab(Routes.PROFILE) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                    onAddCard = { navController.navigate(Routes.cardEditor()) },
                    onOpenCard = { id -> navController.navigate(Routes.cardEditor(id)) },
                )
            }
            composable(Routes.CARDS) {
                CardsScreen(
                    state = cardsUiState,
                    onFilterChange = { appViewModel.updateFilter(it) },
                    onResetFilters = { appViewModel.resetFilters() },
                    onAdd = { navController.navigate(Routes.cardEditor()) },
                    onEdit = { id -> navController.navigate(Routes.cardEditor(id)) },
                    onFavorite = { id -> appViewModel.toggleFavorite(id) },
                    onDelete = { id -> appViewModel.deleteCard(id) }
                )
            }
            composable(Routes.TRAINING) {
                TrainingScreen(
                    state = trainingState,
                    allCards = allCards,
                    onConfigChange = { trainingViewModel.updateConfig(it) },
                    onStart = { trainingViewModel.start() },
                    onReveal = { trainingViewModel.reveal() },
                    onRate = { trainingViewModel.rate(it) },
                    onReset = { trainingViewModel.reset() },
                    onOpenCards = { navController.navigateTab(Routes.CARDS) }
                )
            }
            composable(Routes.STATS) {
                StatisticsScreen(
                    statistics = statistics,
                    cards = allCards
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    profile = profile,
                    onSaveProfile = { name, email -> appViewModel.updateProfile(name, email) },
                    onAvatarSelected = { uri -> appViewModel.saveAvatar(uri) },
                    onRemoveAvatar = { appViewModel.removeAvatar() },
                    onThemeChange = { appViewModel.setDarkTheme(it) }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    snackbarHostState = snackbarHostState,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = Routes.CARD_EDITOR_PATTERN,
                arguments = listOf(
                    navArgument(Routes.CARD_ID_ARG) {
                        type = NavType.LongType
                        defaultValue = 0L
                    },
                ),
            ) { entry ->
                val cardId = entry.arguments?.getLong(Routes.CARD_ID_ARG) ?: 0L
                CardEditorScreen(
                    cardId = cardId,
                    cards = allCards,
                    onBack = { navController.popBackStack() },
                    onSave = { card -> appViewModel.saveCard(card) }
                )
            }
        }
    }
}

@Composable
private fun BottomNav(navController: NavHostController, currentRoute: String?) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        navItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigateTab(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

private fun NavHostController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(Routes.HOME) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
