package com.example.bitacoraelectronica.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bitacoraelectronica.BitacoraApplication
import com.example.bitacoraelectronica.data.repository.ISettingsRepository
import com.example.bitacoraelectronica.utils.DatabaseBackupHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsViewModel(
    private val settingsRepository: ISettingsRepository
) : ViewModel() {

    private val _isExportingBackup = MutableStateFlow(false)
    private val _backupMessage = MutableStateFlow<String?>(null)

    /**
     * Flujo caliente de estado que consolida todas las preferencias de DataStore y el estado del respaldo.
     */
    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.isDarkModeStream,
        settingsRepository.fontSizeStream,
        settingsRepository.lastBackupTimestampStream,
        _isExportingBackup,
        _backupMessage
    ) { isDarkMode, fontSize, lastBackup, isExporting, message ->
        SettingsUiState(
            isDarkMode = isDarkMode,
            fontSize = fontSize,
            lastBackupTimestamp = lastBackup,
            isLoading = false,
            isExportingBackup = isExporting,
            backupMessage = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = SettingsUiState(isLoading = true)
    )

    fun onDarkModeChanged(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(enabled)
        }
    }

    fun onFontSizeChanged(size: String) {
        viewModelScope.launch {
            settingsRepository.setFontSize(size)
        }
    }

    /**
     * Registra la fecha actual del respaldo en DataStore (respaldo rápido de marca de tiempo).
     */
    fun triggerBackupExport() {
        viewModelScope.launch {
            val currentFormattedDate = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date())
            settingsRepository.updateLastBackupTimestamp(currentFormattedDate)
            _backupMessage.value = "Marca de respaldo actualizada ($currentFormattedDate)"
        }
    }

    /**
     * Exporta físicamente el archivo SQLite (.db) al almacenamiento aislado de la app
     * y actualiza la fecha del último respaldo en DataStore.
     */
    fun exportDatabaseToLocalFile(context: Context) {
        viewModelScope.launch {
            _isExportingBackup.value = true
            val result = DatabaseBackupHelper.exportDatabaseFile(context)
            if (result.success && result.formattedTimestamp.isNotBlank()) {
                settingsRepository.updateLastBackupTimestamp(result.formattedTimestamp)
            }
            _backupMessage.value = result.message
            _isExportingBackup.value = false
        }
    }

    fun clearBackupMessage() {
        _backupMessage.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BitacoraApplication)
                SettingsViewModel(
                    settingsRepository = application.container.settingsRepository
                )
            }
        }
    }
}