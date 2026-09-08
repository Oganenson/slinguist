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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agon.app.data.model.VocabularyCard as CardModel
import com.agon.app.ui.components.AvatarView
import com.agon.app.ui.components.DifficultyBadge
import com.agon.app.ui.components.EmptyState
import com.agon.app.ui.components.MetaChip
import com.agon.app.ui.components.SectionTitle
import com.agon.app.ui.components.StatTile
import com.agon.app.ui.theme.GoldFavorite
import com.agon.app.ui.theme.SkyBlue
import com.agon.app.ui.theme.VioletMid
import com.agon.app.ui.theme.VioletPrimary
import com.agon.app.util.DateUtils
import com.agon.app.viewmodel.AppViewModelProvider
import com.agon.app.viewmodel.HomeViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    onOpenCards: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSettings: () -> Unit,
    onAddCard: () -> Unit,
    onOpenCard: (Long) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.error) {
        val error = state.error
        if (error != null) {
            snackbarHostState.showSnackbar(error)
            viewModel.consumeError()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(50))) {
                        Surface(
                            color = MaterialTheme.colorScheme.background,
                            onClick = onOpenProfile,
                        ) {
                            AvatarView(
                                initials = if (state.profile.name.isNotBlank()) state.profile.name.take(1).uppercase() else "",
                                avatarPath = state.profile.avatarPath,
                                size = 46,
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = greeting(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = state.profile.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Настройки",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item { HeroCard(state = state, onStart = onOpenTraining) }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile(
                        value = "${state.totalCards}",
                        label = "Всего карточек",
                        icon = Icons.Default.Style,
                        tint = VioletPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        value = "${state.studiedCards}",
                        label = "Изучено слов",
                        icon = Icons.Default.TaskAlt,
                        tint = SkyBlue,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile(
                        value = "${state.stats.trainingsCount}",
                        label = "Тренировок",
                        icon = Icons.Default.School,
                        tint = VioletMid,
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        value = "${state.favoriteCards}",
                        label = "В избранном",
                        icon = Icons.Default.Star,
                        tint = GoldFavorite,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onAddCard,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Новая")
                    }
                    OutlinedButton(
                        onClick = onOpenStats,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text("Статистика")
                    }
                }
            }

            if (state.favorites.isNotEmpty()) {
                item {
                    SectionTitle(text = "Избранное")
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.favorites, key = { it.id }) { card ->
                            FavoriteCardChip(card = card, onClick = { onOpenCard(card.id) })
                        }
                    }
                }
            }

            item {
                SectionTitle(
                    text = "Недавние карточки",
                    action = {
                        TextButton(onClick = onOpenCards) {
                            Text("Все")
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    },
                )
            }

            if (state.recentCards.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        EmptyState(
                            icon = { Icon(Icons.Default.Style, contentDescription = null) },
                            title = "Карточек пока нет",
                            text = "Создайте первую карточку — слово, перевод и произношение.",
                        )
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
                            Button(onClick = onAddCard, shape = MaterialTheme.shapes.small) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("Добавить карточку")
                            }
                        }
                    }
                }
            } else {
                items(state.recentCards, key = { it.id }) { card ->
                    CompactCardRow(card = card, onClick = { onOpenCard(card.id) })
                }
            }

            item {
                Text(
                    text = "Последняя активность: ${DateUtils.relative(state.stats.lastActivityAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun HeroCard(
    state: com.agon.app.viewmodel.HomeUiState,
    onStart: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Brush.linearGradient(listOf(VioletPrimary, VioletMid, SkyBlue)))
            .padding(20.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(21.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Серия: ${state.stats.streakDays} ${DateUtils.plural(state.stats.streakDays, "день", "дня", "дней")}",
                        style = MaterialTheme.typography.titleMedium,
                        color = androidx.compose.ui.graphics.Color.White,
                    )
                    Text(
                        text = "Цель на день: ${state.studiedToday} из ${state.settings.dailyGoal}",
                        style = MaterialTheme.typography.bodySmall,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
                    )
                }
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { state.dailyProgress },
                        modifier = Modifier.size(52.dp),
                        color = androidx.compose.ui.graphics.Color.White,
                        trackColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
                        strokeWidth = 5.dp,
                    )
                    Text(
                        text = "${(state.dailyProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = androidx.compose.ui.graphics.Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { state.dailyProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = androidx.compose.ui.graphics.Color.White,
                trackColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.28f),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (state.goalReached) "Цель на сегодня выполнена! Продолжим?"
                else "Готовы потренироваться?",
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f),
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color.White,
                    contentColor = VioletPrimary,
                ),
                shape = MaterialTheme.shapes.small,
            ) {
                Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Начать тренировку", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun FavoriteCardChip(card: CardModel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(168.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = card.language.title.take(1), style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(6.dp))
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = GoldFavorite,
                    modifier = Modifier.size(14.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = card.phrase,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = card.translation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun CompactCardRow(card: CardModel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = card.language.title.take(1), style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.phrase,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = card.translation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (card.pronunciation.isNotBlank()) {
                    Text(
                        text = card.pronunciation,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                DifficultyBadge(card.difficulty)
                Spacer(Modifier.height(4.dp))
                MetaChip(text = card.wordType.title)
            }
        }
    }
}

private fun greeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Доброе утро"
        in 12..17 -> "Добрый день"
        in 18..22 -> "Добрый вечер"
        else -> "Доброй ночи"
    }
}
