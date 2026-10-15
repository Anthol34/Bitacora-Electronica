package com.example.bitacoraelectronica.data.repository

import kotlinx.coroutines.flow.Flow

/**
 * Contrato de abstracción del repositorio para las preferencias y configuración técnica del usuario.
 * Proporciona flujos continuos de lectura y funciones de escritura asíncronas.
 */
interface ISettingsRepository {

    /**
     * Flujo reactivo que emite el estado actual del modo oscuro.
     */
    val isDarkModeStream: Flow<Boolean>

    /**
     * Flujo reactivo que emite el tamaño de escala tipográfica seleccionado para la bitácora.
     */
    val fontSizeStream: Flow<String>

    /**
     * Flujo reactivo que emite la marca de tiempo de la última exportación de la base de datos local.
     */
    val lastBackupTimestampStream: Flow<String>

    /**
     * Actualiza la preferencia del tema visual oscuro o claro.
     */
    suspend fun setDarkMode(isDark: Boolean)

    /**
     * Actualiza la escala de tamaño de letra utilizada en las mediciones técnicas.
     */
    suspend fun setFontSize(size: String)

    /**
     * Registra la marca de tiempo tras una exportación de respaldo SQLite exitosa.
     */
    suspend fun updateLastBackupTimestamp(timestamp: String)
}