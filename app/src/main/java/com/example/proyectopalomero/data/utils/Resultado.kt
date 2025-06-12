package com.example.proyectopalomero.data.utils

sealed class Resultado<out T> {
    data class Exito<out T>(val datos: T) : Resultado<T>()
    data class Error(val mensaje: String, val exception: Exception? = null) : Resultado<Nothing>()
}
