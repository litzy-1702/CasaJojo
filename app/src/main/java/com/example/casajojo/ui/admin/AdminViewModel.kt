package com.example.casajojo.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casajojo.data.local.MenuItemEntity
import com.example.casajojo.data.repository.ComandasRepository
import com.example.casajojo.domain.Categoria
import com.example.casajojo.util.Validadores
import com.example.casajojo.util.textoMenu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val platos: List<MenuItemEntity> = emptyList(),
    val nombre: String = "",
    val precio: String = "",
    val categoria: Categoria = Categoria.FONDO,
    val errorNombre: String? = null,
    val errorPrecio: String? = null,
    val mensaje: String? = null
)

class AdminViewModel(private val repo: ComandasRepository) : ViewModel() {

    private val formulario = MutableStateFlow(AdminUiState())

    /** Une la lista de platos de la base de datos con el estado del formulario. */
    val uiState: StateFlow<AdminUiState> = combine(repo.observarMenu(), formulario) { platos, f ->
        f.copy(platos = platos)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AdminUiState())

    fun onNombre(valor: String) {
        formulario.update { it.copy(nombre = valor, errorNombre = Validadores.nombrePlato(valor), mensaje = null) }
    }

    fun onPrecio(valor: String) {
        formulario.update { it.copy(precio = valor, errorPrecio = Validadores.precio(valor), mensaje = null) }
    }

    fun onCategoria(categoria: Categoria) {
        formulario.update { it.copy(categoria = categoria) }
    }

    fun guardar() {
        val f = formulario.value
        val errorNombre = Validadores.nombrePlato(f.nombre)
        val errorPrecio = Validadores.precio(f.precio)
        if (errorNombre != null || errorPrecio != null) {
            formulario.update { it.copy(errorNombre = errorNombre, errorPrecio = errorPrecio) }
            return
        }
        viewModelScope.launch {
            repo.agregarPlato(f.nombre.trim(), f.categoria, f.precio.toInt())
            formulario.update {
                it.copy(nombre = "", precio = "", mensaje = "Plato \"${f.nombre.trim()}\" agregado")
            }
        }
    }

    fun alternarDisponible(plato: MenuItemEntity) {
        viewModelScope.launch { repo.alternarDisponible(plato) }
    }

    fun eliminar(plato: MenuItemEntity) {
        viewModelScope.launch { repo.eliminarPlato(plato) }
    }

    fun textoParaCompartir(): String = textoMenu(uiState.value.platos)
}
