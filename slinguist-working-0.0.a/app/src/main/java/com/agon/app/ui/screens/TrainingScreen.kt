package com.agon.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agon.app.data.model.Difficulty
import com.agon.app.data.model.LearningLanguage
import com.agon.app.data.model.TrainingConfig
import com.agon.app.data.model.TrainingPhase
import com.agon.app.data.model.TrainingRating
import com.agon.app.data.model.TrainingUiState
import com.agon.app.data.model.VocabularyCard
import com.agon.app.data.model.WordType
import com.agon.app.ui.components.DifficultyPill
import com.agon.app.ui.components.SelectionField
import com.agon.app.ui.components.TagPill
import com.agon.app.ui.theme.EasyGreen
import com.agon.app.ui.theme.MediumBlue

@Composable
fun TrainingScreen(
    state: TrainingUiState,
    allCards: List<VocabularyCard>,
    onConfigChange: (TrainingConfig) -> Unit,
    onStart: () -> Unit,
    onReveal: () -> Unit,
    onRate: (TrainingRating) -> Unit,
    onReset: () -> Unit,
    onOpenCards: () -> Unit,
) {
    when (state.phase) {
        TrainingPhase.SETUP -> TrainingSetup(state, allCards, onConfigChange, onStart, onOpenCards)
        TrainingPhase.ACTIVE -> ActiveTraining(state, onReveal, onRate, onReset)
        TrainingPhase.COMPLETED -> TrainingCompleted(state, onReset, onOpenCards)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrainingSetup(
    state: TrainingUiState,
    allCards: List<VocabularyCard>,
    onConfigChange: (TrainingConfig) -> Unit,
    onStart: () -> Unit,
    onOpenCards: () -> Unit,
) {
    val config = state.config
    val matchingCount = allCards.count { card ->
        (config.language == null || card.language == config.language) &&
            (config.difficulty == null || card.difficulty == config.difficulty) &&
            (config.wordType == null || card.wordType == config.wordType)
    }
    val languages: List<LearningLanguage?> = listOf(null) + LearningLanguage.entries
    val difficulties: List<Difficulty?> = listOf(null) + Difficulty.entries
    val types: List<WordType?> = listOf(null) + WordType.entries
    val counts = listOf(5, 10, 20, 0)

    Scaffold(topBar = { TopAppBar(title = { Text("Тренировка") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(12.dp).size(28.dp))
                    }
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text("Настройте занятие", style = MaterialTheme.typography.titleLarge)
                        Text("Подберём карточки из вашего словаря", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            SelectionField("Язык", config.language, languages, { it?.title ?: "Все языки" }, { onConfigChange(config.copy(language = it)) })
            SelectionField("Сложность", config.difficulty, difficulties, { it?.title ?: "Любая сложность" }, { onConfigChange(config.copy(difficulty = it)) })
            SelectionField("Тип слова", config.wordType, types, { it?.title ?: "Все типы" }, { onConfigChange(config.copy(wordType = it)) })
            SelectionField("Количество карточек", config.count, counts, { if (it == 0) "Все подходящие" else "$it карточек" }, { onConfigChange(config.copy(count = it)) })

            Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        "Доступно: $matchingCount",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 10.dp).weight(1f),
                    )
                }
            }
            state.setupMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(
                onClick = onStart,
                enabled = matchingCount > 0,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 14.dp),
            ) { Text("Начать тренировку") }
            if (allCards.isEmpty()) {
                OutlinedButton(onClick = onOpenCards, modifier = Modifier.fillMaxWidth()) { Text("Добавить карточки") }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActiveTraining(
    state: TrainingUiState,
    onReveal: () -> Unit,
    onRate: (TrainingRating) -> Unit,
    onReset: () -> Unit,
) {
    val card = state.currentCard ?: return
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${state.currentIndex + 1} из ${state.cards.size}") },
                navigationIcon = { IconButton(onClick = onReset) { Icon(Icons.Default.Close, contentDescription = "Завершить") } },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LinearProgressIndicator(
                progress = { (state.currentIndex + 1).toFloat() / state.cards.size.toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 22.dp).animateContentSize(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TagPill(card.language.title)
                        DifficultyPill(card.difficulty)
                    }
                    Spacer(Modifier.height(30.dp))
                    Text(card.phrase, style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    if (card.pronunciation.isNotBlank()) {
                        Text(card.pronunciation, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 10.dp))
                    }
                    AnimatedContent(targetState = state.isTranslationVisible, label = "перевод") { visible ->
                        if (visible) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 30.dp)) {
                                Text("Перевод", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(card.translation, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                                Text(card.wordType.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 12.dp))
                            }
                        } else {
                            Text("Сначала вспомните перевод", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 30.dp))
                        }
                    }
                }
            }
            if (!state.isTranslationVisible) {
                Button(onClick = onReveal, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 14.dp)) {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                    Text("Показать перевод", modifier = Modifier.padding(start = 8.dp))
                }
            } else {
                Text("Как вы справились?", style = MaterialTheme.typography.titleMedium)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onRate(TrainingRating.AGAIN) }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 12.dp, horizontal = 4.dp)) { Text("Не знаю") }
                    OutlinedButton(onClick = { onRate(TrainingRating.HARD) }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 12.dp, horizontal = 4.dp)) { Text("С трудом") }
                    Button(onClick = { onRate(TrainingRating.KNOW) }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 12.dp, horizontal = 4.dp)) { Text("Знаю") }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrainingCompleted(state: TrainingUiState, onReset: () -> Unit, onOpenCards: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Готово") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(shape = CircleShape, color = EasyGreen.copy(alpha = 0.15f), contentColor = EasyGreen) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.padding(22.dp).size(52.dp))
            }
            Text("Тренировка завершена", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 22.dp))
            Text("Повторено карточек: ${state.cards.size}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(top = 28.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ResultBox("Знаю", state.knownCount, EasyGreen, Modifier.weight(1f))
                ResultBox("С трудом", state.hardCount, MediumBlue, Modifier.weight(1f))
                ResultBox("Не знаю", state.unknownCount, MaterialTheme.colorScheme.error, Modifier.weight(1f))
            }
            Button(onClick = onReset, modifier = Modifier.fillMaxWidth().padding(top = 28.dp), contentPadding = PaddingValues(vertical = 14.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Text("Ещё тренировка", modifier = Modifier.padding(start = 8.dp))
            }
            OutlinedButton(onClick = onOpenCards, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Text("Открыть словарь") }
        }
    }
}

@Composable
private fun ResultBox(label: String, value: Int, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(16.dp), color = color.copy(alpha = 0.12f), contentColor = color) {
        Column(modifier = Modifier.padding(vertical = 14.dp, horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        }
    }
}
