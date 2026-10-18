package com.example.bitacoraelectronica.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bitacoraelectronica.BitacoraApplication
import com.example.bitacoraelectronica.data.repository.IComponenteRepository
import com.example.bitacoraelectronica.data.repository.IReparacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetalleReparacionViewModel(
    private val reparacionId: Long,
    private val reparacionRepository: IReparacionRepository,
    private val componenteRepository: IComponenteRepository
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    /**
     * Combina reactivamente la reparación con sus piezas asignadas y el inventario general disponible.
     */
    val uiState: StateFlow<DetalleReparacionUiState> = combine(
        reparacionRepository.getReparacionWithComponentesStream(reparacionId),
        componenteRepository.getAllComponentesStream(),
        _isSaving,
        _errorMessage
    ) { reparacionRelacional, inventario, saving, error ->
        DetalleReparacionUiState(
            reparacionConComponentes = reparacionRelacional,
            componentesDisponibles = inventario,
            isLoading = reparacionRelacional == null,
            isSaving = saving,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DetalleReparacionUiState(isLoading = true)
    )

    fun actualizarEstado(nuevoEstado: String) {
        val reparacionActual = uiState.value.reparacionConComponentes?.reparacion ?: return
        viewModelScope.launch {
            reparacionRepository.updateReparacion(
                reparacionActual.copy(estado = nuevoEstado)
            )
        }
    }

    fun actualizarFotoEvidencia(nuevaUri: String) {
        val reparacionActual = uiState.value.reparacionConComponentes?.reparacion ?: return
        viewModelScope.launch {
            try {
                // Si en ReparacionEntity tu propiedad se llama fotoEvidenciaUri,
                // cambia "fotoUri = nuevaUri" por "fotoEvidenciaUri = nuevaUri"
                reparacionRepository.updateReparacion(
                    reparacionActual.copy(fotoEvidenciaUri = nuevaUri)
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar la foto: ${e.message}"
            }
        }
    }

    fun guardarDatosTecnicos(
        equipo: String,
        marcaModelo: String,
        fallaReportada: String,
        diagnostico: String,
        medicionesTecnicas: String,
        costoEstimado: Double,
        fotoUri: String? = null
    ) {
        val reparacionActual = uiState.value.reparacionConComponentes?.reparacion ?: return
        viewModelScope.launch {
            _isSaving.value = true
            reparacionRepository.updateReparacion(
                reparacionActual.copy(
                    equipo = equipo,
                    marcaModelo = marcaModelo,
                    fallaReportada = fallaReportada,
                    diagnostico = diagnostico,
                    medicionesTecnicas = medicionesTecnicas,
                    costoEstimado = costoEstimado,
                    fotoEvidenciaUri = fotoUri ?: reparacionActual.fotoEvidenciaUri
                )
            )
            _isSaving.value = false
        }
    }

    fun vincularComponente(componenteId: Long, cantidad: Int = 1) {
        viewModelScope.launch {
            val exito = reparacionRepository.asignarComponente(
                reparacionId = reparacionId,
                componenteId = componenteId,
                cantidad = cantidad
            )
            if (!exito) {
                _errorMessage.value = "Stock insuficiente para vincular este repuesto."
            }
        }
    }

    fun desvincularComponente(componenteId: Long, cantidadARestituir: Int) {
        viewModelScope.launch {
            reparacionRepository.removerComponente(
                reparacionId = reparacionId,
                componenteId = componenteId,
                cantidadARestituir = cantidadARestituir
            )
        }
    }

    fun limpiarMensajeError() {
        _errorMessage.value = null
    }

    companion object {
        fun provideFactory(reparacionId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BitacoraApplication)
                DetalleReparacionViewModel(
                    reparacionId = reparacionId,
                    reparacionRepository = application.container.reparacionRepository,
                    componenteRepository = application.container.componenteRepository
                )
            }
        }
    }
}