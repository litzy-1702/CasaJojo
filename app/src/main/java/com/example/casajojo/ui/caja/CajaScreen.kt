@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.casajojo.ui.caja

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.casajojo.domain.EstadoPedido
import com.example.casajojo.domain.PedidoVista
import com.example.casajojo.domain.estadoEnum
import com.example.casajojo.domain.listaItems
import com.example.casajojo.ui.components.EstadoChip
import com.example.casajojo.ui.components.RestriccionBanner
import com.example.casajojo.ui.components.ScreenScaffold
import com.example.casajojo.util.aHora
import com.example.casajojo.util.pesos
import com.example.casajojo.util.textoRetiro

@Composable
fun CajaScreen(vm: CajaViewModel, onVolver: () -> Unit) {
    val pedidos by vm.pedidos.collectAsState()

    ScreenScaffold(titulo = "Caja · pedidos", onVolver = onVolver) { padding ->
        if (pedidos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay pedidos pendientes")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pedidos, key = { it.pedido.id }) { vista ->
                    TarjetaCaja(
                        vista = vista,
                        onEntregado = { vm.marcarEntregado(vista.pedido.id) },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaCaja(
    vista: PedidoVista,
    onEntregado: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pedido = vista.pedido
    val estado = pedido.estadoEnum()

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "#${pedido.id} · ${pedido.cliente}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                EstadoChip(estado)
            }
            Text(
                "Pedido: ${pedido.creadoMillis.aHora()} · Retiro: ${pedido.textoRetiro()}",
                style = MaterialTheme.typography.bodySmall
            )
            HorizontalDivider()
            Text(pedido.listaItems().joinToString("\n") { "• $it" }, style = MaterialTheme.typography.bodyMedium)
            if (pedido.restricciones.isNotBlank()) {
                RestriccionBanner(pedido.restricciones)
            }
            HorizontalDivider()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Total a cobrar: ${pedido.total.pesos()}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                // Solo se puede entregar cuando cocina lo marcó como listo
                Button(
                    onClick = onEntregado,
                    enabled = estado == EstadoPedido.LISTO
                ) {
                    Text("Entregado")
                }
            }
        }
    }
}
