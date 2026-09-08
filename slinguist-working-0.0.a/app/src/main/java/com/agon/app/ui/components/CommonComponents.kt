package com.agon.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.agon.app.data.model.Difficulty
import com.agon.app.data.model.UserProfile
import com.agon.app.data.model.VocabularyCard
import com.agon.app.ui.theme.EasyGreen
import com.agon.app.ui.theme.GoldFavorite
import com.agon.app.ui.theme.HardViolet
import com.agon.app.ui.theme.MediumBlue
import java.io.File

@Composable
fun <T> SelectionField(
    label: String,
    selected: T,
    options: List<T>,
    title: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        )
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    text = title(selected),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Открыть список")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(title(option)) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun DifficultyPill(difficulty: Difficulty) {
    val color = when (difficulty) {
        Difficulty.EASY -> EasyGreen
        Difficulty.MEDIUM -> MediumBlue
        Difficulty.HARD -> HardViolet
    }
    Surface(
        color = color.copy(alpha = 0.13f),
        contentColor = color,
        shape = RoundedCornerShape(50),
    ) {
        Text(
            text = difficulty.title,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}

@Composable
fun DifficultyBadge(difficulty: Difficulty) = DifficultyPill(difficulty)

@Composable
fun TagPill(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(50),
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun MetaChip(text: String) = TagPill(text)

@Composable
fun ProfileAvatar(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    size: Int = 52,
) {
    AvatarView(
        initials = if (profile.name.isNotBlank()) profile.name.trim().take(1).uppercase() else "",
        avatarPath = profile.avatarPath,
        size = size,
        modifier = modifier
    )
}

@Composable
fun AvatarView(
    initials: String,
    avatarPath: String,
    size: Int = 46,
    modifier: Modifier = Modifier
) {
    val avatarModifier = modifier
        .size(size.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer)
    
    if (avatarPath.isNotBlank() && File(avatarPath).exists()) {
        AsyncImage(
            model = File(avatarPath),
            contentDescription = "Аватар",
            contentScale = ContentScale.Crop,
            modifier = avatarModifier,
        )
    } else {
        Box(modifier = avatarModifier, contentAlignment = Alignment.Center) {
            if (initials.isNotBlank()) {
                Text(
                    text = initials,
                    style = if (size > 60) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            } else {
                Icon(Icons.Outlined.Person, contentDescription = null)
            }
        }
    }
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        action?.invoke()
    }
}

@Composable
fun StatTile(
    value: String,
    label: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = tint)
            Spacer(Modifier.size(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OptionPickerSheet(
    title: String,
    options: List<T>,
    selected: T,
    labelOf: (T) -> String,
    leadingOf: (T) -> String = { "" },
    subtitleOf: (T) -> String = { "" },
    allowAll: Boolean = false,
    onSelect: (T?) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            }
            items(options.size) { index ->
                val option = options[index]
                ListItem(
                    headlineContent = { Text(labelOf(option)) },
                    supportingContent = { if (subtitleOf(option).isNotBlank()) Text(subtitleOf(option)) },
                    leadingContent = { if (leadingOf(option).isNotBlank()) Text(leadingOf(option)) },
                    trailingContent = {
                        RadioButton(
                            selected = option == selected,
                            onClick = { onSelect(option); onDismiss() }
                        )
                    },
                    modifier = Modifier.clickable { onSelect(option); onDismiss() }
                )
            }
        }
    }
}

@Composable
fun VocabularyCardRow(
    card: VocabularyCard,
    onOpen: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(start = 18.dp, top = 16.dp, bottom = 14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.phrase,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (card.pronunciation.isNotBlank()) {
                        Text(
                            text = card.pronunciation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    Text(
                        text = card.translation,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(onClick = onFavorite) {
                    Icon(
                        imageVector = if (card.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (card.isFavorite) "Убрать из избранного" else "В избранное",
                        tint = if (card.isFavorite) GoldFavorite else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DifficultyPill(card.difficulty)
                TagPill(card.language.title, modifier = Modifier.weight(1f, fill = false))
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onOpen, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Редактировать", modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    text: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ) {
            Box(modifier = Modifier.padding(22.dp)) { icon() }
        }
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 20.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
