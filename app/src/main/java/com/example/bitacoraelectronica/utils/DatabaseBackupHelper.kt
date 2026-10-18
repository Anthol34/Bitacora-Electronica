package com.example.bitacoraelectronica.utils

import android.content.Context
import com.example.bitacoraelectronica.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Resultado de la operación de respaldo local del archivo SQLite (.db).
 */
data class BackupResult(
    val success: Boolean,
    val backupFilePath: String? = null,
    val formattedTimestamp: String = "",
    val message: String
)

/**
 * Utilidad para la exportación y respaldo local de la base de datos SQLite (AppDatabase).
 */
object DatabaseBackupHelper {

    private const val BACKUP_FOLDER_NAME = "Backups"

    /**
     * Copia el archivo principal de SQLite ("bitacora_electronica.db") y sus archivos
     * de registro transaccional (-wal y -shm) al directorio de respaldos de la aplicación.
     */
    suspend fun exportDatabaseFile(context: Context): BackupResult = withContext(Dispatchers.IO) {
        try {
            val dbFile: File = context.getDatabasePath(AppDatabase.DATABASE_NAME)
            if (!dbFile.exists()) {
                return@withContext BackupResult(
                    success = false,
                    message = "Aún no existe el archivo de base de datos local para respaldar."
                )
            }

            val backupDir = File(context.getExternalFilesDir(null), BACKUP_FOLDER_NAME)
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            val fileStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val displayStamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            val destDbFile = File(backupDir, "bitacora_backup_$fileStamp.db")
            dbFile.copyTo(destDbFile, overwrite = true)

            // Copiar archivos auxiliares de Write-Ahead Logging (WAL y SHM) si existen
            val walFile = File(dbFile.path + "-wal")
            if (walFile.exists()) {
                walFile.copyTo(File(destDbFile.path + "-wal"), overwrite = true)
            }

            val shmFile = File(dbFile.path + "-shm")
            if (shmFile.exists()) {
                shmFile.copyTo(File(destDbFile.path + "-shm"), overwrite = true)
            }

            BackupResult(
                success = true,
                backupFilePath = destDbFile.absolutePath,
                formattedTimestamp = displayStamp,
                message = "Respaldo SQLite creado: ${destDbFile.name}"
            )
        } catch (e: Exception) {
            BackupResult(
                success = false,
                message = "Error al exportar la base de datos: ${e.localizedMessage ?: "Error desconocido"}"
            )
        }
    }
}