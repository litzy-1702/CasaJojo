package com.example.casajojo.data.repository

import com.example.casajojo.data.local.MenuDao
import com.example.casajojo.data.local.MenuItemEntity
import com.example.casajojo.data.local.PedidoDao
import com.example.casajojo.data.local.PedidoEntity
import com.example.casajojo.domain.Categoria
import com.example.casajojo.domain.EstadoPedido
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Repositorio: única puerta de entrada a los datos.
 * Los ViewModels no conocen Room; solo llaman a estos métodos.
 */
class ComandasRepository(
    private val menuDao: MenuDao,
    private val pedidoDao: PedidoDao
) {
    fun observarMenu(): Flow<List<MenuItemEntity>> = menuDao.observarMenu()

    fun observarPedidos(): Flow<List<PedidoEntity>> = pedidoDao.observarPedidos()

    suspend fun agregarPlato(nombre: String, categoria: Categoria, precio: Int) {
        menuDao.insertar(MenuItemEntity(nombre = nombre, categoria = categoria.name, precio = precio))
    }

    suspend fun alternarDisponible(plato: MenuItemEntity) {
        menuDao.actualizar(plato.copy(disponible = !plato.disponible))
    }

    suspend fun eliminarPlato(plato: MenuItemEntity) {
        menuDao.eliminar(plato)
    }

    suspend fun crearPedido(pedido: PedidoEntity): Long = pedidoDao.insertar(pedido)

    suspend fun cambiarEstado(id: Int, estado: EstadoPedido) {
        pedidoDao.cambiarEstado(id, estado.name)
    }

    /** Carga un menú de ejemplo la primera vez que se abre la app (datos ficticios). */
    suspend fun sembrarSiVacio() {
        if (menuDao.observarMenu().first().isEmpty()) {
            MENU_INICIAL.forEach { (nombre, categoria, precio) ->
                menuDao.insertar(MenuItemEntity(nombre = nombre, categoria = categoria.name, precio = precio))
            }
        }
    }

    companion object {
        private val MENU_INICIAL = listOf(
            Triple("Pollo asado con papas", Categoria.FONDO, 4500),
            Triple("Lentejas con longaniza", Categoria.FONDO, 4200),
            Triple("Cazuela de vacuno", Categoria.FONDO, 4800),
            Triple("Arroz graneado", Categoria.ACOMPANAMIENTO, 1500),
            Triple("Puré de papas", Categoria.ACOMPANAMIENTO, 1500),
            Triple("Ensalada chilena", Categoria.ENSALADA, 1800),
            Triple("Ensalada mixta", Categoria.ENSALADA, 1800),
            Triple("Mousse de chocolate", Categoria.POSTRE, 1900),
            Triple("Arroz con leche", Categoria.POSTRE, 1900),
            Triple("Agua mineral", Categoria.BEBIDA, 1200),
            Triple("Jugo natural", Categoria.BEBIDA, 1500)
        )
    }
}
