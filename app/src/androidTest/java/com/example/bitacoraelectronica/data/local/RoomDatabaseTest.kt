package com.example.bitacoraelectronica.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bitacoraelectronica.data.local.dao.ComponenteDao
import com.example.bitacoraelectronica.data.local.dao.ReparacionComponenteDao
import com.example.bitacoraelectronica.data.local.dao.ReparacionDao
import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity
import com.example.bitacoraelectronica.data.local.entity.ReparacionComponenteEntity
import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomDatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var reparacionDao: ReparacionDao
    private lateinit var componenteDao: ComponenteDao
    private lateinit var reparacionComponenteDao: ReparacionComponenteDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        reparacionDao = database.reparacionDao()
        componenteDao = database.componenteDao()
        reparacionComponenteDao = database.reparacionComponenteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetReparacion_verifiesCorrectPersistence() = runTest {
        val reparacion = ReparacionEntity(
            equipo = "Cargador Dell 65W",
            marcaModelo = "Dell HA65NS5-00",
            fallaReportada = "Sin salida de voltaje",
            diagnostico = "MOSFET en corto",
            medicionesTecnicas = "V_in: 120VAC | V_out: 0VDC",
            estado = ReparacionEntity.ESTADO_PENDIENTE,
            costoEstimado = 18.50
        )

        val id = reparacionDao.insertReparacion(reparacion)
        val loaded = reparacionDao.getReparacionByIdOnce(id)

        assertNotNull(loaded)
        assertEquals("Cargador Dell 65W", loaded?.equipo)
        assertEquals(ReparacionEntity.ESTADO_PENDIENTE, loaded?.estado)
    }

    @Test
    fun deductStock_decreasesStockCorrectly() = runTest {
        val componente = ComponenteEntity(
            nombre = "MOSFET IRF3205",
            categoria = ComponenteEntity.CATEGORIA_SEMICONDUCTORES,
            especificacionTecnica = "55V 110A",
            cantidadStock = 10,
            precioUnitario = 2.50
        )

        val compId = componenteDao.insertComponente(componente)
        val rowsAffected = componenteDao.deductStock(componenteId = compId, cantidad = 3)
        val updated = componenteDao.getComponenteByIdOnce(compId)

        assertEquals(1, rowsAffected)
        assertEquals(7, updated?.cantidadStock)
    }

    @Test
    fun relationalQuery_reparacionWithComponentes_returnsAssociatedParts() = runTest {
        val repId = reparacionDao.insertReparacion(
            ReparacionEntity(
                equipo = "Fuente ATX 500W",
                marcaModelo = "EVGA 500 W1",
                fallaReportada = "Apagado repentino"
            )
        )

        val compId = componenteDao.insertComponente(
            ComponenteEntity(
                nombre = "Capacitor 100µF 25V",
                categoria = ComponenteEntity.CATEGORIA_PASIVOS,
                cantidadStock = 20
            )
        )

        reparacionComponenteDao.insertAsignacion(
            ReparacionComponenteEntity(
                reparacionId = repId,
                componenteId = compId,
                cantidadUtilizada = 2
            )
        )

        val relationalData = reparacionDao.getReparacionWithComponentes(repId).first()

        assertNotNull(relationalData)
        assertEquals(1, relationalData?.componentes?.size)
        assertEquals("Capacitor 100µF 25V", relationalData?.componentes?.first()?.nombre)
    }
}