package com.englishwords.ui.screens.addword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.englishwords.data.local.Word
import com.englishwords.data.preferences.SpacePreferences
import com.englishwords.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AddWordUiState(
    val englishWord: String = "",
    val russianTranslations: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AddWordViewModel(
    private val repository: WordRepository,
    private val spacePreferences: SpacePreferences
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddWordUiState())
    val uiState: StateFlow<AddWordUiState> = _uiState.asStateFlow()
    
    fun onEnglishWordChange(word: String) {
        _uiState.value = _uiState.value.copy(englishWord = word, errorMessage = null)
    }
    
    fun onRussianTranslationsChange(translations: String) {
        _uiState.value = _uiState.value.copy(russianTranslations = translations, errorMessage = null)
    }
    
    fun saveWord() {
        val state = _uiState.value
        
        if (state.englishWord.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Введите английское слово")
            return
        }
        
        if (state.russianTranslations.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Введите русский перевод")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)
            
            try {
                val spaceId = spacePreferences.currentSpaceId.first()
                val word = Word(
                    spaceId = spaceId,
                    englishWord = state.englishWord.trim(),
                    russianTranslations = state.russianTranslations.trim()
                )
                
                repository.insert(word)
                
                _uiState.value = AddWordUiState(saveSuccess = true)
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isSaving = false,
                    errorMessage = "Ошибка сохранения: ${e.message}"
                )
            }
        }
    }
    
    fun resetState() {
        _uiState.value = AddWordUiState()
    }
}

class AddWordViewModelFactory(
    private val repository: WordRepository,
    private val spacePreferences: SpacePreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddWordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddWordViewModel(repository, spacePreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
