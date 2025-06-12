package com.example.proyectopalomero.ui.theme.screens.Feed

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.model.PublicacionFire
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.repository.PublicacionesRepository
import com.example.proyectopalomero.data.repository.UsuarioRepository
import com.example.proyectopalomero.data.utils.EstadoUI
import com.example.proyectopalomero.data.utils.Resultado
import com.example.proyectopalomero.data.utils.errorGeneral
import com.example.proyectopalomero.data.utils.errorSnackBar
import com.example.proyectopalomero.ui.theme.Components.Publicacion.PublicacionActions
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val publicacionesRepository: PublicacionesRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel(), PublicacionActions {

    private val _estadoUI = MutableStateFlow<EstadoUI<Boolean>>(EstadoUI.Vacio)
    val estadoUI: StateFlow<EstadoUI<Boolean>> = _estadoUI.asStateFlow()

    private val _usuariosMap = mutableStateMapOf<String, UsuarioFire?>()
    val usuariosMap: Map<String, UsuarioFire?> get() = _usuariosMap

    private var publicacionesJob: Job? = null

    val publicaciones = publicacionesRepository.publicacionesFlow

    init { publicacionesRepository.iniciarEscuchaPublicacionesEnTiempoReal() }

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
                    _estadoUI.value = EstadoUI.Error("Error al cargar usuarios: ${resultado.mensaje}", errorGeneral)
                }
            }
        }
    }

    private suspend fun obtenerUsuarios(userIds: List<String>): Resultado<Map<String, UsuarioFire?>> = coroutineScope {
        try {
            val deferredUsuarios = userIds.map { id ->
                async {
                    val resultado = usuarioRepository.obtenerUsuarioPorId(id)
                    id to resultado
                }
            }
            val resultados = deferredUsuarios.awaitAll()
            val usuariosMap = mutableMapOf<String, UsuarioFire?>()

            for ((id, resultado) in resultados) {
                when (resultado) {
                    is Resultado.Exito -> usuariosMap[id] = resultado.datos
                    is Resultado.Error -> { usuariosMap[id] = null }
                }
            }
            Resultado.Exito(usuariosMap)
        } catch (e: Exception) {
            Resultado.Error("Error cargando usuarios", e)
        }
    }

    suspend fun obtenerUsuarioActual(): UsuarioFire? {
        val resultado = usuarioRepository.obtenerUsuarioActual()

        when (resultado) {
            is Resultado.Exito -> {
                _estadoUI.value = EstadoUI.Exito(true)
                return resultado.datos
            }
            is Resultado.Error -> {
                _estadoUI.value = EstadoUI.Error("Error al obtener usuario actual: ${resultado.mensaje}", errorSnackBar)
                return null
            }
        }
    }


    override fun leGustaAlUsuario(publicacion: PublicacionFire, idUsuario: String): Boolean {
        return publicacion.listaMeGustas?.contains(idUsuario) == true
    }

    override fun alternarMeGusta(publicacion: PublicacionFire, idUsuario: String) {
        viewModelScope.launch {
            val leGusta = publicacion.listaMeGustas?.contains(idUsuario) ?: false

            if (leGusta) {
                when (val resultado =  publicacionesRepository.quitarMeGustaPublicacion(publicacion.id!!, idUsuario)){

                    is Resultado.Exito -> {
                        publicacion.listaMeGustas?.remove(idUsuario)
                        _estadoUI.value = EstadoUI.Exito(true)
                    }
                    is Resultado.Error -> {
                        _estadoUI.value = EstadoUI.Error("Error al quitar me gusta: ${resultado.mensaje}",errorSnackBar)
                    }
                }
            } else {
                when(val resultado = publicacionesRepository.darMeGustaPublicacion(publicacion.id!!, idUsuario)){

                    is Resultado.Exito -> {
                        publicacion.listaMeGustas?.add(idUsuario)
                        _estadoUI.value = EstadoUI.Exito(true)
                    }
                    is Resultado.Error -> {
                        _estadoUI.value = EstadoUI.Error("Error al dar me gusta: ${resultado.mensaje}", errorSnackBar)
                    }
                }
            }
        }
    }

    override fun eliminarPublicacion(idPublicacion: String) {
        viewModelScope.launch {
            when (val resultado = publicacionesRepository.eliminarPublicacion(idPublicacion)){
                is Resultado.Exito -> {_estadoUI.value = EstadoUI.Exito(true)}
                is Resultado.Error -> { _estadoUI.value = EstadoUI.Error("Error al eliminar publicacion: ${resultado.mensaje}",errorSnackBar) }
            }
        }
    }

    fun limpiarDatos() {
        publicacionesJob?.cancel()
        _usuariosMap.clear()
        _estadoUI.value = EstadoUI.Vacio
    }

    fun limpiarEstado(){
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