package com.example.bitacoraelectronica.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bitacoraelectronica.BitacoraApplication
import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity
import com.example.bitacoraelectronica.data.repository.IReparacionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ReparacionesViewModel(
    private val reparacionRepository: IReparacionRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filtroEstado = MutableStateFlow<String?>(null)

    /**
     * Flujo reactivo de reparaciones basado en búsquedas predictivas y filtros de estado operativo.
     */
    private val _reparacionesStream = combine(_searchQuery, _filtroEstado) { query, estado ->
        Pair(query, estado)
    }.flatMapLatest { (query, estado) ->
        when {
            query.isNotBlank() -> reparacionRepository.searchReparacionesStream(query.trim())
            estado != null -> reparacionRepository.getReparacionesByEstadoStream(estado)
            else -> reparacionRepository.getAllReparacionesStream()
        }
    }

    /**
     * Estado consolidado de la interfaz expuesto como StateFlow caliente a la capa UI.
     */
    val uiState: StateFlow<ReparacionesUiState> = combine(
        _reparacionesStream,
        _searchQuery,
        _filtroEstado
    ) { reparaciones, query, estado ->
        ReparacionesUiState(
            listaReparaciones = reparaciones,
            filtroEstado = estado,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ReparacionesUiState(isLoading = true)
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFiltroEstadoSelected(estado: String?) {
        _filtroEstado.value = if (_filtroEstado.value == estado) null else estado
    }

    fun deleteReparacion(reparacion: ReparacionEntity) {
        viewModelScope.launch {
            reparacionRepository.deleteReparacion(reparacion)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BitacoraApplication)
                ReparacionesViewModel(
                    reparacionRepository = application.container.reparacionRepository
                )
            }
        }
    }
}