package com.example.casajojo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MenuItemEntity::class, PedidoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun menuDao(): MenuDao
    abstract fun pedidoDao(): PedidoDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        /** Singleton: una sola conexión a la base de datos para toda la app. */
        fun get(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "casajojo.db"
                ).build().also { instancia = it }
            }
    }
}
