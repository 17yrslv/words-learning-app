package com.englishwords.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.englishwords.data.preferences.ThemePreferences
import com.englishwords.data.repository.WordRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: WordRepository,
    themePreferences: ThemePreferences,
    onNavigateToSetup: () -> Unit,
    onNavigateToAddWord: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val isDarkTheme by themePreferences.isDarkTheme.collectAsState(initial = true)
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("English Words Learning") },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                themePreferences.setDarkTheme(!isDarkTheme)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                            contentDescription = if (isDarkTheme) "Переключить на светлую тему" else "Переключить на тёмную тему"
                        )
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
                            text = "Статистика",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        StatRow("Всего слов:", uiState.totalWords.toString())
                        StatRow("Изучается:", uiState.learningWords.toString())
                        StatRow("Выучено:", uiState.learnedWords.toString())
                        StatRow("На повторении сегодня:", uiState.reviewWords.toString())
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Кнопки
                Button(
                    onClick = onNavigateToSetup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("🎯 Начать обучение", style = MaterialTheme.typography.titleMedium)
                }
                
                OutlinedButton(
                    onClick = onNavigateToAddWord,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("➕ Добавить слово", style = MaterialTheme.typography.titleMedium)
                }
                
                OutlinedButton(
                    onClick = onNavigateToStatistics,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("📈 Статистика", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
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
