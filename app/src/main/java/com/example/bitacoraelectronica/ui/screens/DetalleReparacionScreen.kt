package com.example.bitacoraelectronica.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity
import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleReparacionScreen(
    reparacionId: Long,
    onBackClick: () -> Unit,
    onTomarFotoClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetalleReparacionViewModel = viewModel(
        factory = DetalleReparacionViewModel.provideFactory(reparacionId)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Campos de edición local
    var equipo by remember { mutableStateOf("") }
    var marcaModelo by remember { mutableStateOf("") }
    var fallaReportada by remember { mutableStateOf("") }
    var diagnostico by remember { mutableStateOf("") }
    var medicionesTecnicas by remember { mutableStateOf("") }
    var costoText by remember { mutableStateOf("0.0") }
    var showAsignarDialog by remember { mutableStateOf(false) }

    // Carga inicial de datos desde la base de datos local
    LaunchedEffect(uiState.reparacionConComponentes) {
        uiState.reparacionConComponentes?.reparacion?.let { rep ->
            equipo = rep.equipo
            marcaModelo = rep.marcaModelo
            fallaReportada = rep.fallaReportada
            diagnostico = rep.diagnostico
            medicionesTecnicas = rep.medicionesTecnicas
            costoText = rep.costoEstimado.toString()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.limpiarMensajeError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Ficha Técnica #${reparacionId}") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.guardarDatosTecnicos(
                                equipo = equipo,
                                marcaModelo = marcaModelo,
                                fallaReportada = fallaReportada,
                                diagnostico = diagnostico,
                                medicionesTecnicas = medicionesTecnicas,
                                costoEstimado = costoText.toDoubleOrNull() ?: 0.0
                            )
                        },
                        enabled = !uiState.isSaving
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Guardar cambios")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            val reparacion = uiState.reparacionConComponentes?.reparacion
            val componentes = uiState.reparacionConComponentes?.componentes ?: emptyList()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sección 1: Selector rápido de estado operativo (RF-06)
                Text(
                    text = "Estado del Equipo en Taller",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReparacionEntity.estadosDisponibles.forEach { estado ->
                        FilterChip(
                            selected = reparacion?.estado == estado,
                            onClick = { viewModel.actualizarEstado(estado) },
                            label = { Text(estado) }
                        )
                    }
                }

                // Sección 2: Evidencia Fotográfica Aislada (RF-03)
                Text(
                    text = "Evidencia Visual de Placa / Diagrama",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { onTomarFotoClick(reparacionId) },
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    if (!reparacion?.fotoEvidenciaUri.isNullOrBlank()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = Uri.parse(reparacion!!.fotoEvidenciaUri),
                                contentDescription = "Evidencia de reparación",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                    .padding(6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Actualizar foto",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Tomar fotografía",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tocar para capturar evidencia fotográfica aislada",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Sección 3: Datos técnicos y mediciones de multímetro/osciloscopio
                Text(
                    text = "Diagnóstico y Parámetros Técnicos",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = equipo,
                    onValueChange = { equipo = it },
                    label = { Text("Equipo (ej. Cargador Dell 65W)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = marcaModelo,
                    onValueChange = { marcaModelo = it },
                    label = { Text("Marca y Modelo exacto") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fallaReportada,
                    onValueChange = { fallaReportada = it },
                    label = { Text("Síntoma / Falla reportada") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = diagnostico,
                    onValueChange = { diagnostico = it },
                    label = { Text("Diagnóstico del circuito") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = medicionesTecnicas,
                    onValueChange = { medicionesTecnicas = it },
                    label = { Text("Mediciones (ej. V_in: 120VAC | V_out: 0V | Rizado: 10mV)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Engineering, contentDescription = null)
                    }
                )

                OutlinedTextField(
                    value = costoText,
                    onValueChange = { costoText = it },
                    label = { Text("Costo estimado de reparación ($)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Sección 4: Consumo y Vinculación de Repuestos (RF-07)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Componentes de Inventario Utilizados",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(onClick = { showAsignarDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Vincular")
                    }
                }

                if (componentes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se han asignado piezas del inventario a esta orden.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            componentes.forEachIndexed { index, componente ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = componente.nombre,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${componente.especificacionTecnica} - ${componente.ubicacionTaller}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.desvincularComponente(
                                                componenteId = componente.id,
                                                cantidadARestituir = 1
                                            )
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Desvincular repuesto",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                                if (index < componentes.size - 1) {
                                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAsignarDialog) {
        AsignarComponenteDialog(
            componentesDisponibles = uiState.componentesDisponibles,
            onDismiss = { showAsignarDialog = false },
            onSelect = { componenteSeleccionado ->
                viewModel.vincularComponente(componenteId = componenteSeleccionado.id, cantidad = 1)
                showAsignarDialog = false
            }
        )
    }
}

@Composable
fun AsignarComponenteDialog(
    componentesDisponibles: List<ComponenteEntity>,
    onDismiss: () -> Unit,
    onSelect: (ComponenteEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Repuesto de Stock") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (componentesDisponibles.isEmpty()) {
                    Text("No hay piezas disponibles en el inventario.")
                } else {
                    componentesDisponibles.forEach { comp ->
                        val disponible = comp.cantidadStock > 0
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = disponible) { onSelect(comp) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (disponible) MaterialTheme.colorScheme.surface
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = comp.nombre,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Stock: ${comp.cantidadStock} un.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (disponible) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.error
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Outlined.Inventory2,
                                    contentDescription = null,
                                    tint = if (disponible) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}