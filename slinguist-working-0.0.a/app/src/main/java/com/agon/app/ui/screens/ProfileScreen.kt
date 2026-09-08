package com.agon.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.agon.app.data.model.UserProfile
import com.agon.app.ui.components.ProfileAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: UserProfile,
    onSaveProfile: (String, String) -> Unit,
    onAvatarSelected: (Uri) -> Unit,
    onRemoveAvatar: () -> Unit,
    onThemeChange: (Boolean) -> Unit,
) {
    var name by remember { mutableStateOf(profile.name) }
    var email by remember { mutableStateOf(profile.email) }
    var message by remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let(onAvatarSelected)
    }

    LaunchedEffect(profile.name, profile.email) {
        name = profile.name
        email = profile.email
    }

    Column {
        TopAppBar(title = { Text("Профиль") })
        LazyColumn(
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ProfileAvatar(profile = profile, size = 92)
                        Text(profile.name.ifBlank { "Лингвист" }, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp))
                        Text(profile.email.ifBlank { "Локальный профиль" }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 3.dp))
                        Row(modifier = Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { launcher.launch("image/*") }) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Text("Аватар", modifier = Modifier.padding(start = 6.dp))
                            }
                            if (profile.avatarPath.isNotBlank()) {
                                TextButton(onClick = onRemoveAvatar) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null)
                                    Text("Удалить", modifier = Modifier.padding(start = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
            item { Text("Личные данные", style = MaterialTheme.typography.titleLarge) }
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; message = null },
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; message = null },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    isError = message != null,
                    supportingText = { if (message != null) Text(message!!) else Text("Хранится только на этом устройстве") },
                )
            }
            item {
                Button(
                    onClick = {
                        if (email.isNotBlank() && (!email.contains('@') || !email.substringAfter('@').contains('.'))) {
                            message = "Проверьте формат email"
                        } else {
                            onSaveProfile(name, email)
                            message = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 13.dp),
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Text("Сохранить профиль", modifier = Modifier.padding(start = 8.dp))
                }
            }
            item { Text("Оформление", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp)) }
            item {
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    ListItem(
                        headlineContent = { Text(if (profile.darkTheme) "Тёмная тема" else "Светлая тема") },
                        supportingContent = { Text("Выбор сохранится после перезапуска") },
                        leadingContent = {
                            Icon(
                                if (profile.darkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        },
                        trailingContent = {
                            Switch(checked = profile.darkTheme, onCheckedChange = onThemeChange)
                        },
                    )
                }
            }
            item {
                Text(
                    "SLinguist работает полностью локально. Карточки, профиль, аватар и статистика не покидают устройство.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
