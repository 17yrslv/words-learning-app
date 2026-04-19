package com.englishwords.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.englishwords.data.preferences.LanguagePreferences
import com.englishwords.data.preferences.ThemePreferences
import com.englishwords.ui.localization.Strings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themePreferences: ThemePreferences,
    languagePreferences: LanguagePreferences,
    strings: Strings,
    onNavigateBack: () -> Unit
) {
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(themePreferences, languagePreferences)
    )
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val scope = rememberCoroutineScope()
    
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.settings) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = strings.back)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Тема
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch {
                                viewModel.toggleTheme()
                            }
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.Brightness4 else Icons.Default.Brightness7,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = strings.theme,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = if (isDarkTheme) strings.darkTheme else strings.lightTheme,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = {
                            scope.launch {
                                viewModel.toggleTheme()
                            }
                        }
                    )
                }
            }
            
            // Язык
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguageDialog = true }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = strings.language,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = when (currentLanguage) {
                                    "en" -> "English"
                                    "ru" -> "Русский"
                                    else -> "English"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Диалог выбора языка
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(strings.selectLanguage) },
            text = {
                Column {
                    LanguageOption(
                        language = "English",
                        selected = currentLanguage == "en",
                        onClick = {
                            scope.launch {
                                viewModel.setLanguage("en")
                                showLanguageDialog = false
                            }
                        }
                    )
                    LanguageOption(
                        language = "Русский",
                        selected = currentLanguage == "ru",
                        onClick = {
                            scope.launch {
                                viewModel.setLanguage("ru")
                                showLanguageDialog = false
                            }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

@Composable
fun LanguageOption(
    language: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = language,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
