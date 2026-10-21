@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.casajojo.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.casajojo.data.local.MenuItemEntity
import com.example.casajojo.domain.Categoria
import com.example.casajojo.ui.components.CampoValidado
import com.example.casajojo.ui.components.ScreenScaffold
import com.example.casajojo.util.Notificaciones
import com.example.casajojo.util.pesos

@Composable
fun AdminScreen(vm: AdminViewModel, onVolver: () -> Unit) {
    val estado by vm.uiState.collectAsState()
    val context = LocalContext.current

    ScreenScaffold(
        titulo = "Administración del menú",
        onVolver = onVolver,
        acciones = {
            IconButton(onClick = { Notificaciones.compartirMenu(context, vm.textoParaCompartir()) }) {
                Icon(Icons.Filled.Share, contentDescription = "Compartir menú del día")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { FormularioPlato(estado, vm) }

            item {
                Text("Platos del menú", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            }

            Categoria.entries.forEach { categoria ->
                val platos = estado.platos.filter { it.categoria == categoria.name }
                if (platos.isNotEmpty()) {
                    item {
                        Text(
                            categoria.etiqueta,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(platos, key = { it.id }) { plato ->
                        FilaPlato(
                            plato = plato,
                            onToggle = { vm.alternarDisponible(plato) },
                            onEliminar = { vm.eliminar(plato) },
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormularioPlato(estado: AdminUiState, vm: AdminViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Agregar plato", style = MaterialTheme.typography.titleMedium)

            CampoValidado(
                valor = estado.nombre,
                onCambio = { vm.onNombre(it) },
                etiqueta = "Nombre del plato",
                error = estado.errorNombre
            )
            CampoValidado(
                valor = estado.precio,
                onCambio = { vm.onPrecio(it) },
                etiqueta = "Precio (CLP)",
                error = estado.errorPrecio,
                teclado = KeyboardType.Number
            )

            Text("Categoría", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Categoria.entries.forEach { categoria ->
                    FilterChip(
                        selected = estado.categoria == categoria,
                        onClick = { vm.onCategoria(categoria) },
                        label = { Text(categoria.etiqueta) }
                    )
                }
            }

            Button(onClick = { vm.guardar() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Agregar al menú")
            }

            AnimatedVisibility(visible = estado.mensaje != null) {
                Text(
                    estado.mensaje ?: "",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun FilaPlato(
    plato: MenuItemEntity,
    onToggle: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plato.nombre, style = MaterialTheme.typography.bodyLarge)
                Text(plato.precio.pesos(), style = MaterialTheme.typography.bodySmall)
            }
            Text(if (plato.disponible) "Disponible" else "Agotado", style = MaterialTheme.typography.labelSmall)
            Switch(checked = plato.disponible, onCheckedChange = { onToggle() })
            IconButton(onClick = onEliminar) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar plato")
            }
        }
    }
}
