package com.englishwords.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.englishwords.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StatisticsUiState(
    val totalWords: Int = 0,
    val learnedWords: Int = 0,
    val learningWords: Int = 0,
    val reviewWords: Int = 0,
    val accuracy: Int = 0,
    val isLoading: Boolean = true
)

class StatisticsViewModel(
    private val repository: WordRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val total = repository.getTotalCount()
            val learned = repository.getLearnedCount()
            val learning = repository.getLearningCount()
            val review = repository.getReviewCount()
            
            // Вычисляем точность (упрощенно)
            val accuracy = if (total > 0) {
                ((learned + learning) * 100 / total)
            } else {
                0
            }
            
            _uiState.value = StatisticsUiState(
                totalWords = total,
                learnedWords = learned,
                learningWords = learning,
                reviewWords = review,
                accuracy = accuracy,
                isLoading = false
            )
        }
    }
}

class StatisticsViewModelFactory(
    private val repository: WordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
