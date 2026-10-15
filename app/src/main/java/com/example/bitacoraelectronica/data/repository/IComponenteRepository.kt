package com.example.bitacoraelectronica.data.repository

import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio para la gestión del inventario de insumos y repuestos.
 * Define flujos reactivos asíncronos para lecturas continuas y funciones suspendibles para mutaciones.
 */
interface IComponenteRepository {

    /**
     * Emite la lista completa de componentes ordenados alfabéticamente.
     */
    fun getAllComponentesStream(): Flow<List<ComponenteEntity>>

    /**
     * Emite un flujo con el componente correspondiente al identificador suministrado.
     */
    fun getComponenteStream(id: Long): Flow<ComponenteEntity?>

    /**
     * Obtiene una lectura puntual única de un componente.
     */
    suspend fun getComponenteOnce(id: Long): ComponenteEntity?

    /**
     * Filtra reactivamente los componentes que coinciden con una categoría específica.
     */
    fun getComponentesByCategoriaStream(categoria: String): Flow<List<ComponenteEntity>>

    /**
     * Búsqueda predictiva de componentes por coincidencia en nombre o especificaciones técnicas.
     */
    fun searchComponentesStream(query: String): Flow<List<ComponenteEntity>>

    /**
     * Emite los componentes cuyas existencias son iguales o inferiores al umbral mínimo de alerta.
     */
    fun getLowStockComponentesStream(minimo: Int = 3): Flow<List<ComponenteEntity>>

    /**
     * Inserta un nuevo componente en la base de datos local y retorna el identificador autogenerado.
     */
    suspend fun insertComponente(componente: ComponenteEntity): Long

    /**
     * Actualiza los datos de un componente existente.
     */
    suspend fun updateComponente(componente: ComponenteEntity)

    /**
     * Elimina un componente del catálogo local.
     */
    suspend fun deleteComponente(componente: ComponenteEntity)

    /**
     * Descuenta existencias de un componente de forma atómica.
     * Retorna verdadero si la deducción fue exitosa (stock suficiente), falso en caso contrario.
     */
    suspend fun deductStock(componenteId: Long, cantidad: Int): Boolean

    /**
     * Incrementa las existencias en inventario de un componente.
     */
    suspend fun addStock(componenteId: Long, cantidad: Int)
}