package com.englishwords.ui.screens.learning

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.englishwords.data.repository.WordRepository
import com.englishwords.domain.model.AnswerType
import com.englishwords.domain.model.LearningMode
import com.englishwords.domain.model.SessionConfig
import com.englishwords.domain.model.SessionResult
import com.englishwords.ui.screens.learning.components.AnswerInput
import com.englishwords.ui.screens.learning.components.MultipleChoiceOptions
import com.englishwords.ui.screens.learning.components.ResultCard
import com.englishwords.ui.screens.learning.components.WordCard
import com.englishwords.ui.components.AnimatedButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(
    repository: WordRepository,
    spacePreferences: com.englishwords.data.preferences.SpacePreferences,
    config: SessionConfig,
    strings: com.englishwords.ui.localization.Strings,
    onNavigateBack: () -> Unit,
    onSessionComplete: (SessionResult) -> Unit
) {
    val viewModel: LearningViewModel = viewModel(
        factory = LearningViewModelFactory(repository, spacePreferences, config)
    )
    val uiState by viewModel.uiState.collectAsState()
    var showFinishDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(uiState.sessionComplete) {
        if (uiState.sessionComplete) {
            val result = viewModel.getSessionResult()
            onSessionComplete(result)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(strings.learningTitle)
                        if (!uiState.isLoading) {
                            Text(
                                text = uiState.progressText,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = strings.exit)
                    }
                },
                actions = {
                    TextButton(onClick = { showFinishDialog = true }) {
                        Text(strings.finishSession)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showFinishDialog) {
            FinishSessionDialog(
                strings = strings,
                onDismiss = { showFinishDialog = false },
                onConfirm = {
                    showFinishDialog = false
                    viewModel.finishSessionEarly()
                }
            )
        }
        
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
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Прогресс-бар
                LinearProgressIndicator(
                    progress = uiState.progress,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Карточка со словом
                val modeText = when (uiState.currentMode) {
                    LearningMode.EN_TO_RU -> "EN → RU"
                    LearningMode.RU_TO_EN -> "RU → EN"
                    else -> ""
                }
                
                WordCard(
                    word = viewModel.getQuestionText(),
                    mode = modeText,
                    isFavorite = uiState.currentWord?.isFavorite ?: false,
                    onFavoriteClick = { viewModel.toggleFavorite() }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                when (uiState.answerState) {
                    AnswerState.INPUT -> {
                        if (config.answerType == AnswerType.TEXT_INPUT) {
                            // Режим ввода текста
                            AnswerInput(
                                value = uiState.userAnswer,
                                onValueChange = { viewModel.onAnswerChange(it) },
                                onSubmit = { viewModel.checkAnswer() }
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Кнопка проверить
                            AnimatedButton(
                                onClick = { viewModel.checkAnswer() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = uiState.userAnswer.isNotBlank()
                            ) {
                                Text(strings.check, style = MaterialTheme.typography.titleMedium)
                            }
                            
                            // Дополнительное пространство снизу для клавиатуры
                            Spacer(modifier = Modifier.height(200.dp))
                        } else {
                            // Режим выбора из вариантов
                            MultipleChoiceOptions(
                                options = uiState.answerOptions,
                                selectedOption = uiState.selectedOption,
                                correctOption = uiState.correctOption,
                                onOptionSelected = { viewModel.selectOption(it) }
                            )
                        }
                    }
                    
                    AnswerState.CORRECT, AnswerState.INCORRECT -> {
                        if (config.answerType == AnswerType.TEXT_INPUT) {
                            // Карточка с результатом для текстового ввода
                            ResultCard(
                                isCorrect = uiState.answerState == AnswerState.CORRECT,
                                userAnswer = uiState.userAnswer,
                                correctAnswers = viewModel.getCorrectAnswersList(),
                                onNext = { viewModel.nextWord() }
                            )
                        } else {
                            // Для режима выбора показываем варианты с подсветкой и кнопку "Далее"
                            MultipleChoiceOptions(
                                options = uiState.answerOptions,
                                selectedOption = uiState.selectedOption,
                                correctOption = uiState.correctOption,
                                onOptionSelected = { }
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            AnimatedButton(
                                onClick = { viewModel.nextWord() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Text(strings.next, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinishSessionDialog(
    strings: com.englishwords.ui.localization.Strings,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = strings.finishSession)
        },
        text = {
            Text(text = strings.finishSessionMessage)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(strings.finishSessionConfirm)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}
