package com.example.bitacoraelectronica.data

import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity
import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity
import com.example.bitacoraelectronica.fake.FakeComponenteRepository
import com.example.bitacoraelectronica.fake.FakeReparacionRepository
import com.example.bitacoraelectronica.fake.FakeSettingsRepository
import com.example.bitacoraelectronica.ui.screens.DetalleReparacionUiState
import com.example.bitacoraelectronica.ui.screens.InventarioUiState
import com.example.bitacoraelectronica.ui.screens.ReparacionesUiState
import com.example.bitacoraelectronica.ui.screens.SettingsUiState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepositoryAndStateHolderTest {

    private lateinit var fakeComponenteRepository: FakeComponenteRepository
    private lateinit var fakeReparacionRepository: FakeReparacionRepository
    private lateinit var fakeSettingsRepository: FakeSettingsRepository

    private val mosfetInicial = ComponenteEntity(
        id = 1L,
        nombre = "MOSFET IRF3205",
        categoria = ComponenteEntity.CATEGORIA_SEMICONDUCTORES,
        especificacionTecnica = "55V 110A TO-220",
        cantidadStock = 5,
        ubicacionTaller = "Gaveta A1",
        precioUnitario = 1.85
    )

    private val capacitorInicial = ComponenteEntity(
        id = 2L,
        nombre = "Capacitor Electrolítico 100uF",
        categoria = ComponenteEntity.CATEGORIA_PASIVOS,
        especificacionTecnica = "100uF 25V 105°C",
        cantidadStock = 2,
        ubicacionTaller = "Gaveta B4",
        precioUnitario = 0.35
    )

    private val reparacionInicial = ReparacionEntity(
        id = 10L,
        equipo = "Cargador Laptop Dell",
        marcaModelo = "Dell LA65NS2-01 65W",
        fallaReportada = "No enciende el LED indicador",
        diagnostico = "MOSFET de conmutación primaria en corto",
        medicionesTecnicas = "V_in = 165V DC, V_out = 0V DC",
        estado = ReparacionEntity.ESTADO_EN_DIAGNOSTICO,
        costoEstimado = 18.50
    )

    @Before
    fun setUp() {
        fakeComponenteRepository = FakeComponenteRepository(
            initialComponentes = listOf(mosfetInicial, capacitorInicial)
        )
        fakeReparacionRepository = FakeReparacionRepository(
            fakeComponenteRepository = fakeComponenteRepository,
            initialReparaciones = listOf(reparacionInicial)
        )
        fakeSettingsRepository = FakeSettingsRepository()
    }

    @Test
    fun componenteRepository_emitsSortedListAndDetectsLowStock() = runBlocking {
        val todos = fakeComponenteRepository.getAllComponentesStream().first()
        assertEquals(2, todos.size)
        // Orden alfabético: Capacitor primero, MOSFET después
        assertEquals("Capacitor Electrolítico 100uF", todos[0].nombre)
        assertEquals("MOSFET IRF3205", todos[1].nombre)

        val bajoStock = fakeComponenteRepository.getLowStockComponentesStream(minimo = 3).first()
        assertEquals(1, bajoStock.size)
        assertEquals(2L, bajoStock.first().id)

        val uiState = InventarioUiState(
            listaComponentes = todos,
            filtroCategoria = ComponenteEntity.CATEGORIA_PASIVOS,
            isLoading = false
        )
        assertFalse(uiState.isLoading)
        assertEquals(2, uiState.listaComponentes.size)
    }

    @Test
    fun componenteRepository_deductStock_validatesAvailableQuantity() = runBlocking {
        // Descuento válido (hay 5 unidades de MOSFET, descontamos 3)
        val exito = fakeComponenteRepository.deductStock(componenteId = 1L, cantidad = 3)
        assertTrue(exito)
        assertEquals(2, fakeComponenteRepository.getComponenteOnce(1L)?.cantidadStock)

        // Descuento inválido (quedan 2 unidades, intentamos descontar 4)
        val falloPorStockInsuficiente = fakeComponenteRepository.deductStock(componenteId = 1L, cantidad = 4)
        assertFalse(falloPorStockInsuficiente)
        assertEquals(2, fakeComponenteRepository.getComponenteOnce(1L)?.cantidadStock)
    }

    @Test
    fun reparacionRepository_assignAndRemoveComponent_updatesCrossReferenceAndStock() = runBlocking {
        // Asignamos 2 MOSFETs a la reparación #10
        val asignado = fakeReparacionRepository.asignarComponente(
            reparacionId = 10L,
            componenteId = 1L,
            cantidad = 2
        )
        assertTrue(asignado)

        // Verificamos que el stock del MOSFET bajó de 5 a 3
        val mosfetActualizado = fakeComponenteRepository.getComponenteOnce(1L)
        assertEquals(3, mosfetActualizado?.cantidadStock)

        // Verificamos la relación N:M en el flujo reactivo
        val relacion = fakeReparacionRepository.getReparacionWithComponentesStream(10L).first()
        assertNotNull(relacion)
        assertEquals(1, relacion?.componentes?.size)
        assertEquals("MOSFET IRF3205", relacion?.componentes?.first()?.nombre)

        val detalleState = DetalleReparacionUiState(
            reparacionConComponentes = relacion,
            isLoading = false,
            operacionExitosa = true
        )
        assertTrue(detalleState.operacionExitosa)

        // Removemos el componente y restituimos las 2 unidades al inventario
        fakeReparacionRepository.removerComponente(
            reparacionId = 10L,
            componenteId = 1L,
            cantidadARestituir = 2
        )
        assertEquals(5, fakeComponenteRepository.getComponenteOnce(1L)?.cantidadStock)
        val relacionDespues = fakeReparacionRepository.getReparacionWithComponentesStream(10L).first()
        assertTrue(relacionDespues?.componentes.isNullOrEmpty())
    }

    @Test
    fun reparacionRepository_searchAndFilterStreams_emitMatchingRecords() = runBlocking {
        val nuevaOrden = ReparacionEntity(
            equipo = "Fuente Conmutada ATX",
            marcaModelo = "Corsair CV550",
            fallaReportada = "Sin voltaje en línea de +12V",
            diagnostico = "Diodo Schottky rectificador abierto",
            estado = ReparacionEntity.ESTADO_TERMINADO
        )
        fakeReparacionRepository.insertReparacion(nuevaOrden)

        val terminadas = fakeReparacionRepository
            .getReparacionesByEstadoStream(ReparacionEntity.ESTADO_TERMINADO)
            .first()
        assertEquals(1, terminadas.size)
        assertEquals("Corsair CV550", terminadas.first().marcaModelo)

        val busqueda = fakeReparacionRepository.searchReparacionesStream("Schottky").first()
        assertEquals(1, busqueda.size)
        assertEquals("Fuente Conmutada ATX", busqueda.first().equipo)

        val reparacionesState = ReparacionesUiState(
            listaReparaciones = busqueda,
            searchQuery = "Schottky",
            isLoading = false
        )
        assertEquals(1, reparacionesState.listaReparaciones.size)
    }

    @Test
    fun settingsRepository_updatesDarkModeFontSizeAndBackupTimestamp() = runBlocking {
        assertFalse(fakeSettingsRepository.isDarkModeStream.first())
        assertEquals("Media", fakeSettingsRepository.fontSizeStream.first())

        fakeSettingsRepository.setDarkMode(true)
        fakeSettingsRepository.setFontSize("Grande")
        fakeSettingsRepository.updateLastBackupTimestamp("17/10/2026 23:30")

        val state = SettingsUiState(
            isDarkMode = fakeSettingsRepository.isDarkModeStream.first(),
            fontSize = fakeSettingsRepository.fontSizeStream.first(),
            lastBackupTimestamp = fakeSettingsRepository.lastBackupTimestampStream.first(),
            isLoading = false
        )

        assertTrue(state.isDarkMode)
        assertEquals("Grande", state.fontSize)
        assertEquals("17/10/2026 23:30", state.lastBackupTimestamp)
    }
}