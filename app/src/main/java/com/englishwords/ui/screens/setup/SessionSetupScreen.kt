package com.englishwords.ui.screens.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.englishwords.domain.model.AnswerType
import com.englishwords.domain.model.LearningMode
import com.englishwords.domain.model.SessionConfig
import com.englishwords.domain.model.SessionMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSetupScreen(
    onNavigateBack: () -> Unit,
    onStartLearning: (SessionConfig) -> Unit
) {
    val viewModel: SessionSetupViewModel = viewModel()
    val config by viewModel.config.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройка сессии") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Режим обучения
            Text(
                text = "Режим обучения:",
                style = MaterialTheme.typography.titleMedium
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LearningModeOption(
                    text = "EN → RU",
                    selected = config.learningMode == LearningMode.EN_TO_RU,
                    onClick = { viewModel.setLearningMode(LearningMode.EN_TO_RU) }
                )
                LearningModeOption(
                    text = "RU → EN",
                    selected = config.learningMode == LearningMode.RU_TO_EN,
                    onClick = { viewModel.setLearningMode(LearningMode.RU_TO_EN) }
                )
                LearningModeOption(
                    text = "Смешанный режим",
                    selected = config.learningMode == LearningMode.MIXED,
                    onClick = { viewModel.setLearningMode(LearningMode.MIXED) }
                )
            }
            
            Divider()
            
            // Тип ответа
            Text(
                text = "Тип ответа:",
                style = MaterialTheme.typography.titleMedium
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AnswerTypeOption(
                    text = "Ввод текста",
                    selected = config.answerType == AnswerType.TEXT_INPUT,
                    onClick = { viewModel.setAnswerType(AnswerType.TEXT_INPUT) }
                )
                AnswerTypeOption(
                    text = "Выбор из вариантов",
                    selected = config.answerType == AnswerType.MULTIPLE_CHOICE,
                    onClick = { viewModel.setAnswerType(AnswerType.MULTIPLE_CHOICE) }
                )
            }
            
            Divider()
            
            // Количество слов
            Text(
                text = "Количество слов: ${config.wordCount}",
                style = MaterialTheme.typography.titleMedium
            )
            
            Slider(
                value = config.wordCount.toFloat(),
                onValueChange = { viewModel.setWordCount(it.toInt()) },
                valueRange = 5f..50f,
                steps = 44
            )
            
            Text(
                text = "от 5 до 50",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Divider()
            
            // Тип слов
            Text(
                text = "Тип слов:",
                style = MaterialTheme.typography.titleMedium
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SessionModeOption(
                    text = "Новые слова",
                    selected = config.sessionMode == SessionMode.NEW_WORDS,
                    onClick = { viewModel.setSessionMode(SessionMode.NEW_WORDS) }
                )
                SessionModeOption(
                    text = "На повторении",
                    selected = config.sessionMode == SessionMode.REVIEW,
                    onClick = { viewModel.setSessionMode(SessionMode.REVIEW) }
                )
                SessionModeOption(
                    text = "Все слова",
                    selected = config.sessionMode == SessionMode.ALL,
                    onClick = { viewModel.setSessionMode(SessionMode.ALL) }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Кнопка начать
            Button(
                onClick = { onStartLearning(config) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Начать",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun LearningModeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SessionModeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AnswerTypeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}
