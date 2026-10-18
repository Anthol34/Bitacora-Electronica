package com.example.bitacoraelectronica.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Data Source local encargado de gestionar las lecturas y modificaciones
 * reactivas en DataStore Preferences para las opciones del taller técnico.
 */
class SettingsPreferencesDataSource(
    private val dataStore: DataStore<Preferences>
) {
    constructor(context: Context) : this(context.dataStore)

    /**
     * Flujo reactivo que emite el estado del Modo Oscuro.
     */
    val isDarkMode: Flow<Boolean> = dataStore.data
        .catch {
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[PreferencesKeys.KEY_DARK_MODE] ?: PreferencesKeys.DEFAULT_DARK_MODE
        }

    /**
     * Flujo reactivo que emite la preferencia de escala de tipografía técnica ("Pequeña", "Media", "Grande").
     */
    val fontSize: Flow<String> = dataStore.data
        .catch {
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[PreferencesKeys.KEY_FONT_SIZE] ?: PreferencesKeys.DEFAULT_FONT_SIZE
        }

    /**
     * Flujo reactivo que emite la marca de tiempo del último respaldo exportado.
     */
    val lastBackupTimestamp: Flow<String> = dataStore.data
        .catch {
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[PreferencesKeys.KEY_LAST_BACKUP_TIMESTAMP] ?: "Sin copias previas"
        }

    /**
     * Actualiza de forma asíncrona la preferencia de modo oscuro.
     */
    suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_DARK_MODE] = isDark
        }
    }

    /**
     * Actualiza la escala de tamaño de fuente de la bitácora.
     */
    suspend fun setFontSize(size: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_FONT_SIZE] = size
        }
    }

    /**
     * Registra la fecha y hora de la última exportación de la base de datos local SQLite.
     */
    suspend fun updateLastBackupTimestamp(timestamp: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_LAST_BACKUP_TIMESTAMP] = timestamp
        }
    }
}