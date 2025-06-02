package com.example.proyectopalomero.data.model

import com.google.firebase.Timestamp
import com.google.firebase.database.Exclude
import kotlin.collections.emptyList

data class ChatFire(
    val id: String? = null,
    val idUsuario1: String? = null,
    val idUsuario2: String? = null,
    var ultimoMensaje: String? = null,
    var fechaMensaje: Timestamp? = null,
    var mensajes: List<MensajeFire>? = null
)

data class ChatDto(
    val idUsuario1: String = "",
    val idUsuario2: String = "",
    val ultimoMensaje: String? = "",
    val horaUltimoMensaje: Timestamp? = null,
    val listaMensajes: List<MensajeDto> = emptyList()
)