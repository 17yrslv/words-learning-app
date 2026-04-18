package com.englishwords.ui.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.englishwords.data.repository.WordRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    repository: WordRepository,
    onNavigateBack: () -> Unit
) {
    val viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(repository)
    )
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Общая статистика
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "📊 Общая статистика",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Divider()
                        
                        StatRow("Всего слов:", uiState.totalWords.toString())
                        StatRow("Изучается:", uiState.learningWords.toString())
                        StatRow("Выучено:", uiState.learnedWords.toString())
                        StatRow("На повторении:", uiState.reviewWords.toString())
                    }
                }
                
                // Прогресс
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "📈 Прогресс изучения",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Divider()
                        
                        LinearProgressIndicator(
                            progress = uiState.accuracy / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                        )
                        
                        Text(
                            text = "${uiState.accuracy}%",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            text = "Изучено ${uiState.learnedWords + uiState.learningWords} из ${uiState.totalWords} слов",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
                
                // Дополнительная информация
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ℹ️ Информация",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Divider()
                        
                        Text(
                            text = "Система интервальных повторений помогает эффективно запоминать слова.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        
                        Text(
                            text = "Слова проходят через 6 уровней запоминания с интервалами: 0, 1, 3, 7, 14, 30 дней.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
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
