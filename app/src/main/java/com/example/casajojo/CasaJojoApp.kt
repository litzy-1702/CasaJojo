package com.example.casajojo

import android.app.Application
import com.example.casajojo.data.local.AppDatabase
import com.example.casajojo.data.repository.ComandasRepository
import com.example.casajojo.util.Notificaciones
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Clase Application: se crea una sola vez al abrir la app.
 * Aquí se crea el repositorio (punto único de acceso a datos) y el canal de notificaciones.
 */
class CasaJojoApp : Application() {

    val repository: ComandasRepository by lazy {
        val db = AppDatabase.get(this)
        ComandasRepository(db.menuDao(), db.pedidoDao())
    }

    override fun onCreate() {
        super.onCreate()
        Notificaciones.crearCanal(this)
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            repository.sembrarSiVacio()
        }
    }
}
