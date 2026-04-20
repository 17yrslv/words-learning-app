package com.englishwords.ui.screens.setup

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.englishwords.domain.model.AnswerType
import com.englishwords.domain.model.LearningMode
import com.englishwords.domain.model.SessionConfig
import com.englishwords.domain.model.SessionMode
import com.englishwords.ui.components.AnimatedButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSetupScreen(
    repository: com.englishwords.data.repository.WordRepository,
    spacePreferences: com.englishwords.data.preferences.SpacePreferences,
    strings: com.englishwords.ui.localization.Strings,
    onNavigateBack: () -> Unit,
    onStartLearning: (SessionConfig) -> Unit
) {
    val viewModel: SessionSetupViewModel = viewModel(
        factory = SessionSetupViewModelFactory(repository, spacePreferences)
    )
    val config by viewModel.config.collectAsState()
    val validationError by viewModel.validationError.collectAsState()
    val availableWordsCount by viewModel.availableWordsCount.collectAsState()
    
    LaunchedEffect(config.sessionMode) {
        viewModel.updateAvailableWordsCount()
    }
    
    LaunchedEffect(config.wordCount, availableWordsCount) {
        viewModel.validateSession()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.sessionSetup) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Режим обучения
            Text(
                text = strings.learningMode,
                style = MaterialTheme.typography.titleMedium
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LearningModeOption(
                    text = strings.enToRu,
                    selected = config.learningMode == LearningMode.EN_TO_RU,
                    onClick = { viewModel.setLearningMode(LearningMode.EN_TO_RU) }
                )
                LearningModeOption(
                    text = strings.ruToEn,
                    selected = config.learningMode == LearningMode.RU_TO_EN,
                    onClick = { viewModel.setLearningMode(LearningMode.RU_TO_EN) }
                )
                LearningModeOption(
                    text = strings.mixedMode,
                    selected = config.learningMode == LearningMode.MIXED,
                    onClick = { viewModel.setLearningMode(LearningMode.MIXED) }
                )
            }
            
            Divider()
            
            // Тип ответа
            Text(
                text = strings.answerType,
                style = MaterialTheme.typography.titleMedium
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AnswerTypeOption(
                    text = strings.textInput,
                    selected = config.answerType == AnswerType.TEXT_INPUT,
                    onClick = { viewModel.setAnswerType(AnswerType.TEXT_INPUT) }
                )
                AnswerTypeOption(
                    text = strings.multipleChoice,
                    selected = config.answerType == AnswerType.MULTIPLE_CHOICE,
                    onClick = { viewModel.setAnswerType(AnswerType.MULTIPLE_CHOICE) }
                )
            }
            
            Divider()
            
            // Количество слов
            Text(
                text = "${strings.wordCount} ${config.wordCount}",
                style = MaterialTheme.typography.titleMedium
            )
            
            Slider(
                value = config.wordCount.toFloat(),
                onValueChange = { viewModel.setWordCount(it.toInt()) },
                valueRange = 5f..50f,
                steps = 44
            )
            
            Text(
                text = strings.wordCountRange,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Divider()
            
            // Тип слов
            Text(
                text = strings.wordType,
                style = MaterialTheme.typography.titleMedium
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SessionModeOption(
                    text = strings.newWords,
                    selected = config.sessionMode == SessionMode.NEW_WORDS,
                    onClick = { viewModel.setSessionMode(SessionMode.NEW_WORDS) }
                )
                SessionModeOption(
                    text = strings.review,
                    selected = config.sessionMode == SessionMode.REVIEW,
                    onClick = { viewModel.setSessionMode(SessionMode.REVIEW) }
                )
                SessionModeOption(
                    text = strings.allWords,
                    selected = config.sessionMode == SessionMode.ALL,
                    onClick = { viewModel.setSessionMode(SessionMode.ALL) }
                )
                SessionModeOption(
                    text = strings.favoriteWords,
                    selected = config.sessionMode == SessionMode.FAVORITES,
                    onClick = { viewModel.setSessionMode(SessionMode.FAVORITES) }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Ошибка валидации
            if (validationError != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = validationError!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Кнопка начать
            AnimatedButton(
                onClick = { 
                    if (viewModel.validateSession()) {
                        onStartLearning(config)
                    }
                },
                enabled = validationError == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = strings.start,
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
        AnimatedRadioButton(selected = selected)
        Spacer(modifier = Modifier.width(12.dp))
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
        AnimatedRadioButton(selected = selected)
        Spacer(modifier = Modifier.width(12.dp))
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
        AnimatedRadioButton(selected = selected)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AnimatedRadioButton(
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val fillProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "radio_fill"
    )
    
    val strokeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
    val fillColor = MaterialTheme.colorScheme.primary
    
    Canvas(
        modifier = modifier.size(24.dp)
    ) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)
        
        // Внешний круг (обводка)
        drawCircle(
            color = strokeColor,
            radius = radius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )
        
        // Заливка с анимацией
        if (fillProgress > 0f) {
            drawCircle(
                color = fillColor,
                radius = radius * 0.7f * fillProgress,
                center = center
            )
        }
    }
}
