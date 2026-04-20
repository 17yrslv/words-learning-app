package com.englishwords.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.englishwords.data.local.Space
import com.englishwords.data.preferences.SpacePreferences
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
    val isLoading: Boolean = true,
    val spaces: List<Space> = emptyList(),
    val currentSpace: Space? = null,
    val showSpaceDialog: Boolean = false,
    val showCreateSpaceDialog: Boolean = false,
    val showDeleteSpaceDialog: Boolean = false,
    val spaceToDelete: Space? = null
)

class HomeViewModel(
    private val repository: WordRepository,
    private val spacePreferences: SpacePreferences
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadSpaces()
        observeCurrentSpace()
    }
    
    private fun loadSpaces() {
        viewModelScope.launch {
            repository.getAllSpaces().collect { spaces ->
                _uiState.value = _uiState.value.copy(spaces = spaces)
                
                // Обновляем текущее пространство, если оно изменилось
                val currentSpaceId = _uiState.value.currentSpace?.id
                if (currentSpaceId != null) {
                    val updatedCurrentSpace = spaces.find { it.id == currentSpaceId }
                    if (updatedCurrentSpace != null) {
                        _uiState.value = _uiState.value.copy(currentSpace = updatedCurrentSpace)
                    }
                }
            }
        }
    }
    
    private fun observeCurrentSpace() {
        viewModelScope.launch {
            spacePreferences.currentSpaceId.collect { currentSpaceId ->
                val currentSpace = _uiState.value.spaces.find { it.id == currentSpaceId }
                    ?: repository.getSpaceById(currentSpaceId)
                
                _uiState.value = _uiState.value.copy(currentSpace = currentSpace)
                loadStatistics()
            }
        }
    }
    
    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val currentSpaceId = _uiState.value.currentSpace?.id ?: 1L
            
            val total = repository.getTotalCount(currentSpaceId)
            val learned = repository.getLearnedCount(currentSpaceId)
            val learning = repository.getLearningCount(currentSpaceId)
            val review = repository.getReviewCount(currentSpaceId)
            
            _uiState.value = _uiState.value.copy(
                totalWords = total,
                learnedWords = learned,
                learningWords = learning,
                reviewWords = review,
                isLoading = false
            )
        }
    }
    
    fun showSpaceDialog() {
        _uiState.value = _uiState.value.copy(showSpaceDialog = true)
    }
    
    fun hideSpaceDialog() {
        _uiState.value = _uiState.value.copy(showSpaceDialog = false)
    }
    
    fun showCreateSpaceDialog() {
        _uiState.value = _uiState.value.copy(showCreateSpaceDialog = true, showSpaceDialog = false)
    }
    
    fun hideCreateSpaceDialog() {
        _uiState.value = _uiState.value.copy(showCreateSpaceDialog = false)
    }
    
    fun selectSpace(space: Space) {
        viewModelScope.launch {
            spacePreferences.setCurrentSpaceId(space.id)
            _uiState.value = _uiState.value.copy(
                currentSpace = space,
                showSpaceDialog = false
            )
            loadStatistics()
        }
    }
    
    fun showDeleteSpaceDialog(space: Space) {
        _uiState.value = _uiState.value.copy(
            showDeleteSpaceDialog = true,
            spaceToDelete = space,
            showSpaceDialog = false
        )
    }
    
    fun hideDeleteSpaceDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteSpaceDialog = false,
            spaceToDelete = null
        )
    }
    
    fun deleteSpace() {
        viewModelScope.launch {
            val spaceToDelete = _uiState.value.spaceToDelete ?: return@launch
            
            // Нельзя удалить пространство по умолчанию
            if (spaceToDelete.id == 1L) {
                return@launch
            }
            
            // Если удаляем текущее пространство, переключаемся на пространство по умолчанию
            if (spaceToDelete.id == _uiState.value.currentSpace?.id) {
                val defaultSpace = repository.getSpaceById(1L)
                if (defaultSpace != null) {
                    spacePreferences.setCurrentSpaceId(1L)
                }
            }
            
            // Удаляем пространство
            repository.deleteSpace(spaceToDelete.id)
            
            hideDeleteSpaceDialog()
        }
    }
    
    fun createSpace(name: String, shortName: String) {
        viewModelScope.launch {
            val spaceId = repository.createSpace(name, shortName)
            hideCreateSpaceDialog()
            
            // Автоматически переключаемся на новое пространство
            val newSpace = repository.getSpaceById(spaceId)
            if (newSpace != null) {
                selectSpace(newSpace)
            }
        }
    }
}

class HomeViewModelFactory(
    private val repository: WordRepository,
    private val spacePreferences: SpacePreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, spacePreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
