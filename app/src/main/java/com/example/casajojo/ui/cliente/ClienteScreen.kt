@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.casajojo.ui.cliente

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.casajojo.data.local.PedidoEntity
import com.example.casajojo.domain.Categoria
import com.example.casajojo.domain.estadoEnum
import com.example.casajojo.domain.listaItems
import com.example.casajojo.ui.components.CampoValidado
import com.example.casajojo.ui.components.ErrorTexto
import com.example.casajojo.ui.components.RestriccionBanner
import com.example.casajojo.ui.components.ScreenScaffold
import com.example.casajojo.util.aHora
import com.example.casajojo.util.pesos
import com.example.casajojo.util.textoRetiro
import java.util.Calendar

@Composable
fun ClienteScreen(vm: ClienteViewModel, onVolver: () -> Unit) {
    val e by vm.uiState.collectAsState()
    val context = LocalContext.current

    ScreenScaffold(titulo = "Haz tu pedido", onVolver = onVolver) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AnimatedVisibility(visible = e.ultimoPedido != null) {
                    ConfirmacionPedido(id = e.ultimoPedido ?: 0, onCerrar = { vm.limpiarConfirmacion() })
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Tus datos", style = MaterialTheme.typography.titleMedium)
                        CampoValidado(
                            valor = e.nombre,
                            onCambio = { vm.onNombre(it) },
                            etiqueta = "Nombre",
                            error = e.errores["nombre"]
                        )
                        CampoValidado(
                            valor = e.restricciones,
                            onCambio = { vm.onRestricciones(it) },
                            etiqueta = "Restricciones alimentarias (opcional)",
                            error = e.errores["restricciones"],
                            lineas = 2
                        )
                    }
                }
            }

            item {
                Text("Menú del día", style = MaterialTheme.typography.titleMedium)
            }

            Categoria.entries.forEach { categoria ->
                val platos = e.platos.filter { it.categoria == categoria.name }
                if (platos.isNotEmpty()) {
                    item {
                        Text(
                            categoria.etiqueta,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(platos, key = { it.id }) { plato ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { vm.onToggleItem(plato.id) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = plato.id in e.seleccion,
                                onCheckedChange = { vm.onToggleItem(plato.id) }
                            )
                            Text(plato.nombre, modifier = Modifier.weight(1f))
                            Text(plato.precio.pesos())
                        }
                    }
                }
            }

            e.errores["seleccion"]?.let { msg -> item { ErrorTexto(msg) } }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("¿Cuándo retiras?", style = MaterialTheme.typography.titleMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = e.retiroInmediato,
                                onClick = { vm.onModo(true) },
                                label = { Text("Lo antes posible") }
                            )
                            FilterChip(
                                selected = !e.retiroInmediato,
                                onClick = { vm.onModo(false) },
                                label = { Text("Hora específica") }
                            )
                        }
                        AnimatedVisibility(visible = !e.retiroInmediato) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = {
                                    // Recurso nativo: selector de hora del sistema Android
                                    abrirSelectorHora(context, e.hora, e.minuto) { h, m -> vm.onHora(h, m) }
                                }) {
                                    Icon(Icons.Filled.Schedule, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(textoHora(e.hora, e.minuto))
                                }
                                e.errores["hora"]?.let { ErrorTexto(it) }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total", style = MaterialTheme.typography.titleMedium)
                        AnimatedContent(targetState = e.total, label = "total") { total ->
                            Text(total.pesos(), style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
            }

            item {
                Button(onClick = { vm.enviar() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Enviar pedido a Casa JoJo")
                }
            }

            if (e.historial.isNotEmpty()) {
                item {
                    Text("Tus pedidos recientes", style = MaterialTheme.typography.titleMedium)
                }
                items(e.historial, key = { it.id }) { pedido ->
                    HistorialCard(pedido)
                }
            }
        }
    }
}

/** Abre el selector de hora nativo de Android y devuelve la hora elegida. */
private fun abrirSelectorHora(context: Context, hora: Int?, minuto: Int?, alElegir: (Int, Int) -> Unit) {
    val ahora = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, h, m -> alElegir(h, m) },
        hora ?: ahora.get(Calendar.HOUR_OF_DAY),
        minuto ?: ahora.get(Calendar.MINUTE),
        true
    ).show()
}

private fun textoHora(hora: Int?, minuto: Int?): String =
    if (hora != null && minuto != null) "Retiro a las %02d:%02d".format(hora, minuto)
    else "Elegir hora de retiro"

@Composable
private fun ConfirmacionPedido(id: Int, onCerrar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDFF5E1))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("¡Pedido #$id recibido!", style = MaterialTheme.typography.titleMedium)
                Text("Te avisaremos cuando esté listo.", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onCerrar) {
                Icon(Icons.Filled.Close, contentDescription = "Cerrar")
            }
        }
    }
}

@Composable
private fun HistorialCard(pedido: PedidoEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "Pedido #${pedido.id} · ${pedido.estadoEnum().etiqueta}",
                style = MaterialTheme.typography.labelLarge
            )
            Text(pedido.listaItems().joinToString(", "), style = MaterialTheme.typography.bodyMedium)
            if (pedido.restricciones.isNotBlank()) {
                RestriccionBanner(pedido.restricciones)
            }
            Text(
                "Pedido a las ${pedido.creadoMillis.aHora()} · Retiro ${pedido.textoRetiro()} · Total ${pedido.total.pesos()}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
