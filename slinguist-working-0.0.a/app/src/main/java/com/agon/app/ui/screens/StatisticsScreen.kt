package com.agon.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agon.app.data.model.AppStatistics
import com.agon.app.data.model.Difficulty
import com.agon.app.data.model.VocabularyCard
import com.agon.app.ui.components.DifficultyPill
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(statistics: AppStatistics, cards: List<VocabularyCard>) {
    val learnedFraction = if (statistics.totalCards == 0) 0f else statistics.studiedWords.toFloat() / statistics.totalCards
    val topLanguages = cards.groupingBy { it.language }.eachCount().entries.sortedByDescending { it.value }.take(5)
    Column {
        TopAppBar(title = { Text("Статистика") })
        LazyColumn(
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Прогресс словаря", style = MaterialTheme.typography.titleLarge)
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.Bottom) {
                            Text("${statistics.studiedWords}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                            Text(" / ${statistics.totalCards} изучено", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 5.dp))
                        }
                        LinearProgressIndicator(progress = { learnedFraction }, modifier = Modifier.fillMaxWidth().padding(top = 14.dp))
                        Text("${(learnedFraction * 100).toInt()}% карточек были в тренировках", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile("Тренировки", statistics.trainingCount.toString(), Icons.Default.AutoStories, Modifier.weight(1f))
                    StatTile("Серия", "${statistics.currentStreak} дн.", Icons.Default.LocalFireDepartment, Modifier.weight(1f))
                }
            }
            item {
                StatTile(
                    "Последняя активность",
                    statistics.lastActivityAt?.let { formatStatsDate(it) } ?: "Пока нет",
                    Icons.Default.CalendarMonth,
                    Modifier.fillMaxWidth(),
                )
            }
            item { Text("По сложности", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 6.dp)) }
            item {
                Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp), shadowElevation = 1.dp) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Difficulty.entries.forEach { difficulty ->
                            val count = cards.count { it.difficulty == difficulty }
                            val fraction = if (cards.isEmpty()) 0f else count.toFloat() / cards.size
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                DifficultyPill(difficulty)
                                LinearProgressIndicator(progress = { fraction }, modifier = Modifier.weight(1f).padding(horizontal = 12.dp))
                                Text(count.toString(), style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
            item { Text("Языки", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 6.dp)) }
            if (topLanguages.isEmpty()) {
                item {
                    Text("Добавьте карточки, чтобы увидеть распределение по языкам", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                item {
                    Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp), shadowElevation = 1.dp) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            topLanguages.forEach { entry ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(entry.key.title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                                    Text(entry.value.toString(), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(title: String, value: String, icon: ImageVector, modifier: Modifier) {
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

private fun formatStatsDate(timestamp: Long): String =
    SimpleDateFormat("d MMM yyyy, HH:mm", Locale("ru")).format(Date(timestamp))
