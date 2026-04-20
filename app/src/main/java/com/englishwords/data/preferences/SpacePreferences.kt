package com.englishwords.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.spaceDataStore: DataStore<Preferences> by preferencesDataStore(name = "space_preferences")

class SpacePreferences(private val context: Context) {
    private val CURRENT_SPACE_ID = longPreferencesKey("current_space_id")
    
    val currentSpaceId: Flow<Long> = context.spaceDataStore.data
        .map { preferences ->
            preferences[CURRENT_SPACE_ID] ?: 1L // По умолчанию первое пространство
        }
    
    suspend fun setCurrentSpaceId(spaceId: Long) {
        context.spaceDataStore.edit { preferences ->
            preferences[CURRENT_SPACE_ID] = spaceId
        }
    }
}
