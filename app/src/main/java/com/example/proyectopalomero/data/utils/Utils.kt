package com.example.proyectopalomero.data.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun utilsMostrarToast(context: Context, mensaje: String) {
    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
}

object Routes {
    const val SPLASH = "SplashScreen"
    const val LOGIN = "LoginScreen"
    const val REGISTER = "RegisterScreen"
    const val FEED = "FeedScreen"
    const val AGREGAR_PUBLICACION = "AgregarPublicacionScreen"
    const val PERFIL = "PerfilScreen"
    const val EDITAR_PERFIL = "EditarPerfilScreen"
    const val TIEMPO = "TiempoScreen"
    const val CHATS = "ChatsScreen"
    const val MENSAJES = "MensajesScreen"
}

fun formatearHora(timestamp: Timestamp): String {
    val date = timestamp.toDate()

    val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formatoFecha = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

    val ahora = Calendar.getInstance()
    val fechaTimestamp = Calendar.getInstance().apply { time = date }

    val mismaFecha = ahora.get(Calendar.YEAR) == fechaTimestamp.get(Calendar.YEAR) &&
            ahora.get(Calendar.DAY_OF_YEAR) == fechaTimestamp.get(Calendar.DAY_OF_YEAR)

    return if (mismaFecha) {
        formatoHora.format(date) // Solo hora si es hoy
    } else {
        formatoFecha.format(date) // Solo día/mes/año si es otro día
    }
}