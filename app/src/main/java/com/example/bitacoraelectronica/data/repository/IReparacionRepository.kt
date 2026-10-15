package com.example.bitacoraelectronica.data.repository

import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity
import com.example.bitacoraelectronica.data.local.model.ReparacionWithComponentes
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de abstracción del repositorio para la gestión de órdenes de trabajo y reparaciones.
 * Expone flujos reactivos de datos para la capa UI y operaciones de mutación transaccionales.
 */
interface IReparacionRepository {

    /**
     * Emite la lista completa de reparaciones ordenadas por fecha de ingreso descendente.
     */
    fun getAllReparacionesStream(): Flow<List<ReparacionEntity>>

    /**
     * Emite los datos reactivos de una reparación según su identificador único.
     */
    fun getReparacionStream(id: Long): Flow<ReparacionEntity?>

    /**
     * Obtiene una lectura puntual única de una reparación de forma suspendida.
     */
    suspend fun getReparacionOnce(id: Long): ReparacionEntity?

    /**
     * Filtra reactivamente las reparaciones según su estado operativo actual.
     */
    fun getReparacionesByEstadoStream(estado: String): Flow<List<ReparacionEntity>>

    /**
     * Búsqueda predictiva por coincidencia de texto en equipo, marca/modelo o diagnóstico.
     */
    fun searchReparacionesStream(query: String): Flow<List<ReparacionEntity>>

    /**
     * Emite la orden de reparación junto a la lista completa de componentes asociados (N:M).
     */
    fun getReparacionWithComponentesStream(id: Long): Flow<ReparacionWithComponentes?>

    /**
     * Inserta una nueva ficha de reparación en la base de datos local y retorna su ID generado.
     */
    suspend fun insertReparacion(reparacion: ReparacionEntity): Long

    /**
     * Actualiza la información técnica, mediciones, estados o evidencia fotográfica de una reparación.
     */
    suspend fun updateReparacion(reparacion: ReparacionEntity)

    /**
     * Elimina el registro de una reparación por entidad.
     */
    suspend fun deleteReparacion(reparacion: ReparacionEntity)

    /**
     * Elimina un registro de reparación por su identificador primario.
     */
    suspend fun deleteReparacionById(id: Long)

    /**
     * Asocia una pieza de inventario a una reparación descontando el stock correspondiente de forma atómica.
     * Retorna verdadero si la asignación y deducción fueron exitosas.
     */
    suspend fun asignarComponente(reparacionId: Long, componenteId: Long, cantidad: Int): Boolean

    /**
     * Desvincula un repuesto de una reparación y restituye las unidades consumidas al stock del inventario.
     */
    suspend fun removerComponente(reparacionId: Long, componenteId: Long, cantidadARestituir: Int)
}