package com.example.proyectopalomero.data.repository

import com.example.proyectopalomero.data.dao.PublicacionesDao
import com.example.proyectopalomero.data.model.PublicacionDTO
import com.example.proyectopalomero.data.model.PublicacionFire
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PublicacionesRepository(private val publicacionesDao: PublicacionesDao) {

    // StateFlow que mantiene siempre actualizada la lista completa de publicaciones
    private val _publicacionesFlow = MutableStateFlow<List<PublicacionFire>>(emptyList())
    val publicacionesFlow: StateFlow<List<PublicacionFire>> = _publicacionesFlow

    //fun obtenerPublicacionesEnTiempoReal(): Flow<List<PublicacionFire>> = publicacionesDao.obtenerPublicacionesEnTiempoReal()

    fun iniciarEscuchaPublicacionesEnTiempoReal() {
        CoroutineScope(Dispatchers.IO).launch {
            publicacionesDao.obtenerPublicacionesEnTiempoReal()
                .catch { e ->
                    // Puedes loguear o manejar el error si quieres
                }
                .collect { publicaciones ->
                    _publicacionesFlow.value = publicaciones
                }
        }
    }

    fun obtenerPublicacionesPorUsuario(idUsuario: String): Flow<List<PublicacionFire>> =
        publicacionesFlow.map { publicaciones ->
            publicaciones.filter { it.usuario == idUsuario }
        }

    suspend fun agregarPublicacion(publicacion: PublicacionFire): String {
        val dto = PublicacionDTO(
            usuario = publicacion.usuario,
            contenido = publicacion.contenido,
            comentarios = publicacion.comentarios,
            listaMeGustas = publicacion.listaMeGustas,
            fechaCreacion = publicacion.fechaCreacion
        )
        return publicacionesDao.agregarPublicacion(dto)
    }


    suspend fun eliminarPublicacion(idPublicacion: String) = publicacionesDao.eliminarPublicacion(idPublicacion)

    fun darMeGustaPublicacion(idPublicacion: String, idUsuario: String) = publicacionesDao.darMeGustaPublicacion(idPublicacion, idUsuario)
    fun quitarMeGustaPublicacion(idPublicacion: String, idUsuario: String) = publicacionesDao.quitarMeGustaPublicacion(idPublicacion, idUsuario)

}