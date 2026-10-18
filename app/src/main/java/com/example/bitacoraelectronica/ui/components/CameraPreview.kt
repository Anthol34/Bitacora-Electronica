package com.example.bitacoraelectronica.ui.components

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

/**
 * Componente modular que encapsula el visor en tiempo real de CameraX (PreviewView)
 * y garantiza la liberación del hardware al salir de la composición.
 */
@Composable
fun CameraPreview(
    imageCapture: ImageCapture,
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // Garantiza que los casos de uso de CameraX se desvinculen al destruir el Composable
    DisposableEffect(lifecycleOwner) {
        onDispose {
            try {
                cameraProviderFuture.get().unbindAll()
            } catch (e: Exception) {
                Log.e("CameraPreview", "Error al liberar ProcessCameraProvider", e)
            }
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Fallo al vincular la cámara al ciclo de vida", exc)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}