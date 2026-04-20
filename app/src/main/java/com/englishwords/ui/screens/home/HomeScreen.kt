package com.englishwords.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.englishwords.data.preferences.LanguagePreferences
import com.englishwords.data.preferences.SpacePreferences
import com.englishwords.data.preferences.ThemePreferences
import com.englishwords.data.repository.WordRepository
import com.englishwords.ui.components.AnimatedButton
import com.englishwords.ui.localization.Strings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    repository: WordRepository,
    themePreferences: ThemePreferences,
    languagePreferences: LanguagePreferences,
    spacePreferences: SpacePreferences,
    strings: Strings,
    onNavigateToSetup: () -> Unit,
    onNavigateToAddWord: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository, spacePreferences)
    )
    val uiState by viewModel.uiState.collectAsState()
    val isDarkTheme by themePreferences.isDarkTheme.collectAsState(initial = true)
    val language by languagePreferences.language.collectAsState(initial = "en")
    val scope = rememberCoroutineScope()
    
    // Обновляем статистику при каждом возврате на экран
    LaunchedEffect(Unit) {
        viewModel.loadStatistics()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    TextButton(
                        onClick = { viewModel.showSpaceDialog() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.currentSpace?.name ?: "English",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select space"
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = strings.settings
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Логотип поверх всего, абсолютно по центру
            Text(
                text = "Wordy",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = paddingValues.calculateTopPadding() / 2 - 12.dp)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                // Статистика
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = strings.statistics,
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        StatRow(strings.totalWords, uiState.totalWords.toString())
                        StatRow(strings.learning, uiState.learningWords.toString())
                        StatRow(strings.learned, uiState.learnedWords.toString())
                        StatRow(strings.reviewToday, uiState.reviewWords.toString())
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Кнопки
                AnimatedButton(
                    onClick = onNavigateToSetup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(strings.startLearning, style = MaterialTheme.typography.titleMedium)
                }
                
                OutlinedButton(
                    onClick = onNavigateToAddWord,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(strings.addWord, style = MaterialTheme.typography.titleMedium)
                }
                
                OutlinedButton(
                    onClick = onNavigateToStatistics,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(strings.statistics, style = MaterialTheme.typography.titleMedium)
                }
                
                OutlinedButton(
                    onClick = onNavigateToFavorites,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(strings.favorites, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        }
    }
    
    // Диалог выбора пространства
    if (uiState.showSpaceDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideSpaceDialog() },
            title = { Text("Выберите пространство") },
            text = {
                Column {
                    uiState.spaces.forEach { space ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { viewModel.selectSpace(space) },
                                    onLongClick = { 
                                        if (space.id != 1L) { // Нельзя удалить пространство по умолчанию
                                            viewModel.showDeleteSpaceDialog(space)
                                        }
                                    }
                                )
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            Text(
                                text = space.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (space.id == uiState.currentSpace?.id) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    TextButton(
                        onClick = { viewModel.showCreateSpaceDialog() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Создать новое пространство")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.hideSpaceDialog() }) {
                    Text("Закрыть")
                }
            }
        )
    }
    
    // Диалог создания нового пространства
    if (uiState.showCreateSpaceDialog) {
        var spaceName by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { viewModel.hideCreateSpaceDialog() },
            title = { Text("Новое пространство") },
            text = {
                Column {
                    Text("Введите название пространства (например, Italian, Spanish):")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = spaceName,
                        onValueChange = { spaceName = it },
                        label = { Text("Название") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        if (spaceName.isNotBlank()) {
                            viewModel.createSpace(spaceName.trim())
                            spaceName = ""
                        }
                    },
                    enabled = spaceName.isNotBlank()
                ) {
                    Text("Создать")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideCreateSpaceDialog() }) {
                    Text("Отмена")
                }
            }
        )
    }
    
    // Диалог подтверждения удаления пространства
    if (uiState.showDeleteSpaceDialog && uiState.spaceToDelete != null) {
        val spaceToDelete = uiState.spaceToDelete
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteSpaceDialog() },
            title = { Text("Удалить пространство?") },
            text = {
                Column {
                    Text("Вы уверены, что хотите удалить пространство \"${spaceToDelete?.name}\"?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Все слова из этого пространства будут удалены безвозвратно.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteSpace() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteSpaceDialog() }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
