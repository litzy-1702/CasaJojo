package com.example.casajojo.ui.cliente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casajojo.data.local.MenuItemEntity
import com.example.casajojo.data.local.PedidoEntity
import com.example.casajojo.data.repository.ComandasRepository
import com.example.casajojo.domain.EstadoPedido
import com.example.casajojo.util.Validadores
import com.example.casajojo.util.horaDeHoy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClienteUiState(
    val platos: List<MenuItemEntity> = emptyList(),   // solo los disponibles
    val seleccion: Set<Int> = emptySet(),             // ids de platos elegidos
    val nombre: String = "",
    val restricciones: String = "",
    val retiroInmediato: Boolean = true,
    val hora: Int? = null,
    val minuto: Int? = null,
    val intento: Boolean = false,                     // true tras el primer "Enviar"
    val errores: Map<String, String> = emptyMap(),
    val ultimoPedido: Int? = null,                    // para mostrar la confirmación
    val historial: List<PedidoEntity> = emptyList()
) {
    val total: Int get() = platos.filter { it.id in seleccion }.sumOf { it.precio }
}

class ClienteViewModel(private val repo: ComandasRepository) : ViewModel() {

    private val formulario = MutableStateFlow(ClienteUiState())

    /**
     * Combina el formulario con el menú y el historial de la base de datos.
     * Los errores se calculan aquí (no en la pantalla): la UI solo los muestra.
     */
    val uiState: StateFlow<ClienteUiState> = combine(
        formulario,
        repo.observarMenu(),
        repo.observarPedidos()
    ) { f, menu, pedidos ->
        val disponibles = menu.filter { it.disponible }
        val ids = disponibles.map { it.id }.toSet()
        val base = f.copy(platos = disponibles, seleccion = f.seleccion.intersect(ids))
        base.copy(
            errores = if (f.intento) validar(base) else emptyMap(),
            historial = pedidos
                .filter { f.nombre.isNotBlank() && it.cliente.equals(f.nombre.trim(), ignoreCase = true) }
                .take(5)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ClienteUiState())

    fun onNombre(valor: String) = formulario.update { it.copy(nombre = valor) }

    fun onRestricciones(valor: String) = formulario.update { it.copy(restricciones = valor) }

    fun onToggleItem(id: Int) = formulario.update { f ->
        f.copy(seleccion = if (id in f.seleccion) f.seleccion - id else f.seleccion + id)
    }

    fun onModo(inmediato: Boolean) = formulario.update { it.copy(retiroInmediato = inmediato) }

    fun onHora(hora: Int, minuto: Int) = formulario.update { it.copy(hora = hora, minuto = minuto) }

    fun limpiarConfirmacion() = formulario.update { it.copy(ultimoPedido = null) }

    fun enviar() {
        val actual = uiState.value
        if (validar(actual).isNotEmpty()) {
            formulario.update { it.copy(intento = true) }
            return
        }

        val ahora = System.currentTimeMillis()
        val pedido = PedidoEntity(
            cliente = actual.nombre.trim(),
            items = actual.platos.filter { it.id in actual.seleccion }.joinToString("\n") { it.nombre },
            total = actual.total,
            restricciones = actual.restricciones.trim(),
            creadoMillis = ahora,
            retiroMillis = if (actual.retiroInmediato) null
            else horaDeHoy(actual.hora ?: 0, actual.minuto ?: 0, ahora),
            estado = EstadoPedido.RECIBIDO.name
        )

        viewModelScope.launch {
            val id = repo.crearPedido(pedido)
            formulario.update {
                it.copy(
                    seleccion = emptySet(),
                    restricciones = "",
                    intento = false,
                    ultimoPedido = id.toInt()
                )
            }
        }
    }

    /** Reglas del formulario de pedido, separadas de la pantalla. */
    private fun validar(e: ClienteUiState): Map<String, String> = buildMap {
        Validadores.nombreCliente(e.nombre)?.let { put("nombre", it) }
        Validadores.restricciones(e.restricciones)?.let { put("restricciones", it) }
        Validadores.seleccion(e.seleccion)?.let { put("seleccion", it) }
        if (!e.retiroInmediato) {
            val h = e.hora
            val m = e.minuto
            val error = if (h == null || m == null) "Elige la hora de retiro"
            else Validadores.horaRetiro(h, m, System.currentTimeMillis())
            error?.let { put("hora", it) }
        }
    }
}
