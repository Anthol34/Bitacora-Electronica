package com.example.bitacoraelectronica.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bitacoraelectronica.BitacoraApplication
import com.example.bitacoraelectronica.data.repository.ISettingsRepository
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

    /**
     * Flujo caliente de estado que consolida todas las preferencias de DataStore.
     */
    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.isDarkModeStream,
        settingsRepository.fontSizeStream,
        settingsRepository.lastBackupTimestampStream
    ) { isDarkMode, fontSize, lastBackup ->
        SettingsUiState(
            isDarkMode = isDarkMode,
            fontSize = fontSize,
            lastBackupTimestamp = lastBackup,
            isLoading = false
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

    fun triggerBackupExport() {
        viewModelScope.launch {
            val currentFormattedDate = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date())
            settingsRepository.updateLastBackupTimestamp(currentFormattedDate)
        }
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