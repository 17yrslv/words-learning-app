package com.englishwords.ui.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.englishwords.data.repository.WordRepository
import com.englishwords.domain.model.AnswerType
import com.englishwords.domain.model.LearningMode
import com.englishwords.domain.model.SessionConfig
import com.englishwords.domain.model.SessionMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionSetupViewModel(
    private val wordRepository: WordRepository
) : ViewModel() {
    
    private val _config = MutableStateFlow(SessionConfig())
    val config: StateFlow<SessionConfig> = _config.asStateFlow()
    
    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()
    
    private val _availableWordsCount = MutableStateFlow(0)
    val availableWordsCount: StateFlow<Int> = _availableWordsCount.asStateFlow()
    
    fun setWordCount(count: Int) {
        _config.value = _config.value.copy(wordCount = count)
    }
    
    fun setSessionMode(mode: SessionMode) {
        _config.value = _config.value.copy(sessionMode = mode)
    }
    
    fun setLearningMode(mode: LearningMode) {
        _config.value = _config.value.copy(learningMode = mode)
    }
    
    fun setAnswerType(type: AnswerType) {
        _config.value = _config.value.copy(answerType = type)
    }
    
    fun updateAvailableWordsCount() {
        viewModelScope.launch {
            val count = when (_config.value.sessionMode) {
                SessionMode.NEW_WORDS -> wordRepository.getNewWordsCount()
                SessionMode.REVIEW -> wordRepository.getWordsForReviewCount()
                SessionMode.ALL -> wordRepository.getTotalCount()
            }
            _availableWordsCount.value = count
            validateSession()
        }
    }
    
    fun validateSession(): Boolean {
        viewModelScope.launch {
            val totalWords = wordRepository.getTotalCount()
            val availableWords = _availableWordsCount.value
            val requestedWords = _config.value.wordCount
            
            when {
                totalWords < 5 -> {
                    _validationError.value = "Невозможно начать сессию. В словаре должно быть минимум 5 слов."
                }
                requestedWords > availableWords -> {
                    _validationError.value = "Недостаточно слов в базе данных. Доступно: $availableWords, выбрано: $requestedWords"
                }
                else -> {
                    _validationError.value = null
                }
            }
        }
        return _validationError.value == null
    }
    
    fun clearError() {
        _validationError.value = null
    }
}

class SessionSetupViewModelFactory(
    private val repository: WordRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionSetupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionSetupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
