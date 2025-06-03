package com.example.proyectopalomero.ui.theme.Components.Publicacion

import com.example.proyectopalomero.data.model.PublicacionFire

interface PublicacionActions {
    suspend fun eliminarPublicacion(id: String)
    fun alternarMeGusta(publicacion: PublicacionFire, userId: String)
    fun leGustaAlUsuario(publicacion: PublicacionFire, userId: String): Boolean
}