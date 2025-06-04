package com.example.proyectopalomero.data.utils

sealed class EstadoUI<out T> {
    object Cargando : EstadoUI<Nothing>()
    object Vacio : EstadoUI<Nothing>()
    data class Exito<out T>(val datos: T) : EstadoUI<T>()
    data class Error(val mensaje: String) : EstadoUI<Nothing>()
}