package com.example.proyectopalomero.data.model

import com.google.firebase.Timestamp


data class PublicacionFire (

    var id: String? = null,
    val usuario: String? = null,
    val contenido: String? = null,
    var comentarios: Int? = null,
    var listaMeGustas: MutableList<String>? = mutableListOf(),
    var fechaCreacion: Timestamp? = null
)

data class PublicacionDTO(
    val usuario: String? = null,
    val contenido: String? = null,
    var comentarios: Int? = null,
    var listaMeGustas: MutableList<String>? = mutableListOf(),
    var fechaCreacion: Timestamp? = null
)

data class Publicacion(
    val id: Int,
    val usuario: Usuario,
    val texto: String,
    var like: Int,
    var meGusta: Boolean,
    var listaMeGusta: MutableList<Usuario> = mutableListOf()
)
