package com.englishwords.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_settings")

class LanguagePreferences(private val context: Context) {
    
    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("app_language")
    }
    
    val language: Flow<String> = context.languageDataStore.data
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: "en"
        }
    
    suspend fun setLanguage(language: String) {
        context.languageDataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }
}
