package com.example.proyectopalomero.ui.theme.screens.Perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.model.PublicacionFire
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.repository.PublicacionesRepository
import com.example.proyectopalomero.data.repository.UsuarioRepository
import com.example.proyectopalomero.data.utils.EstadoUI
import com.example.proyectopalomero.data.utils.Resultado
import com.example.proyectopalomero.data.utils.errorSnackBar
import com.example.proyectopalomero.ui.theme.Components.Publicacion.PublicacionActions
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val publicacionesRepository: PublicacionesRepository
) : ViewModel(), PublicacionActions {

    private val _estadoUI = MutableStateFlow<EstadoUI<Boolean>>(EstadoUI.Vacio)
    val estadoUI: StateFlow<EstadoUI<Boolean>> = _estadoUI.asStateFlow()

    private val _publicaciones = MutableStateFlow<List<PublicacionFire>>(emptyList())
    val publicaciones: StateFlow<List<PublicacionFire>> = _publicaciones

    private var currentUserId: String? = null
    private var publicacionesJob: Job? = null

    fun singOut() {
        usuarioRepository.signOut()
    }

    fun observarPublicacionesPorUsuario(idUsuario: String) {
        if (currentUserId == idUsuario) return // Evita re-suscripción
        currentUserId = idUsuario
        publicacionesJob?.cancel()

        publicacionesJob = viewModelScope.launch {
            _estadoUI.value = EstadoUI.Cargando
            publicacionesRepository.publicacionesFlow
                .map { lista -> lista.filter { it.usuario == idUsuario } }
                .catch { e ->
                    _estadoUI.value = EstadoUI.Error("Error al obtener publicaciones: ${e.message}", errorSnackBar)
                }
                .collect { publicacionesFiltradas ->
                    _publicaciones.value = publicacionesFiltradas
                    _estadoUI.value = EstadoUI.Exito(true)
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

    fun actualizarUsuario(idUsuario: String, nuevoUsuario: UsuarioFire) {
        viewModelScope.launch {
            when (usuarioRepository.actualizarUsuario(idUsuario, nuevoUsuario)) {
                is Resultado.Error -> { _estadoUI.value = EstadoUI.Error("Error al actualizar usuario", errorSnackBar) }
                is Resultado.Exito -> { _estadoUI.value = EstadoUI.Exito(true) }
            }
        }
    }

    fun listaDeAvatares(): List<String> {
        return usuarioRepository.listaDeAvatares()
    }

    fun limpiarDatos() {
        publicacionesJob?.cancel()
        _publicaciones.value = emptyList()
        currentUserId = null
    }
}

class PerfilViewModelFactory(
    private val usuarioRepository: UsuarioRepository,
    private val publicacionesRepository: PublicacionesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PerfilViewModel(usuarioRepository, publicacionesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}