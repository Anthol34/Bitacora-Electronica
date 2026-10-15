package com.example.bitacoraelectronica.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bitacoraelectronica.data.local.dao.ComponenteDao
import com.example.bitacoraelectronica.data.local.dao.ReparacionComponenteDao
import com.example.bitacoraelectronica.data.local.dao.ReparacionDao
import com.example.bitacoraelectronica.data.local.entity.ComponenteEntity
import com.example.bitacoraelectronica.data.local.entity.ReparacionComponenteEntity
import com.example.bitacoraelectronica.data.local.entity.ReparacionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Database(
    entities = [
        ReparacionEntity::class,
        ComponenteEntity::class,
        ReparacionComponenteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reparacionDao(): ReparacionDao
    abstract fun componenteDao(): ComponenteDao
    abstract fun reparacionComponenteDao(): ReparacionComponenteDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        const val DATABASE_NAME = "bitacora_electronica.db"

        fun getDatabase(
            context: Context,
            scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        ): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope) { getDatabase(context, scope) })
                    .build()
                    .also { Instance = it }
            }
        }
    }
}