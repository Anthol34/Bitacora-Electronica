package com.example.bitacoraelectronica.data.repository

import com.example.bitacoraelectronica.data.local.dao.ComponenteDao
import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Implementación concreta del repositorio de inventario con persistencia en Room.
 * Delega las operaciones CRUD y flujos reactivos al DAO local.
 */
class OfflineComponenteRepository(
    private val componenteDao: ComponenteDao
) : IComponenteRepository {

    override fun getAllComponentesStream(): Flow<List<ComponenteEntity>> {
        return componenteDao.getAllComponentes()
    }

    override fun getComponenteStream(id: Long): Flow<ComponenteEntity?> {
        return componenteDao.getComponenteById(id)
    }

    override suspend fun getComponenteOnce(id: Long): ComponenteEntity? {
        return componenteDao.getComponenteByIdOnce(id)
    }

    override fun getComponentesByCategoriaStream(categoria: String): Flow<List<ComponenteEntity>> {
        return componenteDao.getComponentesByCategoria(categoria)
    }

    override fun searchComponentesStream(query: String): Flow<List<ComponenteEntity>> {
        return componenteDao.searchComponentes(query)
    }

    override fun getLowStockComponentesStream(minimo: Int): Flow<List<ComponenteEntity>> {
        return componenteDao.getComponentesLowStock(minimo)
    }

    override suspend fun insertComponente(componente: ComponenteEntity): Long {
        return componenteDao.insertComponente(componente)
    }

    override suspend fun updateComponente(componente: ComponenteEntity) {
        componenteDao.updateComponente(componente)
    }

    override suspend fun deleteComponente(componente: ComponenteEntity) {
        componenteDao.deleteComponente(componente)
    }

    override suspend fun deductStock(componenteId: Long, cantidad: Int): Boolean {
        val updatedRows = componenteDao.deductStock(componenteId = componenteId, cantidad = cantidad)
        return updatedRows > 0
    }

    override suspend fun addStock(componenteId: Long, cantidad: Int) {
        componenteDao.addStock(componenteId = componenteId, cantidad = cantidad)
    }
}