package com.example.bitacoraelectronica.ui.screens

import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity

/**
 * Estado inmutable de la interfaz para la pantalla de Inventario de Componentes (UDF).
 */
data class InventarioUiState(
    val listaComponentes: List<ComponenteEntity> = emptyList(),
    val filtroCategoria: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)