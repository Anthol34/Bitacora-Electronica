package com.example.bitacoraelectronica.ui.screens

import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity

/**
 * Estado inmutable de la interfaz para la pantalla de Reparaciones (UDF).
 */
data class ReparacionesUiState(
    val listaReparaciones: List<ReparacionEntity> = emptyList(),
    val filtroEstado: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)