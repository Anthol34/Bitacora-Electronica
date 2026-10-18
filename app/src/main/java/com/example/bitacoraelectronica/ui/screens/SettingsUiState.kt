package com.example.bitacoraelectronica.ui.screens

/**
 * Estado inmutable de la interfaz para la pantalla de Configuración técnica (UDF).
 */
data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val fontSize: String = "Media",
    val lastBackupTimestamp: String = "Sin copias previas",
    val isLoading: Boolean = false,
    val isExportingBackup: Boolean = false,
    val backupMessage: String? = null
)