package com.example.casajojo.util

/**
 * Reglas de validación en Kotlin puro (sin Compose).
 * Devuelven el mensaje de error, o null si el valor es válido.
 * Así la lógica queda separada de la interfaz y se puede probar sola.
 */
object Validadores {

    private val SOLO_LETRAS = Regex("[\\p{L} ]+")

    fun nombreCliente(valor: String): String? {
        val limpio = valor.trim()
        return when {
            limpio.isEmpty() -> "Ingresa tu nombre"
            limpio.length < 3 -> "Mínimo 3 caracteres"
            limpio.length > 40 -> "Máximo 40 caracteres"
            !SOLO_LETRAS.matches(limpio) -> "Usa solo letras y espacios"
            else -> null
        }
    }

    fun restricciones(valor: String): String? =
        if (valor.length > 120) "Máximo 120 caracteres" else null

    fun nombrePlato(valor: String): String? = when {
        valor.isBlank() -> "Ingresa el nombre del plato"
        valor.trim().length > 40 -> "Máximo 40 caracteres"
        else -> null
    }

    fun precio(valor: String): String? {
        val numero = valor.toIntOrNull() ?: return "Ingresa un número entero"
        return when {
            numero <= 0 -> "El precio debe ser mayor a 0"
            numero > 99_999 -> "Precio demasiado alto"
            else -> null
        }
    }

    fun seleccion(ids: Set<Int>): String? =
        if (ids.isEmpty()) "Elige al menos un plato" else null

    fun horaRetiro(hora: Int, minuto: Int, ahora: Long): String? =
        if (horaDeHoy(hora, minuto, ahora) <= ahora) "La hora debe ser posterior a la actual" else null
}
