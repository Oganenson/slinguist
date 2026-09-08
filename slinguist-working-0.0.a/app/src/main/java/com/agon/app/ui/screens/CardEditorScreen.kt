package com.agon.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.agon.app.data.model.Difficulty
import com.agon.app.data.model.LearningLanguage
import com.agon.app.data.model.VocabularyCard
import com.agon.app.data.model.WordType
import com.agon.app.ui.components.SelectionField
import com.agon.app.ui.theme.GoldFavorite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditorScreen(
    cardId: Long,
    cards: List<VocabularyCard>,
    onBack: () -> Unit,
    onSave: (VocabularyCard) -> Unit,
) {
    val existing = cards.firstOrNull { it.id == cardId }
    var initializedId by remember { mutableStateOf<Long?>(null) }
    var phrase by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var pronunciation by remember { mutableStateOf("") }
    var language by remember { mutableStateOf(LearningLanguage.ENGLISH) }
    var difficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
    var wordType by remember { mutableStateOf(WordType.OTHER) }
    var favorite by remember { mutableStateOf(false) }
    var studiedAt by remember { mutableStateOf<Long?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(cardId, existing) {
        if (cardId <= 0 && initializedId == null) {
            initializedId = cardId
        } else if (existing != null && initializedId != existing.id) {
            phrase = existing.phrase
            translation = existing.translation
            pronunciation = existing.pronunciation
            language = existing.language
            difficulty = existing.difficulty
            wordType = existing.wordType
            favorite = existing.isFavorite
            studiedAt = existing.lastStudiedAt
            initializedId = existing.id
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (cardId > 0) "Редактирование" else "Новая карточка") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Назад") } },
                actions = {
                    IconButton(onClick = { favorite = !favorite }) {
                        Icon(
                            if (favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Избранное",
                            tint = if (favorite) GoldFavorite else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 10.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    "Основное",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            item {
                OutlinedTextField(
                    value = phrase,
                    onValueChange = { phrase = it; validationError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Слово или фраза *") },
                    supportingText = { Text("На изучаемом языке") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
                    isError = validationError != null && phrase.isBlank(),
                )
            }
            item {
                OutlinedTextField(
                    value = translation,
                    onValueChange = { translation = it; validationError = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Перевод *") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
                    isError = validationError != null && translation.isBlank(),
                )
            }
            item {
                OutlinedTextField(
                    value = pronunciation,
                    onValueChange = { pronunciation = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Произношение") },
                    placeholder = { Text("Например: [həˈləʊ]") },
                    singleLine = true,
                )
            }
            item {
                Text("Классификация", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 6.dp))
            }
            item {
                SelectionField(
                    label = "Язык",
                    selected = language,
                    options = LearningLanguage.entries,
                    title = { it.title },
                    onSelect = { language = it },
                )
            }
            item {
                SelectionField(
                    label = "Сложность",
                    selected = difficulty,
                    options = Difficulty.entries,
                    title = { it.title },
                    onSelect = { difficulty = it },
                )
            }
            item {
                SelectionField(
                    label = "Тип слова",
                    selected = wordType,
                    options = WordType.entries,
                    title = { it.title },
                    onSelect = { wordType = it },
                )
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Изучено", style = MaterialTheme.typography.titleMedium)
                        Text(
                            studiedAt?.let { "Последнее изучение: ${formatDate(it)}" } ?: "Ещё не было в тренировке",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = studiedAt != null,
                        onCheckedChange = { checked -> studiedAt = if (checked) System.currentTimeMillis() else null },
                    )
                }
            }
            validationError?.let { message ->
                item { Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }
            }
            item {
                Button(
                    onClick = {
                        if (phrase.isBlank() || translation.isBlank()) {
                            validationError = "Заполните обязательные поля"
                        } else {
                            onSave(
                                VocabularyCard(
                                    id = existing?.id ?: 0,
                                    phrase = phrase,
                                    translation = translation,
                                    pronunciation = pronunciation,
                                    language = language,
                                    difficulty = difficulty,
                                    wordType = wordType,
                                    isFavorite = favorite,
                                    lastStudiedAt = studiedAt,
                                    createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                                ),
                            )
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 14.dp),
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Text("Сохранить карточку", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("d MMMM yyyy", Locale("ru")).format(Date(timestamp))
