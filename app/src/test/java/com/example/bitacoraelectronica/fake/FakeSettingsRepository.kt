package com.example.bitacoraelectronica.fake

import com.example.bitacoraelectronica.data.repository.ISettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsRepository(
    initialDarkMode: Boolean = false,
    initialFontSize: String = "Media",
    initialBackupTimestamp: String = "Sin copias previas"
) : ISettingsRepository {

    private val _isDarkMode = MutableStateFlow(initialDarkMode)
    override val isDarkModeStream: Flow<Boolean> = _isDarkMode.asStateFlow()

    private val _fontSize = MutableStateFlow(initialFontSize)
    override val fontSizeStream: Flow<String> = _fontSize.asStateFlow()

    private val _lastBackupTimestamp = MutableStateFlow(initialBackupTimestamp)
    override val lastBackupTimestampStream: Flow<String> = _lastBackupTimestamp.asStateFlow()

    override suspend fun setDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
    }

    override suspend fun setFontSize(size: String) {
        _fontSize.value = size
    }

    override suspend fun updateLastBackupTimestamp(timestamp: String) {
        _lastBackupTimestamp.value = timestamp
    }
}