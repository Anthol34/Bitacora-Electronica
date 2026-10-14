package com.example.bitacoraelectronica.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bitacoraelectronica.data.local.entity.ReparacionComponenteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReparacionComponenteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsignacion(asignacion: ReparacionComponenteEntity): Long

    @Delete
    suspend fun deleteAsignacion(asignacion: ReparacionComponenteEntity)

    @Query(
        """
        DELETE FROM reparacion_componente 
        WHERE reparacion_id = :reparacionId AND componente_id = :componenteId
        """
    )
    suspend fun removeComponenteFromReparacion(reparacionId: Long, componenteId: Long)

    @Query(
        """
        SELECT * FROM reparacion_componente 
        WHERE reparacion_id = :reparacionId
        """
    )
    fun getAsignacionesByReparacion(reparacionId: Long): Flow<List<ReparacionComponenteEntity>>
}