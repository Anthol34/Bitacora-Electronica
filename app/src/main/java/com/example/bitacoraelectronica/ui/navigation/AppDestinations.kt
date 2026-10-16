package com.example.bitacoraelectronica.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Jerarquía sellada que define todas las rutas y destinos de navegación
 * para la aplicación Bitácora Electrónica.
 */
sealed class AppDestinations(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    // Destinos de autenticación y carga
    data object Splash : AppDestinations(
        route = "splash_screen",
        title = "Splash"
    )

    data object Login : AppDestinations(
        route = "login_screen",
        title = "Iniciar Sesión"
    )

    data object Register : AppDestinations(
        route = "register_screen",
        title = "Registro de Técnico"
    )

    // Destinos principales (visibles en el Navigation Drawer)
    data object Reparaciones : AppDestinations(
        route = "reparaciones_screen",
        title = "Reparaciones",
        icon = Icons.Default.Build
    )

    data object Inventario : AppDestinations(
        route = "inventario_screen",
        title = "Inventario",
        icon = Icons.Default.List
    )

    data object Configuracion : AppDestinations(
        route = "configuracion_screen",
        title = "Configuración",
        icon = Icons.Default.Settings
    )

    // Destinos de detalle y captura nativa de evidencia
    data object DetalleReparacion : AppDestinations(
        route = "detalle_reparacion_screen/{reparacionId}",
        title = "Detalle de Reparación"
    ) {
        const val ARG_REPARACION_ID = "reparacionId"

        fun createRoute(reparacionId: Long = 0L): String {
            return "detalle_reparacion_screen/$reparacionId"
        }
    }

    data object CameraCapture : AppDestinations(
        route = "camera_capture_screen/{reparacionId}",
        title = "Captura de Evidencia"
    ) {
        const val ARG_REPARACION_ID = "reparacionId"

        fun createRoute(reparacionId: Long): String {
            return "camera_capture_screen/$reparacionId"
        }
    }

    companion object {
        // Colección de destinos que se mostrarán en el menú lateral de Material 3
        val drawerDestinations = listOf(
            Reparaciones,
            Inventario,
            Configuracion
        )
    }
}