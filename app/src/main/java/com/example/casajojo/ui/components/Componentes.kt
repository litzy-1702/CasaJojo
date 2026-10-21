@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.casajojo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.casajojo.domain.EstadoPedido
import com.example.casajojo.domain.Puntualidad
import com.example.casajojo.ui.theme.ColorATiempo
import com.example.casajojo.ui.theme.ColorAtrasado
import com.example.casajojo.ui.theme.ColorEnPreparacion

/** Estructura común de pantalla: barra superior con botón volver y contenido. */
@Composable
fun ScreenScaffold(
    titulo: String,
    onVolver: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {},
    contenido: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    if (onVolver != null) {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = acciones
            )
        },
        content = contenido
    )
}

/**
 * Campo de texto con retroalimentación visual: borde rojo, ícono de error
 * y mensaje debajo. La validación llega ya calculada desde el ViewModel.
 */
@Composable
fun CampoValidado(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?,
    modifier: Modifier = Modifier,
    teclado: KeyboardType = KeyboardType.Text,
    lineas: Int = 1
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        trailingIcon = {
            if (error != null) {
                Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        singleLine = lineas == 1,
        maxLines = lineas,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun ErrorTexto(mensaje: String) {
    Text(mensaje, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
}

@Composable
fun EstadoChip(estado: EstadoPedido) {
    val color = when (estado) {
        EstadoPedido.RECIBIDO -> MaterialTheme.colorScheme.secondary
        EstadoPedido.EN_PREPARACION -> ColorEnPreparacion
        EstadoPedido.LISTO -> ColorATiempo
        EstadoPedido.ENTREGADO -> MaterialTheme.colorScheme.outline
    }
    Surface(color = color, contentColor = Color.White, shape = RoundedCornerShape(50)) {
        Text(
            estado.etiqueta,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/** Semáforo de cocina: verde "A tiempo", rojo "Atrasado" con latido animado. */
@Composable
fun PuntualidadChip(puntualidad: Puntualidad) {
    val atrasado = puntualidad == Puntualidad.ATRASADO
    val color by animateColorAsState(
        targetValue = if (atrasado) ColorAtrasado else ColorATiempo,
        label = "color-puntualidad"
    )
    val infinito = rememberInfiniteTransition(label = "latido")
    val latido by infinito.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(animation = tween(700), repeatMode = RepeatMode.Reverse),
        label = "escala-latido"
    )
    Surface(
        color = color,
        contentColor = Color.White,
        shape = RoundedCornerShape(50),
        modifier = Modifier.scale(if (atrasado) latido else 1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (atrasado) Icons.Filled.Warning else Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                if (atrasado) "Atrasado" else "A tiempo",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

/** Aviso destacado de restricción alimentaria (se muestra en cocina, caja y cliente). */
@Composable
fun RestriccionBanner(texto: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
            Spacer(Modifier.width(8.dp))
            Text("Restricción: $texto", color = MaterialTheme.colorScheme.onErrorContainer)
        }
    }
}
