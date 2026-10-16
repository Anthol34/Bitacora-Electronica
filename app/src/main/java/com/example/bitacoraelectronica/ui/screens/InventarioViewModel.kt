package com.example.bitacoraelectronica.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bitacoraelectronica.BitacoraApplication
import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity
import com.example.bitacoraelectronica.data.repository.IComponenteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class InventarioViewModel(
    private val componenteRepository: IComponenteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filtroCategoria = MutableStateFlow<String?>(null)

    /**
     * Flujo reactivo de componentes condicionado por la barra de búsqueda y los filtros de categoría.
     */
    private val _componentesStream = combine(_searchQuery, _filtroCategoria) { query, categoria ->
        Pair(query, categoria)
    }.flatMapLatest { (query, categoria) ->
        when {
            query.isNotBlank() -> componenteRepository.searchComponentesStream(query.trim())
            categoria != null -> componenteRepository.getComponentesByCategoriaStream(categoria)
            else -> componenteRepository.getAllComponentesStream()
        }
    }

    /**
     * Estado consolidado de la UI expuesto como StateFlow para la composición en Jetpack Compose.
     */
    val uiState: StateFlow<InventarioUiState> = combine(
        _componentesStream,
        _searchQuery,
        _filtroCategoria
    ) { componentes, query, categoria ->
        InventarioUiState(
            listaComponentes = componentes,
            filtroCategoria = categoria,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = InventarioUiState(isLoading = true)
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFiltroCategoriaSelected(categoria: String?) {
        _filtroCategoria.value = if (_filtroCategoria.value == categoria) null else categoria
    }

    fun incrementarStock(componenteId: Long, cantidad: Int = 1) {
        viewModelScope.launch {
            componenteRepository.addStock(componenteId = componenteId, cantidad = cantidad)
        }
    }

    fun decrementarStock(componenteId: Long, cantidad: Int = 1) {
        viewModelScope.launch {
            componenteRepository.deductStock(componenteId = componenteId, cantidad = cantidad)
        }
    }

    fun agregarComponente(componente: ComponenteEntity) {
        viewModelScope.launch {
            componenteRepository.insertComponente(componente)
        }
    }

    fun eliminarComponente(componente: ComponenteEntity) {
        viewModelScope.launch {
            componenteRepository.deleteComponente(componente)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BitacoraApplication)
                InventarioViewModel(
                    componenteRepository = application.container.componenteRepository
                )
            }
        }
    }
}