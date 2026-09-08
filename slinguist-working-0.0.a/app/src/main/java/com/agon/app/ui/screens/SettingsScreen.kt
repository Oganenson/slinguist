package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agon.app.data.model.LearningLanguage as Language
import com.agon.app.data.model.ThemeMode
import com.agon.app.ui.components.OptionPickerSheet
import com.agon.app.ui.components.SectionTitle
import com.agon.app.ui.theme.SkyBlue
import com.agon.app.ui.theme.VioletPrimary
import com.agon.app.util.DateUtils
import com.agon.app.viewmodel.AppViewModelProvider
import com.agon.app.viewmodel.SettingsViewModel

private enum class SettingsDialog { NONE, DELETE_ALL, RESET_PROGRESS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var languageSheet by remember { mutableStateOf(false) }
    var dialog by remember { mutableStateOf(SettingsDialog.NONE) }

    LaunchedEffect(state.message) {
        val message = state.message
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { SectionTitle(text = "Оформление") }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        ThemeMode.all.forEach { mode ->
                            ThemeOptionRow(
                                mode = mode,
                                selected = state.settings.themeMode == mode,
                                onClick = { viewModel.setThemeMode(mode) },
                            )
                        }
                    }
                }
            }

            item { SectionTitle(text = "Изучение") }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(0.dp))
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Язык по умолчанию",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = "Подставляется при создании карточки",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Surface(
                                onClick = { languageSheet = true },
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = state.settings.defaultLanguage.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Icon(
                                        Icons.Default.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "Цель на день",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = "${state.settings.dailyGoal} ${DateUtils.plural(state.settings.dailyGoal, "слово", "слова", "слов")}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            Slider(
                                value = state.settings.dailyGoal.toFloat(),
                                onValueChange = { viewModel.setDailyGoal(it.toInt()) },
                                valueRange = 5f..100f,
                                steps = 18,
                            )
                        }
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        SwitchRow(
                            title = "Показывать произношение",
                            subtitle = "В списках и на карточке тренировки",
                            checked = state.settings.showPronunciation,
                            onCheckedChange = viewModel::setShowPronunciation,
                        )
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        SwitchRow(
                            title = "Сразу показывать перевод",
                            subtitle = "Карточка открывается без нажатия",
                            checked = state.settings.autoReveal,
                            onCheckedChange = viewModel::setAutoReveal,
                        )
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        SwitchRow(
                            title = "Перемешивать карточки",
                            subtitle = "Случайный порядок в тренировке",
                            checked = state.settings.shuffleTraining,
                            onCheckedChange = viewModel::setShuffleTraining,
                        )
                    }
                }
            }

            item { SectionTitle(text = "Данные") }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Column {
                        ActionRow(
                            icon = Icons.Default.LibraryAdd,
                            tint = VioletPrimary,
                            title = "Добавить примеры карточек",
                            subtitle = "Набор слов на разных языках",
                            enabled = !state.busy,
                            onClick = viewModel::addSampleCards,
                        )
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        ActionRow(
                            icon = Icons.Default.RestartAlt,
                            tint = SkyBlue,
                            title = "Сбросить прогресс изучения",
                            subtitle = "Очистит даты изучения и статистику",
                            enabled = !state.busy,
                            onClick = { dialog = SettingsDialog.RESET_PROGRESS },
                        )
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        ActionRow(
                            icon = Icons.Default.DeleteForever,
                            tint = MaterialTheme.colorScheme.error,
                            title = "Удалить все карточки",
                            subtitle = "В библиотеке: ${state.totalCards}",
                            enabled = !state.busy && state.totalCards > 0,
                            onClick = { dialog = SettingsDialog.DELETE_ALL },
                        )
                    }
                }
            }

            item { SectionTitle(text = "О приложении") }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "SLinguist 1.0",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Офлайн-тренажёр иностранных слов. 30 языков, 14 типов слов, 3 уровня сложности. Без интернета, без аккаунтов и синхронизации: все данные хранятся в локальной базе устройства.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Bolt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Room · DataStore · Jetpack Compose · Material 3",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }

    if (languageSheet) {
        OptionPickerSheet(
            title = "Язык по умолчанию",
            options = Language.entries,
            selected = state.settings.defaultLanguage,
            labelOf = { it.title },
            leadingOf = { it.title.take(1) },
            subtitleOf = { it.name },
            allowAll = false,
            onSelect = { language -> language?.let { viewModel.setDefaultLanguage(it) } },
            onDismiss = { languageSheet = false },
        )
    }

    when (dialog) {
        SettingsDialog.DELETE_ALL -> ConfirmDialog(
            title = "Удалить все карточки?",
            text = "Будут удалены все ${state.totalCards} карточек и история тренировок. Действие необратимо.",
            confirmText = "Удалить",
            onConfirm = {
                dialog = SettingsDialog.NONE
                viewModel.deleteAllCards()
            },
            onDismiss = { dialog = SettingsDialog.NONE },
        )

        SettingsDialog.RESET_PROGRESS -> ConfirmDialog(
            title = "Сбросить прогресс?",
            text = "Даты изучения, счётчики повторений и вся статистика будут обнулены. Карточки останутся.",
            confirmText = "Сбросить",
            onConfirm = {
                dialog = SettingsDialog.NONE
                viewModel.resetLearningProgress()
            },
            onDismiss = { dialog = SettingsDialog.NONE },
        )

        SettingsDialog.NONE -> Unit
    }
}

@Composable
private fun ThemeOptionRow(
    mode: ThemeMode,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val icon = when (mode) {
        ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
        ThemeMode.LIGHT -> Icons.Default.LightMode
        ThemeMode.DARK -> Icons.Default.DarkMode
    }
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        else MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = mode.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f),
            )
            if (selected) {
                Text(
                    text = "Выбрано",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(12.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    tint: Color,
    title: String,
    subtitle: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(tint.copy(alpha = if (enabled) 0.15f else 0.07f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (enabled) tint else tint.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    text: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        },
        shape = MaterialTheme.shapes.large,
    )
}
