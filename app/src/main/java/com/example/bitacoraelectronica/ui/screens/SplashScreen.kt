package com.example.bitacoraelectronica.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bitacoraelectronica.ui.navigation.AppDestinations
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Transición controlada hacia la pantalla de autenticación
    LaunchedEffect(key1 = true) {
        delay(2000L)
        navController.navigate(AppDestinations.Login.route) {
            popUpTo(AppDestinations.Splash.route) {
                inclusive = true
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Solamente el logo vectorial del microchip
            LogoChipVectorial(
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bitácora Electrónica",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Taller de Mantenimiento & Reparaciones",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        }
    }
}

@Composable
fun LogoChipVectorial(modifier: Modifier = Modifier) {
    val verdeRecuadro = Color(0xFF3BA770)
    val verdeTrazo = Color(0xFF06371F)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(verdeRecuadro),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize(0.6f)) {
            val w = size.width
            val h = size.height
            val stroke = w * 0.08f

            // Cuadrado exterior del chip
            val bodyInset = w * 0.20f
            val bodySize = w - (bodyInset * 2)
            drawRect(
                color = verdeTrazo,
                topLeft = Offset(bodyInset, bodyInset),
                size = Size(bodySize, bodySize),
                style = Stroke(width = stroke)
            )

            // Núcleo interno del chip
            val coreInset = w * 0.38f
            val coreSize = w - (coreInset * 2)
            drawRect(
                color = verdeTrazo,
                topLeft = Offset(coreInset, coreInset),
                size = Size(coreSize, coreSize),
                style = Stroke(width = stroke * 0.85f)
            )

            // 3 pines por cada lado
            val pinPositions = listOf(0.32f, 0.50f, 0.68f)
            val pinLength = bodyInset * 0.75f

            pinPositions.forEach { pos ->
                val coord = w * pos
                // Arriba
                drawLine(
                    color = verdeTrazo,
                    start = Offset(coord, bodyInset - pinLength),
                    end = Offset(coord, bodyInset),
                    strokeWidth = stroke
                )
                // Abajo
                drawLine(
                    color = verdeTrazo,
                    start = Offset(coord, h - bodyInset),
                    end = Offset(coord, h - bodyInset + pinLength),
                    strokeWidth = stroke
                )
                // Izquierda
                drawLine(
                    color = verdeTrazo,
                    start = Offset(bodyInset - pinLength, coord),
                    end = Offset(bodyInset, coord),
                    strokeWidth = stroke
                )
                // Derecha
                drawLine(
                    color = verdeTrazo,
                    start = Offset(w - bodyInset, coord),
                    end = Offset(w - bodyInset + pinLength, coord),
                    strokeWidth = stroke
                )
            }
        }
    }
}