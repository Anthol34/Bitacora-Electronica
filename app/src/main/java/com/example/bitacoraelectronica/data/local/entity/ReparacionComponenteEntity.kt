package com.example.bitacoraelectronica.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad transaccional para la relación N:M entre Reparaciones y Componentes de inventario.
 * Cumple con el requisito técnico de base de datos local (2 tablas maestras y 1 transaccional).
 * Tabla: "reparacion_componente"
 */
@Entity(
    tableName = "reparacion_componente",
    foreignKeys = [
        ForeignKey(
            entity = ReparacionEntity::class,
            parentColumns = ["id"],
            childColumns = ["reparacion_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ComponenteEntity::class,
            parentColumns = ["id"],
            childColumns = ["componente_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["reparacion_id"]),
        Index(value = ["componente_id"])
    ]
)
data class ReparacionComponenteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "reparacion_id")
    val reparacionId: Long,

    @ColumnInfo(name = "componente_id")
    val componenteId: Long,

    @ColumnInfo(name = "cantidad_utilizada")
    val cantidadUtilizada: Int = 1,

    @ColumnInfo(name = "fecha_asignacion")
    val fechaAsignacion: Long = System.currentTimeMillis()
)