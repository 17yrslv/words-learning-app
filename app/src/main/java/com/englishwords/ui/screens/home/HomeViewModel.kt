package com.englishwords.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.englishwords.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val totalWords: Int = 0,
    val learnedWords: Int = 0,
    val learningWords: Int = 0,
    val reviewWords: Int = 0,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val repository: WordRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val total = repository.getTotalCount()
            val learned = repository.getLearnedCount()
            val learning = repository.getLearningCount()
            val review = repository.getReviewCount()
            
            _uiState.value = HomeUiState(
                totalWords = total,
                learnedWords = learned,
                learningWords = learning,
                reviewWords = review,
                isLoading = false
            )
        }
    }
}

class HomeViewModelFactory(
    private val repository: WordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
