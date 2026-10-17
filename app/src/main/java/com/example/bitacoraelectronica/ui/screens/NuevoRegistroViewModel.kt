package com.example.bitacoraelectronica.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bitacoraelectronica.BitacoraApplication
import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity
import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity
import com.example.bitacoraelectronica.data.repository.IComponenteRepository
import com.example.bitacoraelectronica.data.repository.IReparacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NuevoRegistroUiState(
    val equipo: String = "",
    val marcaModelo: String = "",
    val fallaReportada: String = "",
    val diagnostico: String = "",
    val medicionesTecnicas: String = "",
    val estadoSeleccionado: String = ReparacionEntity.ESTADO_PENDIENTE,
    val costoEstimado: String = "",
    val componenteInicialId: Long? = null,
    val cantidadComponenteInicial: Int = 1,
    val isSaving: Boolean = false,
    val registroExitoso: Boolean = false,
    val errorMessage: String? = null
)

class NuevoRegistroViewModel(
    private val reparacionRepository: IReparacionRepository,
    componenteRepository: IComponenteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NuevoRegistroUiState())
    val uiState: StateFlow<NuevoRegistroUiState> = _uiState.asStateFlow()

    val componentesDisponibles: StateFlow<List<ComponenteEntity>> =
        componenteRepository.getAllComponentesStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = emptyList()
            )

    fun onEquipoChanged(value: String) {
        _uiState.update { it.copy(equipo = value, errorMessage = null) }
    }

    fun onMarcaModeloChanged(value: String) {
        _uiState.update { it.copy(marcaModelo = value, errorMessage = null) }
    }

    fun onFallaReportadaChanged(value: String) {
        _uiState.update { it.copy(fallaReportada = value, errorMessage = null) }
    }

    fun onDiagnosticoChanged(value: String) {
        _uiState.update { it.copy(diagnostico = value) }
    }

    fun onMedicionesChanged(value: String) {
        _uiState.update { it.copy(medicionesTecnicas = value) }
    }

    fun onEstadoSelected(estado: String) {
        _uiState.update { it.copy(estadoSeleccionado = estado) }
    }

    fun onCostoChanged(value: String) {
        _uiState.update { it.copy(costoEstimado = value) }
    }

    fun onComponenteInicialSelected(componenteId: Long?) {
        _uiState.update { it.copy(componenteInicialId = componenteId) }
    }

    fun guardarNuevoRegistro() {
        val state = _uiState.value
        if (state.equipo.isBlank() || state.marcaModelo.isBlank() || state.fallaReportada.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Complete los campos obligatorios: Equipo, Marca/Modelo y Falla reportada.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val costo = state.costoEstimado.toDoubleOrNull() ?: 0.0
                val nuevaReparacion = ReparacionEntity(
                    equipo = state.equipo.trim(),
                    marcaModelo = state.marcaModelo.trim(),
                    fallaReportada = state.fallaReportada.trim(),
                    diagnostico = state.diagnostico.trim(),
                    medicionesTecnicas = state.medicionesTecnicas.trim(),
                    estado = state.estadoSeleccionado,
                    costoEstimado = costo
                )

                val nuevoId = reparacionRepository.insertReparacion(nuevaReparacion)

                // Si se seleccionó un repuesto inicial, lo vinculamos y descontamos stock
                state.componenteInicialId?.let { compId ->
                    reparacionRepository.asignarComponente(
                        reparacionId = nuevoId,
                        componenteId = compId,
                        cantidad = state.cantidadComponenteInicial
                    )
                }

                _uiState.update { it.copy(isSaving = false, registroExitoso = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Error al guardar la ficha técnica: ${e.message}"
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BitacoraApplication)
                NuevoRegistroViewModel(
                    reparacionRepository = application.container.reparacionRepository,
                    componenteRepository = application.container.componenteRepository
                )
            }
        }
    }
}