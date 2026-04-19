package com.englishwords.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.englishwords.data.preferences.LanguagePreferences
import com.englishwords.data.preferences.ThemePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val themePreferences: ThemePreferences,
    private val languagePreferences: LanguagePreferences
) : ViewModel() {
    
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()
    
    init {
        viewModelScope.launch {
            themePreferences.isDarkTheme.collect { isDark ->
                _isDarkTheme.value = isDark
            }
        }
        
        viewModelScope.launch {
            languagePreferences.language.collect { lang ->
                _currentLanguage.value = lang
            }
        }
    }
    
    suspend fun toggleTheme() {
        themePreferences.setDarkTheme(!_isDarkTheme.value)
    }
    
    suspend fun setLanguage(language: String) {
        languagePreferences.setLanguage(language)
    }
}

class SettingsViewModelFactory(
    private val themePreferences: ThemePreferences,
    private val languagePreferences: LanguagePreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(themePreferences, languagePreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
