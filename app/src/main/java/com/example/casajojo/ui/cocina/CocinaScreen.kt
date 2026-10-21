@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.casajojo.ui.cocina

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.casajojo.domain.EstadoPedido
import com.example.casajojo.domain.PedidoVista
import com.example.casajojo.domain.Puntualidad
import com.example.casajojo.domain.estadoEnum
import com.example.casajojo.domain.listaItems
import com.example.casajojo.ui.components.EstadoChip
import com.example.casajojo.ui.components.PuntualidadChip
import com.example.casajojo.ui.components.RestriccionBanner
import com.example.casajojo.ui.components.ScreenScaffold
import com.example.casajojo.ui.theme.ColorATiempo
import com.example.casajojo.ui.theme.ColorAtrasado
import com.example.casajojo.util.Notificaciones
import com.example.casajojo.util.textoRetiro

@Composable
fun CocinaScreen(vm: CocinaViewModel, onVolver: () -> Unit) {
    val pedidos by vm.pedidos.collectAsState()
    val context = LocalContext.current

    ScreenScaffold(titulo = "Cocina", onVolver = onVolver) { padding ->
        if (pedidos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Sin pedidos en cola")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pedidos, key = { it.pedido.id }) { vista ->
                    TarjetaCocina(
                        vista = vista,
                        onAvanzar = {
                            vm.avanzar(vista.pedido) {
                                // Recurso nativo: notificación local cuando el pedido queda listo
                                Notificaciones.pedidoListo(context, vista.pedido.id, vista.pedido.cliente)
                            }
                        },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaCocina(
    vista: PedidoVista,
    onAvanzar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pedido = vista.pedido
    val estado = pedido.estadoEnum()

    // El borde cambia de color con animación según la puntualidad
    val colorBorde by animateColorAsState(
        targetValue = if (vista.puntualidad == Puntualidad.ATRASADO) ColorAtrasado else ColorATiempo,
        label = "borde-cocina"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, colorBorde)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "#${pedido.id} · retiro ${pedido.textoRetiro()}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                EstadoChip(estado)
            }

            PuntualidadChip(vista.puntualidad)

            Text(
                pedido.listaItems().joinToString("\n") { "• $it" },
                style = MaterialTheme.typography.bodyMedium
            )

            AnimatedVisibility(visible = pedido.restricciones.isNotBlank()) {
                RestriccionBanner(pedido.restricciones)
            }

            if (estado == EstadoPedido.RECIBIDO || estado == EstadoPedido.EN_PREPARACION) {
                Button(onClick = onAvanzar, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        if (estado == EstadoPedido.RECIBIDO) "Empezar a preparar"
                        else "Marcar como listo"
                    )
                }
            }
        }
    }
}
