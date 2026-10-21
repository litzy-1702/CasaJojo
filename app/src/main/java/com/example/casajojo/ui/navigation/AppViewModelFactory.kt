package com.example.casajojo.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.casajojo.data.repository.ComandasRepository
import com.example.casajojo.ui.admin.AdminViewModel
import com.example.casajojo.ui.caja.CajaViewModel
import com.example.casajojo.ui.cliente.ClienteViewModel
import com.example.casajojo.ui.cocina.CocinaViewModel

/**
 * Fábrica que le entrega el repositorio a cada ViewModel.
 * Así los ViewModels reciben sus dependencias por constructor (inyección manual).
 */
class AppViewModelFactory(private val repo: ComandasRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AdminViewModel::class.java) -> AdminViewModel(repo)
        modelClass.isAssignableFrom(ClienteViewModel::class.java) -> ClienteViewModel(repo)
        modelClass.isAssignableFrom(CajaViewModel::class.java) -> CajaViewModel(repo)
        modelClass.isAssignableFrom(CocinaViewModel::class.java) -> CocinaViewModel(repo)
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    } as T
}
