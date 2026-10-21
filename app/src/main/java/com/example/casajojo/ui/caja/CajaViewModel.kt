package com.example.casajojo.ui.caja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casajojo.data.repository.ComandasRepository
import com.example.casajojo.domain.EstadoPedido
import com.example.casajojo.domain.PedidoVista
import com.example.casajojo.domain.activos
import com.example.casajojo.util.reloj
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CajaViewModel(private val repo: ComandasRepository) : ViewModel() {

    /** Pedidos pendientes de entrega, con su estado de puntualidad. */
    val pedidos: StateFlow<List<PedidoVista>> = combine(repo.observarPedidos(), reloj()) { lista, ahora ->
        lista.activos(ahora)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun marcarEntregado(id: Int) {
        viewModelScope.launch { repo.cambiarEstado(id, EstadoPedido.ENTREGADO) }
    }
}
