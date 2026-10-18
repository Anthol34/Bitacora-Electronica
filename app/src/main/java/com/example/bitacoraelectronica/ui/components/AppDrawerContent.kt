package com.example.bitacoraelectronica.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.bitacoraelectronica.ui.navigation.AppDestinations

@Composable
fun AppDrawerContent(
    currentRoute: String?,
    onNavigateToDestination: (AppDestinations) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        // Encabezado visual del Drawer (inspirado en el prototipo de alta fidelidad)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = "Emblema del taller",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Bitácora",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Electrónica",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Taller de reparaciones & banco de pruebas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de destinos principales del Navigation Drawer
        AppDestinations.drawerDestinations.filterNotNull().forEach { destination ->
            val isSelected = currentRoute == destination.route
            NavigationDrawerItem(
                label = {
                    Text(
                        text = destination.title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                selected = isSelected,
                onClick = { onNavigateToDestination(destination) },
                icon = destination.icon?.let { iconVector ->
                    {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = "Ir a la sección de ${destination.title}"
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(modifier = Modifier.height(8.dp))

        // Ítem estático de información del sistema
        NavigationDrawerItem(
            label = {
                Text(
                    text = "Acerca de la App",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            badge = {
                Text(
                    text = "v1.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            },
            selected = false,
            onClick = { /* Opcional: Diálogo de información ULEAM */ },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Información sobre el proyecto"
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}