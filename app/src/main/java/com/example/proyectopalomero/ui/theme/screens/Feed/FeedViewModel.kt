package com.example.proyectopalomero.ui.theme.screens.Feed

import android.R.attr.id
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
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FeedViewModel(
    private val publicacionesRepository: PublicacionesRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() , PublicacionActions{

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _mensajeError = MutableLiveData<String?>()
    val mensajeError: LiveData<String?> = _mensajeError

    private val _usuariosMap = mutableStateMapOf<String, UsuarioFire?>()
    val usuariosMap: Map<String, UsuarioFire?> get() = _usuariosMap

    private var publicacionesJob: Job? = null

    // Nos suscribimos directamente al flujo expuesto por el repositorio
    val publicaciones = publicacionesRepository.publicacionesFlow

    init {
        publicacionesRepository.iniciarEscuchaPublicacionesEnTiempoReal()
        obtenerPublicaciones()
    }

    fun obtenerPublicaciones() {
        publicacionesJob?.cancel()

        publicacionesJob = viewModelScope.launch {
            _isLoading.value = true

            publicaciones
                .catch { e ->
                    _mensajeError.value = e.message
                    _success.value = false
                    _isLoading.value = false
                }
                .collect { publicacionesList ->
                    cargarUsuarios(publicacionesList.mapNotNull { it.usuario }.distinct())
                    _success.value = true
                    _isLoading.value = false
                }
        }
    }

    private fun cargarUsuarios(userIds: List<String>) {
        viewModelScope.launch {
            try {
                val usuarios = userIds.associateWith { id ->
                    usuarioRepository.obtenerUsuarioPorId(id)
                }
                _usuariosMap.clear()
                _usuariosMap.putAll(usuarios)
            } catch (e: Exception) {
                // Manejo de error opcional
            }
        }
    }

    suspend fun obtenerUsuarioActual(): UsuarioFire {
        return usuarioRepository.obtenerUsuarioActual()
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
        _success.value = false
        _mensajeError.value = null
    }

    fun recargarPublicaciones() {
        _isLoading.value = true
        limpiarDatos()
        publicacionesRepository.iniciarEscuchaPublicacionesEnTiempoReal()
        obtenerPublicaciones()
        _isLoading.value = false

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