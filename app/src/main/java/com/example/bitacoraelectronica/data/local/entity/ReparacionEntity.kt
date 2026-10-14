package com.example.bitacoraelectronica.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad maestra para la persistencia local de órdenes de trabajo y reparaciones electrónicas.
 * Tabla: "reparaciones"
 */
@Entity(tableName = "reparaciones")
data class ReparacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "equipo")
    val equipo: String,

    @ColumnInfo(name = "marca_modelo")
    val marcaModelo: String,

    @ColumnInfo(name = "falla_reportada")
    val fallaReportada: String,

    @ColumnInfo(name = "diagnostico")
    val diagnostico: String = "",

    @ColumnInfo(name = "mediciones_tecnicas")
    val medicionesTecnicas: String = "",

    @ColumnInfo(name = "estado")
    val estado: String = ESTADO_PENDIENTE,

    @ColumnInfo(name = "fecha_ingreso")
    val fechaIngreso: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "fecha_entrega")
    val fechaEntrega: Long? = null,

    @ColumnInfo(name = "foto_evidencia_uri")
    val fotoEvidenciaUri: String? = null,

    @ColumnInfo(name = "costo_estimado")
    val costoEstimado: Double = 0.0
) {
    companion object {
        // Estados normados de la reparación
        const val ESTADO_PENDIENTE = "Pendiente"
        const val ESTADO_EN_DIAGNOSTICO = "En Diagnóstico"
        const val ESTADO_ESPERANDO_PIEZA = "Esperando pieza"
        const val ESTADO_TERMINADO = "Terminado"

        val estadosDisponibles = listOf(
            ESTADO_PENDIENTE,
            ESTADO_EN_DIAGNOSTICO,
            ESTADO_ESPERANDO_PIEZA,
            ESTADO_TERMINADO
        )
    }
}