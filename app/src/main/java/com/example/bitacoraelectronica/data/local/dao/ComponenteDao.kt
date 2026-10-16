package com.example.bitacoraelectronica.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) que gestiona el inventario de repuestos
 * e insumos electrónicos sobre la tabla "componentes".
 */
@Dao
interface ComponenteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponente(componente: ComponenteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllComponentes(componentes: List<ComponenteEntity>)

    @Update
    suspend fun updateComponente(componente: ComponenteEntity)

    @Delete
    suspend fun deleteComponente(componente: ComponenteEntity)

    @Query("DELETE FROM componentes WHERE id = :id")
    suspend fun deleteComponenteById(id: Long)

    @Query("SELECT * FROM componentes ORDER BY nombre ASC")
    fun getAllComponentes(): Flow<List<ComponenteEntity>>

    @Query("SELECT * FROM componentes WHERE id = :id")
    fun getComponenteById(id: Long): Flow<ComponenteEntity?>

    @Query("SELECT * FROM componentes WHERE id = :id")
    suspend fun getComponenteByIdOnce(id: Long): ComponenteEntity?

    @Query("SELECT * FROM componentes WHERE categoria = :categoria ORDER BY nombre ASC")
    fun getComponentesByCategoria(categoria: String): Flow<List<ComponenteEntity>>

    @Query(
        """
        SELECT * FROM componentes 
        WHERE nombre LIKE '%' || :query || '%' 
           OR especificacion_tecnica LIKE '%' || :query || '%'
           OR categoria LIKE '%' || :query || '%'
        ORDER BY nombre ASC
        """
    )
    fun searchComponentes(query: String): Flow<List<ComponenteEntity>>

    @Query(
        """
        UPDATE componentes 
        SET cantidad_stock = cantidad_stock - :cantidad 
        WHERE id = :componenteId AND cantidad_stock >= :cantidad
        """
    )
    suspend fun deductStock(componenteId: Long, cantidad: Int): Int

    @Query(
        """
        UPDATE componentes 
        SET cantidad_stock = cantidad_stock + :cantidad 
        WHERE id = :componenteId
        """
    )
    suspend fun addStock(componenteId: Long, cantidad: Int)

    @Query("SELECT * FROM componentes WHERE cantidad_stock <= :limiteMinimo ORDER BY cantidad_stock ASC")
    fun getComponentesLowStock(limiteMinimo: Int = 3): Flow<List<ComponenteEntity>>
}