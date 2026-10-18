package com.example.bitacoraelectronica.fake

import com.example.bitacoraelectronica.data.local.entity.ReparacionComponenteEntity
import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity
import com.example.bitacoraelectronica.data.local.model.ReparacionWithComponentes
import com.example.bitacoraelectronica.data.repository.IReparacionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeReparacionRepository(
    private val fakeComponenteRepository: FakeComponenteRepository = FakeComponenteRepository(),
    initialReparaciones: List<ReparacionEntity> = emptyList()
) : IReparacionRepository {

    private val reparacionesFlow = MutableStateFlow(initialReparaciones)
    private val asignacionesFlow = MutableStateFlow<List<ReparacionComponenteEntity>>(emptyList())

    override fun getAllReparacionesStream(): Flow<List<ReparacionEntity>> {
        return reparacionesFlow.map { list -> list.sortedByDescending { it.fechaIngreso } }
    }

    override fun getReparacionStream(id: Long): Flow<ReparacionEntity?> {
        return reparacionesFlow.map { list -> list.find { it.id == id } }
    }

    override suspend fun getReparacionOnce(id: Long): ReparacionEntity? {
        return reparacionesFlow.value.find { it.id == id }
    }

    override fun getReparacionesByEstadoStream(estado: String): Flow<List<ReparacionEntity>> {
        return reparacionesFlow.map { list ->
            list.filter { it.estado == estado }.sortedByDescending { it.fechaIngreso }
        }
    }

    override fun searchReparacionesStream(query: String): Flow<List<ReparacionEntity>> {
        return reparacionesFlow.map { list ->
            list.filter {
                it.equipo.contains(query, ignoreCase = true) ||
                        it.marcaModelo.contains(query, ignoreCase = true) ||
                        it.diagnostico.contains(query, ignoreCase = true)
            }.sortedByDescending { it.fechaIngreso }
        }
    }

    override fun getReparacionWithComponentesStream(id: Long): Flow<ReparacionWithComponentes?> {
        return combine(
            reparacionesFlow,
            asignacionesFlow,
            fakeComponenteRepository.getAllComponentesStream()
        ) { reparaciones, asignaciones, todosComponentes ->
            val reparacion = reparaciones.find { it.id == id } ?: return@combine null
            val idsAsociados = asignaciones
                .filter { it.reparacionId == id }
                .map { it.componenteId }
                .toSet()

            val componentesAsignados = todosComponentes.filter { it.id in idsAsociados }
            ReparacionWithComponentes(
                reparacion = reparacion,
                componentes = componentesAsignados
            )
        }
    }

    override suspend fun insertReparacion(reparacion: ReparacionEntity): Long {
        val generatedId = if (reparacion.id == 0L) {
            (reparacionesFlow.value.maxOfOrNull { it.id } ?: 0L) + 1L
        } else {
            reparacion.id
        }
        val nueva = reparacion.copy(id = generatedId)
        reparacionesFlow.update { current ->
            current.filterNot { it.id == generatedId } + nueva
        }
        return generatedId
    }

    override suspend fun updateReparacion(reparacion: ReparacionEntity) {
        reparacionesFlow.update { current ->
            current.map { if (it.id == reparacion.id) reparacion else it }
        }
    }

    override suspend fun deleteReparacion(reparacion: ReparacionEntity) {
        deleteReparacionById(reparacion.id)
    }

    override suspend fun deleteReparacionById(id: Long) {
        reparacionesFlow.update { current -> current.filterNot { it.id == id } }
        asignacionesFlow.update { current -> current.filterNot { it.reparacionId == id } }
    }

    override suspend fun asignarComponente(
        reparacionId: Long,
        componenteId: Long,
        cantidad: Int
    ): Boolean {
        val stockDeducted = fakeComponenteRepository.deductStock(componenteId, cantidad)
        if (!stockDeducted) return false

        val nuevaAsignacion = ReparacionComponenteEntity(
            id = (asignacionesFlow.value.maxOfOrNull { it.id } ?: 0L) + 1L,
            reparacionId = reparacionId,
            componenteId = componenteId,
            cantidadUtilizada = cantidad
        )
        asignacionesFlow.update { current -> current + nuevaAsignacion }
        return true
    }

    override suspend fun removerComponente(
        reparacionId: Long,
        componenteId: Long,
        cantidadARestituir: Int
    ) {
        asignacionesFlow.update { current ->
            current.filterNot {
                it.reparacionId == reparacionId && it.componenteId == componenteId
            }
        }
        fakeComponenteRepository.addStock(componenteId, cantidadARestituir)
    }
}