package com.example.casajojo.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * RECURSOS NATIVOS del dispositivo:
 *  1) Notificaciones locales (canal + permiso POST_NOTIFICATIONS)
 *  2) Intent ACTION_SEND para compartir el menú por WhatsApp
 *  (El selector nativo de hora se usa en la pantalla de Cliente con TimePickerDialog.)
 */
object Notificaciones {

    const val CANAL_ID = "estado_pedidos"

    /** Crear el canal una sola vez (obligatorio desde Android 8). */
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Estado de pedidos",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
    }

    @SuppressLint("MissingPermission")
    fun pedidoListo(context: Context, pedidoId: Int, cliente: String) {
        val permitido = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!permitido) return  // sin permiso no se notifica, y la app sigue funcionando

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡Tu pedido está listo!")
            .setContentText("$cliente, puedes retirar tu pedido #$pedidoId en Casa JoJo.")
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(pedidoId, notificacion)
    }

    fun compartirMenu(context: Context, texto: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, texto)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir menú del día"))
    }
}
