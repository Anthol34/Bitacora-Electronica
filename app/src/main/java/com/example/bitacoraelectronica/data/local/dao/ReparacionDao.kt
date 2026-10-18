package com.example.bitacoraelectronica.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity
import com.example.bitacoraelectronica.data.local.model.ReparacionWithComponentes
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) que gestiona las operaciones CRUD y consultas reactivas
 * de las órdenes de servicio técnico sobre la tabla "reparaciones".
 */
@Dao
interface ReparacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReparacion(reparacion: ReparacionEntity): Long

    @Update
    suspend fun updateReparacion(reparacion: ReparacionEntity)

    @Delete
    suspend fun deleteReparacion(reparacion: ReparacionEntity)

    @Query("DELETE FROM reparaciones WHERE id = :id")
    suspend fun deleteReparacionById(id: Long): Int

    @Query("SELECT * FROM reparaciones ORDER BY fecha_ingreso DESC")
    fun getAllReparaciones(): Flow<List<ReparacionEntity>>

    @Query("SELECT * FROM reparaciones WHERE id = :id")
    fun getReparacionById(id: Long): Flow<ReparacionEntity?>

    @Query("SELECT * FROM reparaciones WHERE id = :id")
    suspend fun getReparacionByIdOnce(id: Long): ReparacionEntity?

    @Query("SELECT * FROM reparaciones WHERE estado = :estado ORDER BY fecha_ingreso DESC")
    fun getReparacionesByEstado(estado: String): Flow<List<ReparacionEntity>>

    @Query(
        """
        SELECT * FROM reparaciones 
        WHERE equipo LIKE '%' || :query || '%' 
           OR marca_modelo LIKE '%' || :query || '%' 
           OR diagnostico LIKE '%' || :query || '%'
        ORDER BY fecha_ingreso DESC
        """
    )
    fun searchReparaciones(query: String): Flow<List<ReparacionEntity>>

    @Transaction
    @Query("SELECT * FROM reparaciones WHERE id = :id")
    fun getReparacionWithComponentes(id: Long): Flow<ReparacionWithComponentes?>

    @Transaction
    @Query("SELECT * FROM reparaciones ORDER BY fecha_ingreso DESC")
    fun getAllReparacionesWithComponentes(): Flow<List<ReparacionWithComponentes>>
}