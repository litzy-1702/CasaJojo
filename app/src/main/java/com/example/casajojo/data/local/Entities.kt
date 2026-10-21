package com.example.casajojo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Tabla de platos del menú diario. */
@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val categoria: String,      // nombre del enum Categoria (FONDO, POSTRE...)
    val precio: Int,            // pesos chilenos, sin decimales
    val disponible: Boolean = true
)

/** Tabla de pedidos (comandas). */
@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cliente: String,
    val items: String,          // nombres de los platos separados por salto de línea
    val total: Int,
    val restricciones: String,  // texto libre, puede estar vacío
    val creadoMillis: Long,     // momento en que se hizo el pedido
    val retiroMillis: Long?,    // null = "lo antes posible"
    val estado: String          // nombre del enum EstadoPedido
)
