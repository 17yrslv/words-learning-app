package com.englishwords.ui.screens.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.englishwords.domain.model.SessionResult
import com.englishwords.domain.model.WordError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionResultScreen(
    result: SessionResult?,
    onNavigateHome: () -> Unit,
    onRepeatErrors: () -> Unit
) {
    var showCorrectWords by remember { mutableStateOf(false) }
    var showIncorrectWords by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Результаты сессии") }
            )
        }
    ) { paddingValues ->
        result?.let { sessionResult: SessionResult ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Заголовок
                    Text(
                        text = if (sessionResult.accuracy >= 80) "🎉 Отличная работа!" 
                              else if (sessionResult.accuracy >= 60) "👍 Хорошо!" 
                              else "💪 Продолжай практиковаться!",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                
                item {
                    // Основная статистика
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Статистика сессии",
                                style = MaterialTheme.typography.titleLarge
                            )
                            
                            Divider()
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Всего слов:",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${sessionResult.totalWords}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "✅ Правильно:",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${sessionResult.correctAnswers}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "❌ Неправильно:",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${sessionResult.incorrectAnswers}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            
                            Divider()
                            
                            // Прогресс-бар
                            LinearProgressIndicator(
                                progress = sessionResult.accuracy / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                            )
                            
                            Text(
                                text = "Точность: ${sessionResult.accuracy}%",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
                
                // Правильные слова (раскрывающийся список)
                if (sessionResult.correctWords.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCorrectWords = !showCorrectWords }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✅ Правильные слова (${sessionResult.correctWords.size})",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Icon(
                                    imageVector = if (showCorrectWords) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (showCorrectWords) "Свернуть" else "Развернуть"
                                )
                            }
                        }
                    }
                    
                    if (showCorrectWords) {
                        items(sessionResult.correctWords) { word ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = word.englishWord,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = word.getRussianTranslationsList().firstOrNull() ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Неправильные слова (раскрывающийся список)
                if (sessionResult.wordsWithErrors.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showIncorrectWords = !showIncorrectWords }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "❌ Неправильные слова (${sessionResult.wordsWithErrors.size})",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Icon(
                                    imageVector = if (showIncorrectWords) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (showIncorrectWords) "Свернуть" else "Развернуть"
                                )
                            }
                        }
                    }
                    
                    if (showIncorrectWords) {
                        items(sessionResult.wordsWithErrors) { wordError ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = wordError.word.englishWord,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Правильно: ${wordError.correctAnswers.firstOrNull() ?: ""}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Ваш ответ: ${wordError.userAnswer}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                        
                        item {
                            OutlinedButton(
                                onClick = onRepeatErrors,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Text("Повторить ошибки")
                            }
                        }
                    }
                }
                
                item {
                    Button(
                        onClick = onNavigateHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("На главную")
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Нет данных о результатах")
            }
        }
    }
}
