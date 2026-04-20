package com.englishwords.ui.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.englishwords.data.local.Word
import com.englishwords.data.preferences.SpacePreferences
import com.englishwords.data.repository.WordRepository
import com.englishwords.domain.model.AnswerType
import com.englishwords.domain.model.LearningMode
import com.englishwords.domain.model.SessionConfig
import com.englishwords.domain.model.SessionResult
import com.englishwords.domain.model.WordError
import com.englishwords.domain.usecase.CheckAnswerUseCase
import com.englishwords.domain.usecase.GenerateAnswerOptionsUseCase
import com.englishwords.domain.usecase.GetSessionWordsUseCase
import com.englishwords.domain.usecase.UpdateWordProgressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AnswerState {
    INPUT,      // Ввод ответа
    CORRECT,    // Правильный ответ
    INCORRECT   // Неправильный ответ
}

data class LearningUiState(
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val userAnswer: String = "",
    val answerState: AnswerState = AnswerState.INPUT,
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0,
    val wordsWithErrors: List<WordError> = emptyList(),
    val correctWords: List<Word> = emptyList(),
    val isLoading: Boolean = true,
    val sessionComplete: Boolean = false,
    val currentMode: LearningMode = LearningMode.EN_TO_RU,
    val answerOptions: List<String> = emptyList(),
    val selectedOption: String? = null,
    val correctOption: String? = null
) {
    val currentWord: Word?
        get() = words.getOrNull(currentIndex)
    
    val progress: Float
        get() = if (words.isNotEmpty()) (currentIndex + 1).toFloat() / words.size else 0f
    
    val progressText: String
        get() = "${currentIndex + 1}/${words.size}"
}

class LearningViewModel(
    private val repository: WordRepository,
    private val spacePreferences: SpacePreferences,
    private val config: SessionConfig
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()
    
    private val checkAnswerUseCase = CheckAnswerUseCase()
    private val getSessionWordsUseCase = GetSessionWordsUseCase(repository, spacePreferences)
    private val updateWordProgressUseCase = UpdateWordProgressUseCase(repository)
    private val generateAnswerOptionsUseCase = GenerateAnswerOptionsUseCase(repository, spacePreferences)
    
    init {
        loadWords()
    }
    
    private fun loadWords() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val words = getSessionWordsUseCase.execute(config)
            
            _uiState.value = _uiState.value.copy(
                words = words,
                isLoading = false,
                currentMode = determineCurrentMode()
            )
            
            // Если режим теста, загружаем варианты ответов
            if (config.answerType == AnswerType.MULTIPLE_CHOICE) {
                loadAnswerOptions()
            }
        }
    }
    
    private fun determineCurrentMode(): LearningMode {
        return when (config.learningMode) {
            LearningMode.MIXED -> {
                if (Math.random() < 0.5) LearningMode.EN_TO_RU else LearningMode.RU_TO_EN
            }
            else -> config.learningMode
        }
    }
    
    fun onAnswerChange(answer: String) {
        _uiState.value = _uiState.value.copy(userAnswer = answer)
    }
    
    fun checkAnswer() {
        val state = _uiState.value
        val currentWord = state.currentWord ?: return
        
        val correctAnswers = when (state.currentMode) {
            LearningMode.EN_TO_RU -> {
                currentWord.russianTranslations
            }
            LearningMode.RU_TO_EN -> {
                currentWord.englishWord
            }
            else -> return
        }
        
        val isCorrect = checkAnswerUseCase.execute(state.userAnswer, correctAnswers)
        
        viewModelScope.launch {
            updateWordProgressUseCase.execute(currentWord, isCorrect)
        }
        
        val newState = if (isCorrect) {
            state.copy(
                answerState = AnswerState.CORRECT,
                correctAnswers = state.correctAnswers + 1,
                correctWords = state.correctWords + currentWord
            )
        } else {
            val correctAnswersList = if (correctAnswers is String) {
                correctAnswers.split(",").map { answer -> answer.trim() }
            } else {
                listOf(correctAnswers.toString())
            }
            
            state.copy(
                answerState = AnswerState.INCORRECT,
                incorrectAnswers = state.incorrectAnswers + 1,
                wordsWithErrors = state.wordsWithErrors + WordError(
                    word = currentWord,
                    userAnswer = state.userAnswer,
                    correctAnswers = correctAnswersList
                )
            )
        }
        
        _uiState.value = newState
    }
    
    fun nextWord() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        
        if (nextIndex >= state.words.size) {
            // Сессия завершена
            _uiState.value = state.copy(sessionComplete = true)
        } else {
            // Следующее слово
            val newMode = if (config.learningMode == LearningMode.MIXED) {
                determineCurrentMode()
            } else {
                state.currentMode
            }
            
            _uiState.value = state.copy(
                currentIndex = nextIndex,
                userAnswer = "",
                answerState = AnswerState.INPUT,
                currentMode = newMode,
                selectedOption = null,
                correctOption = null
            )
            
            // Если режим теста, загружаем новые варианты
            if (config.answerType == AnswerType.MULTIPLE_CHOICE) {
                loadAnswerOptions()
            }
        }
    }
    
    private fun loadAnswerOptions() {
        viewModelScope.launch {
            val state = _uiState.value
            val currentWord = state.currentWord ?: return@launch
            
            val options = generateAnswerOptionsUseCase.execute(currentWord, state.currentMode)
            _uiState.value = state.copy(answerOptions = options)
        }
    }
    
    fun selectOption(option: String) {
        val state = _uiState.value
        
        // Сразу проверяем ответ
        val currentWord = state.currentWord ?: return
        
        val correctAnswer = when (state.currentMode) {
            LearningMode.EN_TO_RU -> {
                currentWord.getRussianTranslationsList().firstOrNull() ?: ""
            }
            LearningMode.RU_TO_EN -> {
                currentWord.englishWord
            }
            else -> return
        }
        
        val isCorrect = option == correctAnswer
        
        viewModelScope.launch {
            updateWordProgressUseCase.execute(currentWord, isCorrect)
        }
        
        val newState = if (isCorrect) {
            state.copy(
                selectedOption = option,
                correctOption = correctAnswer,
                answerState = AnswerState.CORRECT,
                correctAnswers = state.correctAnswers + 1,
                correctWords = state.correctWords + currentWord
            )
        } else {
            state.copy(
                selectedOption = option,
                correctOption = correctAnswer,
                answerState = AnswerState.INCORRECT,
                incorrectAnswers = state.incorrectAnswers + 1,
                wordsWithErrors = state.wordsWithErrors + WordError(
                    word = currentWord,
                    userAnswer = option,
                    correctAnswers = listOf(correctAnswer)
                )
            )
        }
        
        _uiState.value = newState
    }
    
    fun getSessionResult(): SessionResult {
        val state = _uiState.value
        return SessionResult(
            totalWords = state.words.size,
            correctAnswers = state.correctAnswers,
            incorrectAnswers = state.incorrectAnswers,
            wordsWithErrors = state.wordsWithErrors,
            correctWords = state.correctWords
        )
    }
    
    fun getQuestionText(): String {
        val state = _uiState.value
        val currentWord = state.currentWord ?: return ""
        
        return when (state.currentMode) {
            LearningMode.EN_TO_RU -> currentWord.englishWord
            LearningMode.RU_TO_EN -> {
                val russianOptions = currentWord.getRussianTranslationsList()
                russianOptions.firstOrNull() ?: ""
            }
            else -> ""
        }
    }
    
    fun getCorrectAnswersList(): List<String> {
        val state = _uiState.value
        val currentWord = state.currentWord ?: return emptyList()
        
        return when (state.currentMode) {
            LearningMode.EN_TO_RU -> currentWord.getRussianTranslationsList()
            LearningMode.RU_TO_EN -> listOf(currentWord.englishWord)
            else -> emptyList()
        }
    }
    
    fun toggleFavorite() {
        val state = _uiState.value
        val currentWord = state.currentWord ?: return
        
        viewModelScope.launch {
            repository.toggleFavorite(currentWord)
            // Обновляем слово в списке
            val updatedWords = state.words.toMutableList()
            updatedWords[state.currentIndex] = currentWord.copy(isFavorite = !currentWord.isFavorite)
            _uiState.value = state.copy(words = updatedWords)
        }
    }
    
    fun finishSessionEarly() {
        _uiState.value = _uiState.value.copy(sessionComplete = true)
    }
}

class LearningViewModelFactory(
    private val repository: WordRepository,
    private val spacePreferences: SpacePreferences,
    private val config: SessionConfig
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearningViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LearningViewModel(repository, spacePreferences, config) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
