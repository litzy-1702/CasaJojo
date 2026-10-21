package com.example.casajojo.ui.inicio

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.casajojo.ui.navigation.Ruta

private data class Opcion(
    val ruta: String,
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector
)

private val OPCIONES = listOf(
    Opcion(Ruta.CLIENTE, "Hacer pedido", "Arma tu menú del día y elige la hora de retiro", Icons.Filled.ShoppingCart),
    Opcion(Ruta.CAJA, "Caja", "Pedidos, horario y total a cobrar", Icons.Filled.Payments),
    Opcion(Ruta.COCINA, "Cocina", "Preparación, restricciones y puntualidad", Icons.Filled.Restaurant),
    Opcion(Ruta.ADMIN, "Administración", "Menú diario y disponibilidad", Icons.Filled.Settings)
)

@Composable
fun InicioScreen(onElegir: (String) -> Unit) {
    // Pedir permiso de notificaciones (Android 13+). Si el usuario responde no, la app sigue funcionando.
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -it / 2 }
        ) {
            Column {
                Text("Casa JoJo", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
                Text("Gestión digital de comandas", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(24.dp))
            }
        }

        OPCIONES.forEachIndexed { indice, opcion ->
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 120 * indice)) +
                        slideInVertically(tween(400, delayMillis = 120 * indice)) { it / 2 }
            ) {
                Card(
                    onClick = { onElegir(opcion.ruta) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(opcion.icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(opcion.titulo, style = MaterialTheme.typography.titleMedium)
                            Text(opcion.descripcion, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
