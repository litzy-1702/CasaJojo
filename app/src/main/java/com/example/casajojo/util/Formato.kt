package com.example.casajojo.util

import com.example.casajojo.data.local.MenuItemEntity
import com.example.casajojo.data.local.PedidoEntity
import com.example.casajojo.domain.Categoria
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val LOCALE_CL: Locale = Locale.forLanguageTag("es-CL")

/** 1718000000000 -> "13:30" */
fun Long.aHora(): String = SimpleDateFormat("HH:mm", LOCALE_CL).format(Date(this))

/** 4500 -> "$4.500" */
fun Int.pesos(): String = "\$" + String.format(LOCALE_CL, "%,d", this)

/** Convierte hora y minuto de hoy a milisegundos. */
fun horaDeHoy(hora: Int, minuto: Int, ahora: Long): Long =
    Calendar.getInstance().apply {
        timeInMillis = ahora
        set(Calendar.HOUR_OF_DAY, hora)
        set(Calendar.MINUTE, minuto)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

fun PedidoEntity.textoRetiro(): String =
    retiroMillis?.let { "a las ${it.aHora()}" } ?: "lo antes posible"

/** Texto del menú del día listo para copiar y enviar por WhatsApp. */
fun textoMenu(platos: List<MenuItemEntity>): String = buildString {
    appendLine("🍽️ Menú del día - Casa JoJo")
    Categoria.entries.forEach { cat ->
        val lista = platos.filter { it.categoria == cat.name && it.disponible }
        if (lista.isNotEmpty()) {
            appendLine()
            appendLine("*${cat.etiqueta}*")
            lista.forEach { appendLine("- ${it.nombre} (${it.precio.pesos()})") }
        }
    }
    appendLine()
    append("Responde con tu pedido y la hora de retiro.")
}

/** Emite la hora actual cada 30 segundos: sirve para refrescar el semáforo de puntualidad. */
fun reloj(intervaloMs: Long = 30_000L): Flow<Long> = flow {
    while (true) {
        emit(System.currentTimeMillis())
        delay(intervaloMs)
    }
}
