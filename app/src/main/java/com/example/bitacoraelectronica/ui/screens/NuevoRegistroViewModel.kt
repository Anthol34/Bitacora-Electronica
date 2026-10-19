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
import kotlinx.coroutines.flow.combine
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
    val fotoEvidenciaUri: String? = null,
    val componenteInicialId: Long? = null,
    val cantidadComponenteInicial: Int = 1,
    val componentesDisponibles: List<ComponenteEntity> = emptyList(),
    val isSaving: Boolean = false,
    val registroExitoso: Boolean = false,
    val errorMessage: String? = null
)

class NuevoRegistroViewModel(
    private val reparacionRepository: IReparacionRepository,
    componenteRepository: IComponenteRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(NuevoRegistroUiState())

    val uiState: StateFlow<NuevoRegistroUiState> = combine(
        _formState,
        componenteRepository.getAllComponentesStream()
    ) { form, componentes ->
        form.copy(componentesDisponibles = componentes)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = NuevoRegistroUiState()
    )

    fun onEquipoChanged(value: String) {
        _formState.update { it.copy(equipo = value, errorMessage = null) }
    }

    fun onMarcaModeloChanged(value: String) {
        _formState.update { it.copy(marcaModelo = value, errorMessage = null) }
    }

    fun onFallaReportadaChanged(value: String) {
        _formState.update { it.copy(fallaReportada = value, errorMessage = null) }
    }

    fun onDiagnosticoChanged(value: String) {
        _formState.update { it.copy(diagnostico = value) }
    }

    fun onMedicionesChanged(value: String) {
        _formState.update { it.copy(medicionesTecnicas = value) }
    }

    fun onEstadoSelected(estado: String) {
        _formState.update { it.copy(estadoSeleccionado = estado) }
    }

    fun onCostoChanged(value: String) {
        _formState.update { it.copy(costoEstimado = value) }
    }

    fun onComponenteInicialSelected(componenteId: Long?) {
        _formState.update { it.copy(componenteInicialId = componenteId) }
    }

    fun onFotoCapturada(nuevaUri: String) {
        _formState.update { current ->
            val urisExistentes = current.fotoEvidenciaUri
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() } ?: emptyList()

            val listaActualizada = if (nuevaUri in urisExistentes) {
                urisExistentes
            } else {
                urisExistentes + nuevaUri
            }

            val nuevaCadena = listaActualizada.joinToString(",")
            current.copy(fotoEvidenciaUri = nuevaCadena)
        }
    }

    fun onFotoEliminada(uriAEliminar: String) {
        _formState.update { current ->
            val urisExistentes = current.fotoEvidenciaUri
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() } ?: emptyList()

            val listaFiltrada = urisExistentes.filter { it != uriAEliminar }
            val nuevaCadena = if (listaFiltrada.isEmpty()) null else listaFiltrada.joinToString(",")
            current.copy(fotoEvidenciaUri = nuevaCadena)
        }
    }

    fun guardarNuevoRegistro() {
        val state = _formState.value
        if (state.equipo.isBlank() || state.marcaModelo.isBlank() || state.fallaReportada.isBlank()) {
            _formState.update {
                it.copy(errorMessage = "Complete los campos obligatorios: Equipo, Marca/Modelo y Falla reportada.")
            }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val costo = state.costoEstimado.toDoubleOrNull() ?: 0.0
                val nuevaReparacion = ReparacionEntity(
                    equipo = state.equipo.trim(),
                    marcaModelo = state.marcaModelo.trim(),
                    fallaReportada = state.fallaReportada.trim(),
                    diagnostico = state.diagnostico.trim(),
                    medicionesTecnicas = state.medicionesTecnicas.trim(),
                    estado = state.estadoSeleccionado,
                    costoEstimado = costo,
                    fotoEvidenciaUri = state.fotoEvidenciaUri
                )

                val nuevoId = reparacionRepository.insertReparacion(nuevaReparacion)

                state.componenteInicialId?.let { compId ->
                    reparacionRepository.asignarComponente(
                        reparacionId = nuevoId,
                        componenteId = compId,
                        cantidad = state.cantidadComponenteInicial
                    )
                }

                _formState.update { it.copy(isSaving = false, registroExitoso = true) }
            } catch (e: Exception) {
                _formState.update {
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