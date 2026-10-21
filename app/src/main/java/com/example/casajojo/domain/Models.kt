package com.example.casajojo.domain

import com.example.casajojo.data.local.PedidoEntity

/** Secciones del menú del día. */
enum class Categoria(val etiqueta: String) {
    FONDO("Plato de fondo"),
    ACOMPANAMIENTO("Acompañamiento"),
    ENSALADA("Ensalada"),
    POSTRE("Postre"),
    BEBIDA("Bebida")
}

/** Ciclo de vida de un pedido: RECIBIDO -> EN_PREPARACION -> LISTO -> ENTREGADO. */
enum class EstadoPedido(val etiqueta: String) {
    RECIBIDO("Recibido"),
    EN_PREPARACION("En preparación"),
    LISTO("Listo para retiro"),
    ENTREGADO("Entregado");

    companion object {
        fun desde(nombre: String): EstadoPedido =
            entries.firstOrNull { it.name == nombre } ?: RECIBIDO
    }
}

/** Semáforo de puntualidad que ve cocina. */
enum class Puntualidad { A_TIEMPO, ATRASADO }

/** Para pedidos "lo antes posible", se espera que estén listos en 30 minutos. */
const val MINUTOS_PLAZO_INMEDIATO = 30

fun PedidoEntity.estadoEnum(): EstadoPedido = EstadoPedido.desde(estado)

fun PedidoEntity.listaItems(): List<String> =
    items.split("\n").filter { it.isNotBlank() }

/** Momento en que el pedido debe estar listo. */
fun PedidoEntity.referenciaMillis(): Long =
    retiroMillis ?: (creadoMillis + MINUTOS_PLAZO_INMEDIATO * 60_000L)

fun PedidoEntity.puntualidad(ahora: Long): Puntualidad {
    val terminado = estadoEnum() == EstadoPedido.LISTO || estadoEnum() == EstadoPedido.ENTREGADO
    return if (terminado || ahora <= referenciaMillis()) Puntualidad.A_TIEMPO else Puntualidad.ATRASADO
}

/** Pedido + su semáforo, listo para mostrar en pantalla. */
data class PedidoVista(val pedido: PedidoEntity, val puntualidad: Puntualidad)

/** Pedidos que aún no se entregan, ordenados por hora de retiro. */
fun List<PedidoEntity>.activos(ahora: Long): List<PedidoVista> =
    filter { it.estadoEnum() != EstadoPedido.ENTREGADO }
        .sortedBy { it.referenciaMillis() }
        .map { PedidoVista(it, it.puntualidad(ahora)) }
