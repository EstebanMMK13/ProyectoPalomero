package com.example.proyectopalomero.data.model

import com.google.firebase.Timestamp

data class MensajeFire(
    val id: String = "",
    val mensaje: String = "",
    val idUsuario: String = "",
    val fecha: Timestamp? = null
)

data class MensajeDto(
    val mensaje: String = "",
    val idUsuario: String = "",
    val fecha: Timestamp? = null
)

