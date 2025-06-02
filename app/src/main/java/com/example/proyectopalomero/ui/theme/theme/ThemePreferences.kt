package com.example.proyectopalomero.ui.theme.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

object ThemePreference {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    fun getThemeMode(context: Context): Flow<Boolean> {
        return context.dataStore.data
            .map { preferences -> preferences[DARK_MODE_KEY] == true }
    }

    suspend fun saveThemeMode(context: Context, isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }
}