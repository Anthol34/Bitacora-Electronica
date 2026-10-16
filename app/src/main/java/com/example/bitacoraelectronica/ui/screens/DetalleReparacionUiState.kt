package com.example.bitacoraelectronica.ui.screens

import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity
import com.example.bitacoraelectronica.data.local.model.ReparacionWithComponentes

/**
 * Estado inmutable de la interfaz para la pantalla de Detalle de Reparación (UDF).
 */
data class DetalleReparacionUiState(
    val reparacionConComponentes: ReparacionWithComponentes? = null,
    val componentesDisponibles: List<ComponenteEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val operacionExitosa: Boolean = false,
    val errorMessage: String? = null
)