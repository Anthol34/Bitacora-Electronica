package com.example.bitacoraelectronica.data.repository

import com.example.bitacoraelectronica.data.local.dao.ComponenteDao
import com.example.bitacoraelectronica.data.local.dao.ReparacionComponenteDao
import com.example.bitacoraelectronica.data.local.dao.ReparacionDao
import com.example.bitacoraelectronica.data.local.entity.ReparacionComponenteEntity
import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity
import com.example.bitacoraelectronica.data.local.model.ReparacionWithComponentes
import kotlinx.coroutines.flow.Flow

/**
 * Implementación concreta del repositorio de reparaciones con persistencia local en Room.
 * Administra la sincronización atómica entre el diagnóstico de equipos y el consumo de piezas.
 */
class OfflineReparacionRepository(
    private val reparacionDao: ReparacionDao,
    private val componenteDao: ComponenteDao,
    private val reparacionComponenteDao: ReparacionComponenteDao
) : IReparacionRepository {

    override fun getAllReparacionesStream(): Flow<List<ReparacionEntity>> {
        return reparacionDao.getAllReparaciones()
    }

    override fun getReparacionStream(id: Long): Flow<ReparacionEntity?> {
        return reparacionDao.getReparacionById(id)
    }

    override suspend fun getReparacionOnce(id: Long): ReparacionEntity? {
        return reparacionDao.getReparacionByIdOnce(id)
    }

    override fun getReparacionesByEstadoStream(estado: String): Flow<List<ReparacionEntity>> {
        return reparacionDao.getReparacionesByEstado(estado)
    }

    override fun searchReparacionesStream(query: String): Flow<List<ReparacionEntity>> {
        return reparacionDao.searchReparaciones(query)
    }

    override fun getReparacionWithComponentesStream(id: Long): Flow<ReparacionWithComponentes?> {
        return reparacionDao.getReparacionWithComponentes(id)
    }

    override suspend fun insertReparacion(reparacion: ReparacionEntity): Long {
        return reparacionDao.insertReparacion(reparacion)
    }

    override suspend fun updateReparacion(reparacion: ReparacionEntity) {
        reparacionDao.updateReparacion(reparacion)
    }

    override suspend fun deleteReparacion(reparacion: ReparacionEntity) {
        reparacionDao.deleteReparacion(reparacion)
    }

    override suspend fun deleteReparacionById(id: Long) {
        reparacionDao.deleteReparacionById(id)
    }

    override suspend fun asignarComponente(
        reparacionId: Long,
        componenteId: Long,
        cantidad: Int
    ): Boolean {
        // Descuento atómico de stock previo a la vinculación transaccional
        val stockDeducido = componenteDao.deductStock(
            componenteId = componenteId,
            cantidad = cantidad
        )

        return if (stockDeducido > 0) {
            val asignacion = ReparacionComponenteEntity(
                reparacionId = reparacionId,
                componenteId = componenteId,
                cantidadUtilizada = cantidad
            )
            reparacionComponenteDao.insertAsignacion(asignacion)
            true
        } else {
            false
        }
    }

    override suspend fun removerComponente(
        reparacionId: Long,
        componenteId: Long,
        cantidadARestituir: Int
    ) {
        // Elimina el registro transaccional y restaura las existencias en inventario
        reparacionComponenteDao.removeComponenteFromReparacion(
            reparacionId = reparacionId,
            componenteId = componenteId
        )
        componenteDao.addStock(
            componenteId = componenteId,
            cantidad = cantidadARestituir
        )
    }
}