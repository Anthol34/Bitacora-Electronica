package com.example.bitacoraelectronica.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoRegistroScreen(
    onBackClick: () -> Unit,
    onRegistroGuardado: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NuevoRegistroViewModel = viewModel(factory = NuevoRegistroViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val componentesStock by viewModel.componentesDisponibles.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.registroExitoso) {
        if (uiState.registroExitoso) {
            onRegistroGuardado()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Nueva Ficha Técnica") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cancelar y regresar"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = uiState.equipo,
                onValueChange = viewModel::onEquipoChanged,
                label = { Text("Equipo / Dispositivo *") },
                placeholder = { Text("Ej. Cargador Laptop, Fuente ATX") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.marcaModelo,
                onValueChange = viewModel::onMarcaModeloChanged,
                label = { Text("Marca y Modelo *") },
                placeholder = { Text("Ej. Dell 65W LA65NS2-01") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.fallaReportada,
                onValueChange = viewModel::onFallaReportadaChanged,
                label = { Text("Falla Reportada *") },
                placeholder = { Text("Ej. No entrega voltaje de salida, LED apagado") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Estado Inicial del Equipo",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReparacionEntity.estadosDisponibles.forEach { estado ->
                    val selected = uiState.estadoSeleccionado == estado
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onEstadoSelected(estado) },
                        label = { Text(estado) },
                        leadingIcon = if (selected) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                }
            }

            OutlinedTextField(
                value = uiState.diagnostico,
                onValueChange = viewModel::onDiagnosticoChanged,
                label = { Text("Diagnóstico Preliminar") },
                placeholder = { Text("Ej. Cortocircuito en etapa primaria") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.medicionesTecnicas,
                onValueChange = viewModel::onMedicionesChanged,
                label = { Text("Mediciones con Multímetro / Osciloscopio") },
                placeholder = { Text("Ej. V_in = 165V DC, V_out = 0V, Rizado = N/A") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.costoEstimado,
                onValueChange = viewModel::onCostoChanged,
                label = { Text("Costo Estimado ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (componentesStock.isNotEmpty()) {
                Text(
                    text = "Vincular Repuesto Inicial (Opcional)",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    componentesStock.filter { it.cantidadStock > 0 }.forEach { comp ->
                        val isSelected = uiState.componenteInicialId == comp.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.onComponenteInicialSelected(
                                    if (isSelected) null else comp.id
                                )
                            },
                            label = { Text("${comp.nombre} (Stock: ${comp.cantidadStock})") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = viewModel::guardarNuevoRegistro,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isSaving) "Guardando..." else "Guardar Ficha Técnica")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}