package com.example.bitacoraelectronica.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bitacoraelectronica.ui.components.AppDrawerContent
import com.example.bitacoraelectronica.ui.components.BitacoraTopAppBar
import com.example.bitacoraelectronica.ui.screens.CameraCaptureScreen
import com.example.bitacoraelectronica.ui.screens.ConfiguracionScreen
import com.example.bitacoraelectronica.ui.screens.DetalleReparacionScreen
import com.example.bitacoraelectronica.ui.screens.InventarioScreen
import com.example.bitacoraelectronica.ui.screens.LoginScreen
import com.example.bitacoraelectronica.ui.screens.NuevoRegistroScreen
import com.example.bitacoraelectronica.ui.screens.RegisterScreen
import com.example.bitacoraelectronica.ui.screens.ReparacionesScreen
import com.example.bitacoraelectronica.ui.screens.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Mostrar el Drawer únicamente en las pantallas principales del menú lateral
    val showDrawer = currentRoute == AppDestinations.Reparaciones.route ||
            currentRoute == AppDestinations.Inventario.route ||
            currentRoute == AppDestinations.Configuracion.route

    // Determina el título actual de la barra superior
    val currentTitle = when (currentRoute) {
        AppDestinations.Reparaciones.route -> AppDestinations.Reparaciones.title
        AppDestinations.Inventario.route -> AppDestinations.Inventario.title
        AppDestinations.Configuracion.route -> AppDestinations.Configuracion.title
        else -> ""
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showDrawer,
        drawerContent = {
            if (showDrawer) {
                AppDrawerContent(
                    currentRoute = currentRoute,
                    onNavigateToDestination = { destination ->
                        coroutineScope.launch { drawerState.close() }
                        if (currentRoute != destination.route) {
                            navController.navigate(destination.route) {
                                popUpTo(AppDestinations.Reparaciones.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                // Solo mostramos BitacoraTopAppBar en las vistas que no tienen su propio TopAppBar interno
                if (currentRoute == AppDestinations.Reparaciones.route ||
                    currentRoute == AppDestinations.Inventario.route
                ) {
                    BitacoraTopAppBar(
                        title = currentTitle,
                        canNavigateBack = false,
                        onNavigationClick = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        actions = {
                            IconButton(onClick = { /* Búsqueda local */ }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar registro"
                                )
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppDestinations.Splash.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = AppDestinations.Splash.route) {
                    SplashScreen(navController = navController)
                }

                composable(route = AppDestinations.Login.route) {
                    LoginScreen(navController = navController)
                }

                composable(route = AppDestinations.Register.route) {
                    RegisterScreen(navController = navController)
                }

                composable(route = AppDestinations.Reparaciones.route) {
                    ReparacionesScreen(
                        onNavigateToNuevoRegistro = {
                            navController.navigate(AppDestinations.NuevoRegistro.route)
                        },
                        onNavigateToDetalle = { reparacionId ->
                            navController.navigate(
                                AppDestinations.DetalleReparacion.createRoute(reparacionId)
                            )
                        }
                    )
                }

                composable(route = AppDestinations.NuevoRegistro.route) {
                    NuevoRegistroScreen(
                        onBackClick = { navController.popBackStack() },
                        onRegistroGuardado = { navController.popBackStack() }
                    )
                }

                composable(route = AppDestinations.Inventario.route) {
                    InventarioScreen()
                }

                composable(route = AppDestinations.Configuracion.route) {
                    ConfiguracionScreen(
                        onMenuClick = { coroutineScope.launch { drawerState.open() } }
                    )
                }

                composable(
                    route = AppDestinations.DetalleReparacion.route,
                    arguments = listOf(
                        navArgument(AppDestinations.DetalleReparacion.ARG_REPARACION_ID) {
                            type = NavType.LongType
                            nullable = false
                        }
                    )
                ) { backStackEntry ->
                    val reparacionId = backStackEntry.arguments
                        ?.getLong(AppDestinations.DetalleReparacion.ARG_REPARACION_ID) ?: 0L

                    DetalleReparacionScreen(
                        reparacionId = reparacionId,
                        onBackClick = { navController.popBackStack() },
                        onTomarFotoClick = { id ->
                            navController.navigate(AppDestinations.CameraCapture.createRoute(id))
                        }
                    )
                }
                composable(
                    route = AppDestinations.CameraCapture.route,
                    arguments = listOf(
                        navArgument(AppDestinations.CameraCapture.ARG_REPARACION_ID) {
                            type = NavType.LongType
                            nullable = false
                        }
                    )
                ) { backStackEntry ->
                    val reparacionId = backStackEntry.arguments
                        ?.getLong(AppDestinations.CameraCapture.ARG_REPARACION_ID) ?: 0L

                    CameraCaptureScreen(
                        reparacionId = reparacionId,
                        onPhotoCaptured = { uri ->
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("captured_photo_uri", uri.toString())
                            navController.popBackStack()
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}