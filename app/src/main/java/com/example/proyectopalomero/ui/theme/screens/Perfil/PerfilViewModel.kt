package com.example.proyectopalomero.ui.theme.screens.Perfil

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
import com.example.proyectopalomero.ui.theme.Components.Publicacion.PublicacionActions
import com.example.proyectopalomero.ui.theme.screens.LoginYRegister.LoginViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val publicacionesRepository: PublicacionesRepository
) : ViewModel(), PublicacionActions {

    fun singOut() {
        usuarioRepository.signOut()
    }

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _mensajeError = MutableLiveData<String?>()
    val mensajeError: LiveData<String?> = _mensajeError

    private val _publicaciones = MutableStateFlow<List<PublicacionFire>>(emptyList())
    val publicaciones: StateFlow<List<PublicacionFire>> = _publicaciones

    private var currentUserId: String? = null
    private var publicacionesJob: Job? = null

    fun observarPublicacionesPorUsuario(idUsuario: String) {
        if (currentUserId == idUsuario) return // evita re-suscripción innecesaria
        currentUserId = idUsuario
        publicacionesJob?.cancel()

        publicacionesJob = viewModelScope.launch {
            _isLoading.value = true
            publicacionesRepository
                .publicacionesFlow
                .map { lista -> lista.filter { it.usuario == idUsuario } }
                .catch { e ->
                    _mensajeError.value = e.message
                    _success.value = false
                    _isLoading.value = false
                }
                .collect { publicaciones ->
                    _publicaciones.value = publicaciones
                    _success.value = true
                    _isLoading.value = false
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

    override suspend fun eliminarPublicacion(idPublicacion: String) {
        publicacionesRepository.eliminarPublicacion(idPublicacion)
        // No necesitas tocar _publicaciones, el listener lo actualiza solo
    }

    fun actualizarUsuario(idUsuario: String, nuevoUsuario: UsuarioFire) {
        viewModelScope.launch {
            usuarioRepository.actualizarUsuario(idUsuario, nuevoUsuario)
        }
    }

    fun listaDeAvatares(): List<String> {
        return usuarioRepository.listaDeAvatares()
    }

    fun limpiarDatos() {
        publicacionesJob?.cancel()
        _publicaciones.value = emptyList()
        _success.value = false
        _mensajeError.value = null
        currentUserId = null
    }
}

class PerfilViewModelFactory(
    private val usuarioRepository: UsuarioRepository, private val publicacionesRepository: PublicacionesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PerfilViewModel(usuarioRepository,publicacionesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}