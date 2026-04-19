package com.englishwords.ui.screens.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.englishwords.data.preferences.LanguagePreferences
import com.englishwords.data.preferences.ThemePreferences
import com.englishwords.data.repository.WordRepository
import com.englishwords.ui.localization.Strings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themePreferences: ThemePreferences,
    languagePreferences: LanguagePreferences,
    wordRepository: WordRepository,
    strings: Strings,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(themePreferences, languagePreferences, wordRepository, context)
    )
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val exportImportState by viewModel.exportImportState.collectAsState()
    val scope = rememberCoroutineScope()
    
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    
    // Лаунчер для экспорта
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        uri?.let {
            try {
                val outputStream = context.contentResolver.openOutputStream(it)
                if (outputStream != null) {
                    viewModel.exportWords(outputStream)
                }
            } catch (e: Exception) {
                // Обработка ошибки
            }
        }
    }
    
    // Лаунчер для импорта
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                if (inputStream != null) {
                    viewModel.importWords(inputStream)
                }
            } catch (e: Exception) {
                // Обработка ошибки
            }
        }
    }
    
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
            
            // Экспорт/Импорт
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Управление данными",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    // Кнопка экспорта
                    OutlinedButton(
                        onClick = {
                            exportLauncher.launch("english_words_export.csv")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = exportImportState !is ExportImportState.Loading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Экспортировать словарь")
                    }
                    
                    // Кнопка импорта
                    OutlinedButton(
                        onClick = {
                            importLauncher.launch(arrayOf("text/csv", "text/comma-separated-values"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = exportImportState !is ExportImportState.Loading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Импортировать словарь")
                    }
                    
                    // Кнопка удаления всех слов
                    OutlinedButton(
                        onClick = {
                            showDeleteConfirmDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = exportImportState !is ExportImportState.Loading,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Удалить весь словарь")
                    }
                    
                    // Индикатор загрузки
                    if (exportImportState is ExportImportState.Loading) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
    
    // Snackbar для результатов экспорта/импорта
    LaunchedEffect(exportImportState) {
        when (val state = exportImportState) {
            is ExportImportState.Success -> {
                // Показать сообщение об успехе
                kotlinx.coroutines.delay(2000)
                viewModel.resetState()
            }
            is ExportImportState.Error -> {
                // Показать сообщение об ошибке
                kotlinx.coroutines.delay(3000)
                viewModel.resetState()
            }
            else -> {}
        }
    }
    
    // Диалог с результатом
    when (val state = exportImportState) {
        is ExportImportState.Success -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetState() },
                title = { Text("Успешно") },
                text = { Text(state.message) },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetState() }) {
                        Text("OK")
                    }
                }
            )
        }
        is ExportImportState.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetState() },
                title = { Text("Ошибка") },
                text = { Text(state.message) },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetState() }) {
                        Text("OK")
                    }
                }
            )
        }
        else -> {}
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
    
    // Диалог подтверждения удаления
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Удалить весь словарь?") },
            text = { Text("Это действие удалит все слова и их статистику. Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllWords()
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Отмена")
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
