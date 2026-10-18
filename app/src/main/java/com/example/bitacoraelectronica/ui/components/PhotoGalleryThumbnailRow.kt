package com.example.bitacoraelectronica.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Fila y tarjeta de previsualización de evidencia fotográfica técnica aislada (RF-03).
 * Carga la imagen de forma asíncrona desde el directorio privado de la aplicación.
 */
@Composable
fun PhotoGalleryThumbnailRow(
    fotoEvidenciaUri: String?,
    onTomarFotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showFullScreenDialog by remember { mutableStateOf(false) }

    // Decodificación asíncrona en Dispatchers.IO para no bloquear el hilo UI
    val imageBitmap by produceState<ImageBitmap?>(initialValue = null, key1 = fotoEvidenciaUri) {
        value = if (fotoEvidenciaUri.isNullOrBlank()) {
            null
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val uri = Uri.parse(fotoEvidenciaUri)
                    val path = uri.path
                    if (path != null && File(path).exists()) {
                        BitmapFactory.decodeFile(path)?.asImageBitmap()
                    } else {
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                        }
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Evidencia Fotográfica (Aislada)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Almacenada en el directorio privado del taller",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AssistChip(
                    onClick = onTomarFotoClick,
                    label = {
                        Text(if (fotoEvidenciaUri.isNullOrBlank()) "Capturar" else "Reemplazar")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Abrir cámara técnica",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            if (fotoEvidenciaUri.isNullOrBlank()) {
                // Estado vacío cuando aún no hay evidencia capturada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Sin fotografías asociadas a esta orden",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Fila de miniatura con metadatos del archivo aislado
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable(enabled = imageBitmap != null) {
                            showFullScreenDialog = true
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        val bitmap = imageBitmap
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Miniatura de evidencia técnica",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                                    .size(22.dp)
                                    .background(
                                        MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f),
                                        RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ZoomIn,
                                    contentDescription = "Ampliar imagen",
                                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.BrokenImage,
                                contentDescription = "Archivo de imagen",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        val fileName = Uri.parse(fotoEvidenciaUri).lastPathSegment
                            ?: "evidencia_placa.jpg"
                        Text(
                            text = fileName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ruta privada: /Android/data/.../Pictures",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (imageBitmap != null) "Toca para inspeccionar placa" else "URI registrada",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Diálogo modal para inspeccionar la evidencia fotográfica ampliada
    if (showFullScreenDialog && imageBitmap != null) {
        Dialog(onDismissRequest = { showFullScreenDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Inspección de Evidencia",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showFullScreenDialog = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar vista ampliada"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = "Fotografía ampliada del circuito",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            showFullScreenDialog = false
                            onTomarFotoClick()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar Nueva Fotografía")
                    }
                }
            }
        }
    }
}