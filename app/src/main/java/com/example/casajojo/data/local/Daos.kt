package com.example.casajojo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    /** Flow: Room vuelve a emitir la lista cada vez que la tabla cambia. */
    @Query("SELECT * FROM menu_items ORDER BY categoria, nombre")
    fun observarMenu(): Flow<List<MenuItemEntity>>

    @Insert
    suspend fun insertar(item: MenuItemEntity): Long

    @Update
    suspend fun actualizar(item: MenuItemEntity)

    @Delete
    suspend fun eliminar(item: MenuItemEntity)
}

@Dao
interface PedidoDao {
    @Query("SELECT * FROM pedidos ORDER BY creadoMillis DESC")
    fun observarPedidos(): Flow<List<PedidoEntity>>

    @Insert
    suspend fun insertar(pedido: PedidoEntity): Long

    @Query("UPDATE pedidos SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Int, estado: String)
}
