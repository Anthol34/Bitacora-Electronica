package com.example.bitacoraelectronica.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Callback de Room para poblar la base de datos con datos de prueba
 * al crearse por primera vez en el dispositivo móvil de forma atómica y directa.
 */
class DatabaseCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        try {
            populateInitialData(db)
        } catch (e: Exception) {
            android.util.Log.e("DatabaseCallback", "Error poblando la base de datos inicial", e)
        }
    }

    private fun populateInitialData(db: SupportSQLiteDatabase) {
        val now = System.currentTimeMillis()

        // 1. Inserción de componentes semilla de inventario
        db.execSQL(
            """
            INSERT INTO componentes (id, nombre, categoria, especificacion_tecnica, cantidad_stock, ubicacion_taller, precio_unitario, fecha_registro)
            VALUES (1, 'MOSFET IRF3205', 'Semiconductores', '55V 110A TO-220 N-Channel', 12, 'Gaveta A1', 2.50, $now)
            """
        )

        db.execSQL(
            """
            INSERT INTO componentes (id, nombre, categoria, especificacion_tecnica, cantidad_stock, ubicacion_taller, precio_unitario, fecha_registro)
            VALUES (2, 'Capacitor 100µF 25V', 'Pasivos', 'Electrolítico radial 105°C', 34, 'Gaveta B3', 0.35, $now)
            """
        )

        db.execSQL(
            """
            INSERT INTO componentes (id, nombre, categoria, especificacion_tecnica, cantidad_stock, ubicacion_taller, precio_unitario, fecha_registro)
            VALUES (3, 'Fusible 5A rápido', 'Protección', 'Cristal 20mm 250V', 6, 'Gaveta C2', 0.50, $now)
            """
        )

        db.execSQL(
            """
            INSERT INTO componentes (id, nombre, categoria, especificacion_tecnica, cantidad_stock, ubicacion_taller, precio_unitario, fecha_registro)
            VALUES (4, 'Regulador LM7805', 'Semiconductores', 'Salida fija 5V 1.5A TO-220', 2, 'Gaveta A4', 1.20, $now)
            """
        )

        db.execSQL(
            """
            INSERT INTO componentes (id, nombre, categoria, especificacion_tecnica, cantidad_stock, ubicacion_taller, precio_unitario, fecha_registro)
            VALUES (5, 'Cautín punta fina', 'Herramienta', 'Repuesto cerámico 60W', 3, 'Estante H1', 4.00, $now)
            """
        )

        // 2. Inserción de órdenes de reparación semilla
        db.execSQL(
            """
            INSERT INTO reparaciones (id, equipo, marca_modelo, falla_reportada, diagnostico, mediciones_tecnicas, estado, fecha_ingreso, fecha_entrega, foto_evidencia_uri, costo_estimado)
            VALUES (1, 'Cargador Dell 65W', 'Dell HA65NS5-00', 'No entrega voltaje de salida ni enciende el LED indicador', 'MOSFET conmutador primario en cortocircuito y fusible fundido', 'V_in: 120.0 VAC | V_out: 0.0 VDC | Rizado: 0 mVpp', 'Pendiente', $now, NULL, NULL, 18.50)
            """
        )

        db.execSQL(
            """
            INSERT INTO reparaciones (id, equipo, marca_modelo, falla_reportada, diagnostico, mediciones_tecnicas, estado, fecha_ingreso, fecha_entrega, foto_evidencia_uri, costo_estimado)
            VALUES (2, 'Placa madre Asus B450', 'Asus Prime B450M-A', 'No da video, ventiladores giran al máximo', 'Fase de alimentación VRM degradada en carril Vcore', 'V_in: 12.1 VDC | V_core: 0.4 VDC (Anormal)', 'Pendiente', $now, NULL, NULL, 35.00)
            """
        )

        db.execSQL(
            """
            INSERT INTO reparaciones (id, equipo, marca_modelo, falla_reportada, diagnostico, mediciones_tecnicas, estado, fecha_ingreso, fecha_entrega, foto_evidencia_uri, costo_estimado)
            VALUES (3, 'Fuente ATX 500W', 'EVGA 500 W1', 'Apagón repentino bajo carga gráfica moderada', 'Capacitores de filtrado del secundario desvalorizados', 'V_in: 118.4 VAC | V_out: 4.7 VDC | Rizado: 210 mVpp', 'Terminado', $now, NULL, NULL, 22.00)
            """
        )

        db.execSQL(
            """
            INSERT INTO reparaciones (id, equipo, marca_modelo, falla_reportada, diagnostico, mediciones_tecnicas, estado, fecha_ingreso, fecha_entrega, foto_evidencia_uri, costo_estimado)
            VALUES (4, 'Amplificador de audio', 'Yamaha A-S301', 'Canal derecho con distorsión y sobrecalentamiento', 'Transistores de potencia complementarios con fuga térmica', 'Bias derecho: 85 mV (Alto) | Bias izquierdo: 15 mV (Normal)', 'Terminado', $now, NULL, NULL, 45.00)
            """
        )

        // 3. Vinculación transaccional
        db.execSQL(
            """
            INSERT INTO reparacion_componente (id, reparacion_id, componente_id, cantidad_utilizada, fecha_asignacion)
            VALUES (1, 1, 1, 1, $now)
            """
        )

        db.execSQL(
            """
            INSERT INTO reparacion_componente (id, reparacion_id, componente_id, cantidad_utilizada, fecha_asignacion)
            VALUES (2, 1, 3, 1, $now)
            """
        )
    }
}
