package com.example.casajojo.ui.cocina

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casajojo.data.local.PedidoEntity
import com.example.casajojo.data.repository.ComandasRepository
import com.example.casajojo.domain.EstadoPedido
import com.example.casajojo.domain.PedidoVista
import com.example.casajojo.domain.activos
import com.example.casajojo.domain.estadoEnum
import com.example.casajojo.util.reloj
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CocinaViewModel(private val repo: ComandasRepository) : ViewModel() {

    /** Pedidos en cola, ordenados por hora de retiro, con semáforo de puntualidad. */
    val pedidos: StateFlow<List<PedidoVista>> = combine(repo.observarPedidos(), reloj()) { lista, ahora ->
        lista.activos(ahora)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Avanza el estado: RECIBIDO -> EN_PREPARACION -> LISTO.
     * Al quedar LISTO se llama onListo (la pantalla dispara la notificación).
     */
    fun avanzar(pedido: PedidoEntity, onListo: () -> Unit) {
        viewModelScope.launch {
            when (pedido.estadoEnum()) {
                EstadoPedido.RECIBIDO -> repo.cambiarEstado(pedido.id, EstadoPedido.EN_PREPARACION)
                EstadoPedido.EN_PREPARACION -> {
                    repo.cambiarEstado(pedido.id, EstadoPedido.LISTO)
                    onListo()
                }
                else -> Unit
            }
        }
    }
}
