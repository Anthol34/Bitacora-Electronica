package com.example.bitacoraelectronica.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
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
import com.example.bitacoraelectronica.ui.screens.LoginScreen
import com.example.bitacoraelectronica.ui.screens.RegisterScreen
import com.example.bitacoraelectronica.ui.screens.SplashScreen
import com.example.bitacoraelectronica.ui.components.AppDrawerContent
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Observador de la ruta actual para actualizar el ítem activo del menú
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Ocultar el menú lateral en pantallas de inicio, autenticación o cámara
    val showDrawer = currentRoute != null &&
            !currentRoute.startsWith(AppDestinations.Splash.route) &&
            !currentRoute.startsWith(AppDestinations.Login.route) &&
            !currentRoute.startsWith(AppDestinations.Register.route) &&
            !currentRoute.startsWith("camera_capture_screen")

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
    }
        Scaffold(
            modifier = modifier.fillMaxSize()
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
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
                }

                composable(route = AppDestinations.Inventario.route) {
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
                }

                composable(route = AppDestinations.Configuracion.route) {
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
                }

                composable(
                    route = AppDestinations.DetalleReparacion.route,
                    arguments = listOf(
                        navArgument(AppDestinations.DetalleReparacion.ARG_REPARACION_ID) {
                            type = NavType.LongType
                            defaultValue = 0L
                        }
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
                }

                composable(
                    route = AppDestinations.CameraCapture.route,
                    arguments = listOf(
                        navArgument(AppDestinations.CameraCapture.ARG_REPARACION_ID) {
                            type = NavType.LongType
                            defaultValue = 0L
                        }
                    )
                ) {

                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
                }
            }
        }
    }