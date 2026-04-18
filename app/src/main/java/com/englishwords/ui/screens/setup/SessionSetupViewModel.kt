package com.englishwords.ui.screens.setup

import androidx.lifecycle.ViewModel
import com.englishwords.domain.model.AnswerType
import com.englishwords.domain.model.LearningMode
import com.englishwords.domain.model.SessionConfig
import com.englishwords.domain.model.SessionMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionSetupViewModel : ViewModel() {
    
    private val _config = MutableStateFlow(SessionConfig())
    val config: StateFlow<SessionConfig> = _config.asStateFlow()
    
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
}
