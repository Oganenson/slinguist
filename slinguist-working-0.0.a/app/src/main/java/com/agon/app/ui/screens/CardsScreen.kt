package com.agon.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agon.app.data.model.CardFilter
import com.agon.app.data.model.CardsUiState
import com.agon.app.data.model.Difficulty
import com.agon.app.data.model.LearningLanguage
import com.agon.app.data.model.VocabularyCard
import com.agon.app.data.model.WordType
import com.agon.app.ui.components.EmptyState
import com.agon.app.ui.components.SelectionField
import com.agon.app.ui.components.VocabularyCardRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    state: CardsUiState,
    onFilterChange: (CardFilter) -> Unit,
    onResetFilters: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onFavorite: (Long) -> Unit,
    onDelete: (Long) -> Unit,
) {
    var filtersVisible by remember { mutableStateOf(false) }
    var deleteCandidate by remember { mutableStateOf<VocabularyCard?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Карточки")
                        Text(
                            "Найдено: ${state.cards.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { filtersVisible = true }) {
                        BadgedBox(
                            badge = {
                                if (state.filter.activeCount > 0) Badge { Text(state.filter.activeCount.toString()) }
                            },
                        ) {
                            Icon(Icons.Default.FilterAlt, contentDescription = "Фильтры")
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAdd,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Добавить") },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            SearchFields(state.filter, onFilterChange)
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                state.cards.isEmpty() -> EmptyState(
                    title = if (state.filter.activeCount > 0) "Ничего не найдено" else "Словарь пока пуст",
                    text = if (state.filter.activeCount > 0) "Измените запрос или сбросьте фильтры" else "Добавьте слова и фразы для изучения",
                    icon = { Icon(Icons.Default.Style, contentDescription = null) },
                    modifier = Modifier.fillMaxSize(),
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.cards, key = { it.id }) { card ->
                        VocabularyCardRow(
                            card = card,
                            onOpen = { onEdit(card.id) },
                            onFavorite = { onFavorite(card.id) },
                            onDelete = { deleteCandidate = card },
                        )
                    }
                }
            }
        }
    }

    if (filtersVisible) {
        FilterSheet(
            filter = state.filter,
            onChange = onFilterChange,
            onReset = onResetFilters,
            onDismiss = { filtersVisible = false },
        )
    }

    deleteCandidate?.let { card ->
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            icon = { Icon(Icons.Default.Close, contentDescription = null) },
            title = { Text("Удалить карточку?") },
            text = { Text("«${card.phrase}» будет удалена без возможности восстановления.") },
            confirmButton = {
                Button(onClick = { onDelete(card.id); deleteCandidate = null }) { Text("Удалить") }
            },
            dismissButton = { TextButton(onClick = { deleteCandidate = null }) { Text("Отмена") } },
        )
    }
}

@Composable
private fun SearchFields(filter: CardFilter, onChange: (CardFilter) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = filter.phraseQuery,
            onValueChange = { onChange(filter.copy(phraseQuery = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Поиск по слову") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (filter.phraseQuery.isNotEmpty()) IconButton(onClick = { onChange(filter.copy(phraseQuery = "")) }) {
                    Icon(Icons.Default.Close, contentDescription = "Очистить")
                }
            },
            singleLine = true,
        )
        OutlinedTextField(
            value = filter.translationQuery,
            onValueChange = { onChange(filter.copy(translationQuery = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Поиск по переводу") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (filter.translationQuery.isNotEmpty()) IconButton(onClick = { onChange(filter.copy(translationQuery = "")) }) {
                    Icon(Icons.Default.Close, contentDescription = "Очистить")
                }
            },
            singleLine = true,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSheet(filter: CardFilter, onChange: (CardFilter) -> Unit, onReset: () -> Unit, onDismiss: () -> Unit) {
    val allLanguages: List<LearningLanguage?> = listOf(null) + LearningLanguage.entries
    val allDifficulties: List<Difficulty?> = listOf(null) + Difficulty.entries
    val allTypes: List<WordType?> = listOf(null) + WordType.entries
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Фильтры", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
                    TextButton(onClick = onReset, enabled = filter.activeCount > 0) { Text("Сбросить") }
                }
            }
            item { HorizontalDivider() }
            item {
                SelectionField(
                    label = "Язык",
                    selected = filter.language,
                    options = allLanguages,
                    title = { it?.title ?: "Все языки" },
                    onSelect = { onChange(filter.copy(language = it)) },
                )
            }
            item {
                SelectionField(
                    label = "Сложность",
                    selected = filter.difficulty,
                    options = allDifficulties,
                    title = { it?.title ?: "Любая сложность" },
                    onSelect = { onChange(filter.copy(difficulty = it)) },
                )
            }
            item {
                SelectionField(
                    label = "Тип слова",
                    selected = filter.wordType,
                    options = allTypes,
                    title = { it?.title ?: "Все типы" },
                    onSelect = { onChange(filter.copy(wordType = it)) },
                )
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Только избранные", style = MaterialTheme.typography.titleMedium)
                        Text("Показать карточки с отметкой ♥", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = filter.favoritesOnly, onCheckedChange = { onChange(filter.copy(favoritesOnly = it)) })
                }
            }
            item {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Показать ${if (filter.activeCount > 0) "результаты" else "все карточки"}") }
            }
        }
    }
}
