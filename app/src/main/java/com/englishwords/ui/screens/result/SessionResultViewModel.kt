package com.englishwords.ui.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.englishwords.data.local.Word
import com.englishwords.data.repository.WordRepository
import com.englishwords.domain.model.SessionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionResultViewModel(
    private val repository: WordRepository
) : ViewModel() {
    
    private val _result = MutableStateFlow<SessionResult?>(null)
    val result: StateFlow<SessionResult?> = _result.asStateFlow()
    
    private val _favoriteStates = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val favoriteStates: StateFlow<Map<Long, Boolean>> = _favoriteStates.asStateFlow()
    
    fun setResult(result: SessionResult) {
        _result.value = result
    }
    
    fun loadFavoriteStates(result: SessionResult) {
        val states = mutableMapOf<Long, Boolean>()
        result.correctWords.forEach { word ->
            states[word.id] = word.isFavorite
        }
        result.wordsWithErrors.forEach { wordError ->
            states[wordError.word.id] = wordError.word.isFavorite
        }
        _favoriteStates.value = states
    }
    
    fun toggleFavorite(word: Word) {
        viewModelScope.launch {
            val currentStates = _favoriteStates.value.toMutableMap()
            val currentFavoriteState = currentStates[word.id] ?: word.isFavorite
            
            repository.toggleFavorite(word)
            currentStates[word.id] = !currentFavoriteState
            _favoriteStates.value = currentStates
        }
    }
}

class SessionResultViewModelFactory(
    private val repository: WordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionResultViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionResultViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
