package com.example.casajojo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.casajojo.ui.admin.AdminScreen
import com.example.casajojo.ui.admin.AdminViewModel
import com.example.casajojo.ui.caja.CajaScreen
import com.example.casajojo.ui.caja.CajaViewModel
import com.example.casajojo.ui.cliente.ClienteScreen
import com.example.casajojo.ui.cliente.ClienteViewModel
import com.example.casajojo.ui.cocina.CocinaScreen
import com.example.casajojo.ui.cocina.CocinaViewModel
import com.example.casajojo.ui.inicio.InicioScreen

/** Rutas de la app. Usar constantes evita errores de escritura. */
object Ruta {
    const val INICIO = "inicio"
    const val ADMIN = "admin"
    const val CLIENTE = "cliente"
    const val CAJA = "caja"
    const val COCINA = "cocina"
}

@Composable
fun AppNavigation(factory: ViewModelProvider.Factory) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Ruta.INICIO) {
        composable(Ruta.INICIO) {
            InicioScreen(onElegir = { ruta -> nav.navigate(ruta) })
        }
        composable(Ruta.ADMIN) {
            val vm: AdminViewModel = viewModel(factory = factory)
            AdminScreen(vm = vm, onVolver = { nav.popBackStack() })
        }
        composable(Ruta.CLIENTE) {
            val vm: ClienteViewModel = viewModel(factory = factory)
            ClienteScreen(vm = vm, onVolver = { nav.popBackStack() })
        }
        composable(Ruta.CAJA) {
            val vm: CajaViewModel = viewModel(factory = factory)
            CajaScreen(vm = vm, onVolver = { nav.popBackStack() })
        }
        composable(Ruta.COCINA) {
            val vm: CocinaViewModel = viewModel(factory = factory)
            CocinaScreen(vm = vm, onVolver = { nav.popBackStack() })
        }
    }
}
