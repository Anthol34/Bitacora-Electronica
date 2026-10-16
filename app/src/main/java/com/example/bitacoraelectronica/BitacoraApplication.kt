package com.example.bitacoraelectronica

import android.app.Application
import com.example.bitacoraelectronica.data.di.AppContainer
import com.example.bitacoraelectronica.data.di.DefaultAppContainer

/**
 * Subclase de Application que inicializa y retiene el contenedor de dependencias manual.
 * Facilita el acceso a los repositorios de datos locales para los ViewModels de la app.
 */
class BitacoraApplication : Application() {

    /** Contenedor de dependencias que centraliza el acceso a la base de datos y preferencias */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
