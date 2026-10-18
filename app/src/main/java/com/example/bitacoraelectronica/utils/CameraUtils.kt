package com.example.bitacoraelectronica.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

fun takePhoto(
    imageCapture: ImageCapture,
    context: Context,
    onPhotoCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    // Directorio privado y aislado de la aplicación
    val outputDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    val photoFile = File(
        outputDirectory,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = try {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        photoFile
                    )
                } catch (_: Exception) {
                    Uri.fromFile(photoFile)
                }
                onPhotoCaptured(savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}