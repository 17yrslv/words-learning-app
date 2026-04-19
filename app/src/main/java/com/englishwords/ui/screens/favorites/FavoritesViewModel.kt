package com.englishwords.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.englishwords.data.local.Word
import com.englishwords.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: WordRepository
) : ViewModel() {
    
    private val _favoriteWords = MutableStateFlow<List<Word>>(emptyList())
    val favoriteWords: StateFlow<List<Word>> = _favoriteWords.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun loadFavoriteWords() {
        viewModelScope.launch {
            _isLoading.value = true
            val count = repository.getFavoriteWordsCount()
            val words = repository.getFavoriteWords(count)
            _favoriteWords.value = words
            _isLoading.value = false
        }
    }
    
    fun removeFromFavorites(word: Word) {
        viewModelScope.launch {
            repository.toggleFavorite(word)
            loadFavoriteWords()
        }
    }
}

class FavoritesViewModelFactory(
    private val repository: WordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
