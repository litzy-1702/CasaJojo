package com.example.casajojo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores de marca (placeholder). Cuando envíen el logo, cambiamos estos valores.
private val Colores = lightColorScheme(
    primary = Color(0xFFB8442B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD2),
    onPrimaryContainer = Color(0xFF3B0A00),
    secondary = Color(0xFF6B5E2E),
    tertiary = Color(0xFF3D6A4A),
    background = Color(0xFFFFF8F5),
    surface = Color(0xFFFFF8F5)
)

// Colores de estado usados por cocina y caja
val ColorATiempo = Color(0xFF2E7D32)
val ColorAtrasado = Color(0xFFC62828)
val ColorEnPreparacion = Color(0xFFEF6C00)

@Composable
fun CasaJojoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Colores,
        typography = Typography(),
        content = content
    )
}
