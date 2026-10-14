package com.example.bitacoraelectronica.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad maestra para la persistencia local del inventario de repuestos e insumos electrónicos.
 * Tabla: "componentes"
 */
@Entity(tableName = "componentes")
data class ComponenteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "categoria")
    val categoria: String,

    @ColumnInfo(name = "especificacion_tecnica")
    val especificacionTecnica: String = "",

    @ColumnInfo(name = "cantidad_stock")
    val cantidadStock: Int = 0,

    @ColumnInfo(name = "ubicacion_taller")
    val ubicacionTaller: String = "",

    @ColumnInfo(name = "precio_unitario")
    val precioUnitario: Double = 0.0,

    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: Long = System.currentTimeMillis()
) {
    companion object {
        // Categorías técnicas normalizadas para el taller
        const val CATEGORIA_SEMICONDUCTORES = "Semiconductores"
        const val CATEGORIA_PASIVOS = "Pasivos"
        const val CATEGORIA_PROTECCION = "Protección"
        const val CATEGORIA_HERRAMIENTA = "Herramienta"
        const val CATEGORIA_CIRCUITOS_INTEGRADOS = "Circuitos Integrados"

        val categoriasDisponibles = listOf(
            CATEGORIA_SEMICONDUCTORES,
            CATEGORIA_PASIVOS,
            CATEGORIA_PROTECCION,
            CATEGORIA_HERRAMIENTA,
            CATEGORIA_CIRCUITOS_INTEGRADOS
        )
    }
}