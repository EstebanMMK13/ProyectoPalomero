package com.example.proyectopalomero.data.model

import com.google.firebase.Timestamp
import com.google.firebase.database.Exclude
import kotlin.collections.emptyList

data class ChatFire(
    var id: String? = null,
    val usuarios : List<String>? = emptyList(),
    var ultimoMensaje: String? = null,
    var fechaMensaje: Timestamp? = null,
    var mensajes: List<MensajeFire>? = null
)

data class ChatDto(
    val usuarios : List<String>? = emptyList(),
    val ultimoMensaje: String? = "",
    val horaUltimoMensaje: Timestamp? = null,
    val listaMensajes: List<MensajeDto> = emptyList()
)