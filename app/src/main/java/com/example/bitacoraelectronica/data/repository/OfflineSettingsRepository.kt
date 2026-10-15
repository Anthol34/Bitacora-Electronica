package com.example.bitacoraelectronica.data.repository

import com.example.bitacoraelectronica.data.local.datastore.SettingsPreferencesDataSource
import kotlinx.coroutines.flow.Flow

/**
 * Implementación concreta del repositorio de configuraciones.
 * Encapsula la fuente de datos Preferences DataStore para el sistema de ajustes locales.
 */
class OfflineSettingsRepository(
    private val settingsPreferencesDataSource: SettingsPreferencesDataSource
) : ISettingsRepository {

    override val isDarkModeStream: Flow<Boolean> = settingsPreferencesDataSource.isDarkMode

    override val fontSizeStream: Flow<String> = settingsPreferencesDataSource.fontSize

    override val lastBackupTimestampStream: Flow<String> = settingsPreferencesDataSource.lastBackupTimestamp

    override suspend fun setDarkMode(isDark: Boolean) {
        settingsPreferencesDataSource.setDarkMode(isDark)
    }

    override suspend fun setFontSize(size: String) {
        settingsPreferencesDataSource.setFontSize(size)
    }

    override suspend fun updateLastBackupTimestamp(timestamp: String) {
        settingsPreferencesDataSource.updateLastBackupTimestamp(timestamp)
    }
}