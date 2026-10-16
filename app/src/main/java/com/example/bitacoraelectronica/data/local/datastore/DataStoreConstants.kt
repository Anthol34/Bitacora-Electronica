package com.example.bitacoraelectronica.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

/**
 * Nombre del archivo de almacenamiento para las preferencias locales del técnico.
 */
private const val SETTINGS_PREFERENCES_NAME = "bitacora_user_settings"

/**
 * Delegado singleton para instanciar DataStore de preferencias.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_PREFERENCES_NAME
)

/**
 * Claves y valores por defecto para la configuración de la bitácora técnica.
 */
object PreferencesKeys {
    val KEY_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    val KEY_FONT_SIZE = stringPreferencesKey("font_size_scale")
    val KEY_LAST_BACKUP_TIMESTAMP = stringPreferencesKey("last_backup_timestamp")

    // Valores por defecto
    const val DEFAULT_DARK_MODE = false
    const val DEFAULT_FONT_SIZE = "Media"
    const val FONT_SIZE_SMALL = "Pequeña"
    const val FONT_SIZE_MEDIUM = "Media"
    const val FONT_SIZE_LARGE = "Grande"
}