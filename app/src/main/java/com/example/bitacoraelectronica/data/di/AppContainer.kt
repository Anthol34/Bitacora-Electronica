package com.example.bitacoraelectronica.data.di

import android.content.Context
import com.example.bitacoraelectronica.data.local.AppDatabase
import com.example.bitacoraelectronica.data.local.datastore.SettingsPreferencesDataSource
import com.example.bitacoraelectronica.data.repository.IComponenteRepository
import com.example.bitacoraelectronica.data.repository.IReparacionRepository
import com.example.bitacoraelectronica.data.repository.ISettingsRepository
import com.example.bitacoraelectronica.data.repository.OfflineComponenteRepository
import com.example.bitacoraelectronica.data.repository.OfflineReparacionRepository
import com.example.bitacoraelectronica.data.repository.OfflineSettingsRepository

/**
 * Contenedor de dependencias que define el contrato de acceso a los repositorios de la aplicación.
 */
interface AppContainer {
    val reparacionRepository: IReparacionRepository
    val componenteRepository: IComponenteRepository
    val settingsRepository: ISettingsRepository
}

/**
 * Implementación concreta del contenedor de dependencias utilizando inyección manual.
 * Provee instancias de repositorios alimentadas por Room y Preferences DataStore.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    private val settingsPreferencesDataSource: SettingsPreferencesDataSource by lazy {
        SettingsPreferencesDataSource(context)
    }

    override val reparacionRepository: IReparacionRepository by lazy {
        OfflineReparacionRepository(
            reparacionDao = database.reparacionDao(),
            componenteDao = database.componenteDao(),
            reparacionComponenteDao = database.reparacionComponenteDao()
        )
    }

    override val componenteRepository: IComponenteRepository by lazy {
        OfflineComponenteRepository(
            componenteDao = database.componenteDao()
        )
    }

    override val settingsRepository: ISettingsRepository by lazy {
        OfflineSettingsRepository(
            settingsPreferencesDataSource = settingsPreferencesDataSource
        )
    }
}