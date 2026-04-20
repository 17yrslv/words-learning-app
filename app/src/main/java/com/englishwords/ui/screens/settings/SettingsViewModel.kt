package com.englishwords.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.englishwords.data.csv.CsvExporter
import com.englishwords.data.csv.CsvImporter
import com.englishwords.data.preferences.LanguagePreferences
import com.englishwords.data.preferences.SpacePreferences
import com.englishwords.data.preferences.ThemePreferences
import com.englishwords.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

class SettingsViewModel(
    private val themePreferences: ThemePreferences,
    private val languagePreferences: LanguagePreferences,
    private val spacePreferences: SpacePreferences,
    private val wordRepository: WordRepository,
    private val context: Context
) : ViewModel() {
    
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()
    
    private val _exportImportState = MutableStateFlow<ExportImportState>(ExportImportState.Idle)
    val exportImportState: StateFlow<ExportImportState> = _exportImportState.asStateFlow()
    
    private val csvExporter = CsvExporter()
    private val csvImporter = CsvImporter(context, wordRepository)
    
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
    
    fun exportWords(outputStream: OutputStream) {
        viewModelScope.launch {
            try {
                _exportImportState.value = ExportImportState.Loading
                val spaceId = spacePreferences.currentSpaceId.first()
                val words = wordRepository.getAllWords(spaceId)
                val count = csvExporter.exportWordsToCsv(words, outputStream)
                outputStream.close()
                _exportImportState.value = ExportImportState.Success("Экспортировано слов: $count")
            } catch (e: Exception) {
                _exportImportState.value = ExportImportState.Error("Ошибка экспорта: ${e.message}")
            } finally {
                try {
                    outputStream.close()
                } catch (e: Exception) {
                    // Игнорируем ошибку закрытия
                }
            }
        }
    }
    
    fun importWords(inputStream: InputStream) {
        viewModelScope.launch {
            try {
                _exportImportState.value = ExportImportState.Loading
                val count = csvImporter.importFromInputStream(inputStream, checkDuplicates = true)
                inputStream.close()
                _exportImportState.value = ExportImportState.Success("Импортировано новых слов: $count")
            } catch (e: Exception) {
                _exportImportState.value = ExportImportState.Error("Ошибка импорта: ${e.message}")
            } finally {
                try {
                    inputStream.close()
                } catch (e: Exception) {
                    // Игнорируем ошибку закрытия
                }
            }
        }
    }
    
    fun deleteAllWords() {
        viewModelScope.launch {
            try {
                _exportImportState.value = ExportImportState.Loading
                wordRepository.deleteAll()
                
                // НЕ сбрасываем флаг первоначального импорта
                // Пользователь может работать с пустой базой и добавлять слова вручную
                
                _exportImportState.value = ExportImportState.Success("Все слова удалены")
            } catch (e: Exception) {
                _exportImportState.value = ExportImportState.Error("Ошибка удаления: ${e.message}")
            }
        }
    }
    
    fun resetState() {
        _exportImportState.value = ExportImportState.Idle
    }
}

sealed class ExportImportState {
    object Idle : ExportImportState()
    object Loading : ExportImportState()
    data class Success(val message: String) : ExportImportState()
    data class Error(val message: String) : ExportImportState()
}

class SettingsViewModelFactory(
    private val themePreferences: ThemePreferences,
    private val languagePreferences: LanguagePreferences,
    private val spacePreferences: SpacePreferences,
    private val wordRepository: WordRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(themePreferences, languagePreferences, spacePreferences, wordRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
