package com.example.bitacoraelectronica.fake

import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity
import com.example.bitacoraelectronica.data.repository.IComponenteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeComponenteRepository(
    initialComponentes: List<ComponenteEntity> = emptyList()
) : IComponenteRepository {

    private val componentesFlow = MutableStateFlow(initialComponentes)

    override fun getAllComponentesStream(): Flow<List<ComponenteEntity>> {
        return componentesFlow.map { list -> list.sortedBy { it.nombre } }
    }

    override fun getComponenteStream(id: Long): Flow<ComponenteEntity?> {
        return componentesFlow.map { list -> list.find { it.id == id } }
    }

    override suspend fun getComponenteOnce(id: Long): ComponenteEntity? {
        return componentesFlow.value.find { it.id == id }
    }

    override fun getComponentesByCategoriaStream(categoria: String): Flow<List<ComponenteEntity>> {
        return componentesFlow.map { list ->
            list.filter { it.categoria == categoria }.sortedBy { it.nombre }
        }
    }

    override fun searchComponentesStream(query: String): Flow<List<ComponenteEntity>> {
        return componentesFlow.map { list ->
            list.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                        it.especificacionTecnica.contains(query, ignoreCase = true) ||
                        it.categoria.contains(query, ignoreCase = true)
            }.sortedBy { it.nombre }
        }
    }

    override fun getLowStockComponentesStream(minimo: Int): Flow<List<ComponenteEntity>> {
        return componentesFlow.map { list ->
            list.filter { it.cantidadStock <= minimo }.sortedBy { it.cantidadStock }
        }
    }

    override suspend fun insertComponente(componente: ComponenteEntity): Long {
        val generatedId = if (componente.id == 0L) {
            (componentesFlow.value.maxOfOrNull { it.id } ?: 0L) + 1L
        } else {
            componente.id
        }
        val nuevo = componente.copy(id = generatedId)
        componentesFlow.update { current ->
            current.filterNot { it.id == generatedId } + nuevo
        }
        return generatedId
    }

    override suspend fun updateComponente(componente: ComponenteEntity) {
        componentesFlow.update { current ->
            current.map { if (it.id == componente.id) componente else it }
        }
    }

    override suspend fun deleteComponente(componente: ComponenteEntity) {
        componentesFlow.update { current ->
            current.filterNot { it.id == componente.id }
        }
    }

    override suspend fun deductStock(componenteId: Long, cantidad: Int): Boolean {
        val actual = componentesFlow.value.find { it.id == componenteId } ?: return false
        if (actual.cantidadStock < cantidad) return false

        componentesFlow.update { current ->
            current.map {
                if (it.id == componenteId) {
                    it.copy(cantidadStock = it.cantidadStock - cantidad)
                } else {
                    it
                }
            }
        }
        return true
    }

    override suspend fun addStock(componenteId: Long, cantidad: Int) {
        componentesFlow.update { current ->
            current.map {
                if (it.id == componenteId) {
                    it.copy(cantidadStock = it.cantidadStock + cantidad)
                } else {
                    it
                }
            }
        }
    }
}