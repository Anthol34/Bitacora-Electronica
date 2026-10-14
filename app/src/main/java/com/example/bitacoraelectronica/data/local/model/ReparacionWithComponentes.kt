package com.example.bitacoraelectronica.data.local.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity
import com.example.bitacoraelectronica.data.local.entity.ReparacionComponenteEntity
import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity

/**
 * Modelo relacional que representa una orden de reparación junto con
 * la lista completa de repuestos consumidos en el banco de pruebas.
 */
data class ReparacionWithComponentes(
    @Embedded
    val reparacion: ReparacionEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ReparacionComponenteEntity::class,
            parentColumn = "reparacion_id",
            entityColumn = "componente_id"
        )
    )
    val componentes: List<ComponenteEntity> = emptyList()
)

/**
 * Modelo de lectura extendida para proyectar componentes con su cantidad consumida
 */
data class ComponenteAsignadoItem(
    val componenteId: Long,
    val nombre: String,
    val categoria: String,
    val especificacionTecnica: String,
    val cantidadUtilizada: Int,
    val precioUnitario: Double
)