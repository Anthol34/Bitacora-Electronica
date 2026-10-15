package com.example.bitacoraelectronica.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity
import com.example.bitacoraelectronica.data.local.entity.ReparacionComponenteEntity
import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Callback de Room para poblar la base de datos con datos de prueba
 * al crearse por primera vez en el dispositivo móvil.
 */
class DatabaseCallback(
    private val scope: CoroutineScope,
    private val databaseProvider: () -> AppDatabase
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch(Dispatchers.IO) {
            populateInitialData(databaseProvider())
        }
    }

    private suspend fun populateInitialData(database: AppDatabase) {
        val reparacionDao = database.reparacionDao()
        val componenteDao = database.componenteDao()
        val reparacionComponenteDao = database.reparacionComponenteDao()

        // 1. Inserción de componentes semilla de inventario
        val mosfetId = componenteDao.insertComponente(
            ComponenteEntity(
                nombre = "MOSFET IRF3205",
                categoria = ComponenteEntity.CATEGORIA_SEMICONDUCTORES,
                especificacionTecnica = "55V 110A TO-220 N-Channel",
                cantidadStock = 12,
                ubicacionTaller = "Gaveta A1",
                precioUnitario = 2.50
            )
        )

        val capacitorId = componenteDao.insertComponente(
            ComponenteEntity(
                nombre = "Capacitor 100µF 25V",
                categoria = ComponenteEntity.CATEGORIA_PASIVOS,
                especificacionTecnica = "Electrolítico radial 105°C",
                cantidadStock = 34,
                ubicacionTaller = "Gaveta B3",
                precioUnitario = 0.35
            )
        )

        val fusibleId = componenteDao.insertComponente(
            ComponenteEntity(
                nombre = "Fusible 5A rápido",
                categoria = ComponenteEntity.CATEGORIA_PROTECCION,
                especificacionTecnica = "Cristal 20mm 250V",
                cantidadStock = 6,
                ubicacionTaller = "Gaveta C2",
                precioUnitario = 0.50
            )
        )

        val reguladorId = componenteDao.insertComponente(
            ComponenteEntity(
                nombre = "Regulador LM7805",
                categoria = ComponenteEntity.CATEGORIA_SEMICONDUCTORES,
                especificacionTecnica = "Salida fija 5V 1.5A TO-220",
                cantidadStock = 2,
                ubicacionTaller = "Gaveta A4",
                precioUnitario = 1.20
            )
        )

        componenteDao.insertComponente(
            ComponenteEntity(
                nombre = "Cautín punta fina",
                categoria = ComponenteEntity.CATEGORIA_HERRAMIENTA,
                especificacionTecnica = "Repuesto cerámico 60W",
                cantidadStock = 3,
                ubicacionTaller = "Estante H1",
                precioUnitario = 4.00
            )
        )

        // 2. Inserción de órdenes de reparación semilla
        val repDellId = reparacionDao.insertReparacion(
            ReparacionEntity(
                equipo = "Cargador Dell 65W",
                marcaModelo = "Dell HA65NS5-00",
                fallaReportada = "No entrega voltaje de salida ni enciende el LED indicador",
                diagnostico = "MOSFET conmutador primario en cortocircuito y fusible fundido",
                medicionesTecnicas = "V_in: 120.0 VAC | V_out: 0.0 VDC | Rizado: 0 mVpp",
                estado = ReparacionEntity.ESTADO_PENDIENTE,
                costoEstimado = 18.50
            )
        )

        reparacionDao.insertReparacion(
            ReparacionEntity(
                equipo = "Placa madre Asus B450",
                marcaModelo = "Asus Prime B450M-A",
                fallaReportada = "No da video, ventiladores giran al máximo",
                diagnostico = "Fase de alimentación VRM degradada en carril Vcore",
                medicionesTecnicas = "V_in: 12.1 VDC | V_core: 0.4 VDC (Anormal)",
                estado = ReparacionEntity.ESTADO_PENDIENTE,
                costoEstimado = 35.00
            )
        )

        reparacionDao.insertReparacion(
            ReparacionEntity(
                equipo = "Fuente ATX 500W",
                marcaModelo = "EVGA 500 W1",
                fallaReportada = "Apagón repentino bajo carga gráfica moderada",
                diagnostico = "Capacitores de filtrado del secundario desvalorizados",
                medicionesTecnicas = "V_in: 118.4 VAC | V_out: 4.7 VDC | Rizado: 210 mVpp",
                estado = ReparacionEntity.ESTADO_TERMINADO,
                costoEstimado = 22.00
            )
        )

        reparacionDao.insertReparacion(
            ReparacionEntity(
                equipo = "Amplificador de audio",
                marcaModelo = "Yamaha A-S301",
                fallaReportada = "Canal derecho con distorsión y sobrecalentamiento",
                diagnostico = "Transistores de potencia complementarios con fuga térmica",
                medicionesTecnicas = "Bias derecho: 85 mV (Alto) | Bias izquierdo: 15 mV (Normal)",
                estado = ReparacionEntity.ESTADO_TERMINADO,
                costoEstimado = 45.00
            )
        )

        // 3. Vinculación transaccional: piezas usadas en el Cargador Dell
        reparacionComponenteDao.insertAsignacion(
            ReparacionComponenteEntity(
                reparacionId = repDellId,
                componenteId = mosfetId,
                cantidadUtilizada = 1
            )
        )
        reparacionComponenteDao.insertAsignacion(
            ReparacionComponenteEntity(
                reparacionId = repDellId,
                componenteId = fusibleId,
                cantidadUtilizada = 1
            )
        )
    }
}