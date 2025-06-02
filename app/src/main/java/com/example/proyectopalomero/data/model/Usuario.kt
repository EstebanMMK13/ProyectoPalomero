package com.example.proyectopalomero.data.model

data class UsuarioFire (
    var id : String? = null,
    var nombre: String? = null,
    var nickname: String? = null,
    var correo: String? = null,
    var fotoPerfil: String?= null
)

data class UsuarioDto (
    var nombre: String? = null,
    var nickname: String? = null,
    var correo: String? = null,
    var fotoPerfil: String?= null
)

data class Usuario(
    var nombre: String,
    var nickname: String,
    var correo: String,
    var password: String,
    var fotoPerfil: Int,
    var id: Int
)
