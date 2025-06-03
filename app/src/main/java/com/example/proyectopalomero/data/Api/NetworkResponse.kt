package com.example.proyectopalomero.data.Api

//Clase para manejar la respuesta de la API
sealed class NetworkResponse<out T> {
    data class Success<out T>(val data : T) : NetworkResponse<T>()
    data class Error(val message : String) : NetworkResponse<Nothing>()
    object Loading : NetworkResponse<Nothing>()
    object Empty : NetworkResponse<Nothing>()
}