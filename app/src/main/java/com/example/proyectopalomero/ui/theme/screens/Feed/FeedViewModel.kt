package com.example.proyectopalomero.ui.theme.screens.Feed

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.model.PublicacionFire
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.repository.PublicacionesRepository
import com.example.proyectopalomero.data.repository.UsuarioRepository
import com.example.proyectopalomero.data.utils.EstadoUI
import com.example.proyectopalomero.data.utils.Resultado
import com.example.proyectopalomero.ui.theme.Components.Publicacion.PublicacionActions
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
class FeedViewModel(
    private val publicacionesRepository: PublicacionesRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel(), PublicacionActions {

    private val _estadoUI = MutableLiveData<EstadoUI<Boolean>>()
    val estadoUI: LiveData<EstadoUI<Boolean>> = _estadoUI

    private val _usuariosMap = mutableStateMapOf<String, UsuarioFire?>()
    val usuariosMap: Map<String, UsuarioFire?> get() = _usuariosMap

    private var publicacionesJob: Job? = null

    val publicaciones = publicacionesRepository.publicacionesFlow

    init {
        publicacionesRepository.iniciarEscuchaPublicacionesEnTiempoReal()
        obtenerPublicaciones()
    }

    fun obtenerPublicaciones() {
        publicacionesJob?.cancel()
        _estadoUI.value = EstadoUI.Cargando
        publicacionesJob = viewModelScope.launch {
            publicaciones.collect { publicacionesList ->
                val userIds = publicacionesList.mapNotNull { it.usuario }.distinct()
                cargarUsuarios(userIds)
            }
        }
    }

    private fun cargarUsuarios(userIds: List<String>) {
        viewModelScope.launch {
            val resultado = obtenerUsuarios(userIds)
            when (resultado) {
                is Resultado.Exito -> {
                    _usuariosMap.clear()
                    _usuariosMap.putAll(resultado.datos)
                    _estadoUI.value = EstadoUI.Exito(true)
                }
                is Resultado.Error -> {
                    _estadoUI.value = EstadoUI.Error("Error al cargar usuarios: ${resultado.mensaje}")
                }
            }
        }
    }

    private suspend fun obtenerUsuarios(userIds: List<String>): Resultado<Map<String, UsuarioFire?>> {
        val usuariosMap = mutableMapOf<String, UsuarioFire?>()

        for (id in userIds) {
            when (val resultado = usuarioRepository.obtenerUsuarioPorId(id)) {
                is Resultado.Exito -> {
                    usuariosMap[id] = resultado.datos
                }
                is Resultado.Error -> {
                    return Resultado.Error("Error al obtener usuario con ID $id: ${resultado.mensaje}", resultado.exception)
                }
            }
        }

        return Resultado.Exito(usuariosMap)
    }

    suspend fun obtenerUsuarioActual(): UsuarioFire {
        when (val resultado = usuarioRepository.obtenerUsuarioActual()) {
            is Resultado.Exito -> {
                return resultado.datos
            }
            is Resultado.Error -> {
                throw Exception("Error al obtener usuario actual: ${resultado.mensaje}")
            }
        }
    }

    override fun leGustaAlUsuario(publicacion: PublicacionFire, idUsuario: String): Boolean {
        return publicacion.listaMeGustas?.contains(idUsuario) == true
    }

    override fun alternarMeGusta(publicacion: PublicacionFire, idUsuario: String) {
        val leGusta = publicacion.listaMeGustas?.contains(idUsuario) ?: false
        if (leGusta) {
            publicacion.listaMeGustas?.remove(idUsuario)
            publicacionesRepository.quitarMeGustaPublicacion(publicacion.id!!, idUsuario)
        } else {
            publicacion.listaMeGustas?.add(idUsuario)
            publicacionesRepository.darMeGustaPublicacion(publicacion.id!!, idUsuario)
        }
    }

    fun agregarPublicacion(publicacion: PublicacionFire) {
        viewModelScope.launch {
            val id = publicacionesRepository.agregarPublicacion(publicacion)
            publicacion.id = id
        }
    }

    override suspend fun eliminarPublicacion(idPublicacion: String) {
        publicacionesRepository.eliminarPublicacion(idPublicacion)
    }

    fun limpiarDatos() {
        publicacionesJob?.cancel()
        _usuariosMap.clear()
        _estadoUI.value = EstadoUI.Vacio
    }

    fun recargarPublicaciones() {
        limpiarDatos()
        publicacionesRepository.iniciarEscuchaPublicacionesEnTiempoReal()
        obtenerPublicaciones()
    }
}



class FeedViewModelFactory(
    private val publicacionesRepository: PublicacionesRepository,
    private val usuariosRepository: UsuarioRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModel(publicacionesRepository, usuariosRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}