package com.example.proyectopalomero.ui.theme.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.repository.UsuarioRepository

class SplashViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    fun isUserLoggedIn(): Boolean {
        return usuarioRepository.getCurrentUser()
    }

    suspend fun obtenerUsuarioActual(): UsuarioFire? {
        return usuarioRepository.obtenerUsuarioActual()
    }

    suspend fun comprobarUsuarioExiste(): Boolean {
        return usuarioRepository.comprobarUsuarioExiste()
    }

}

class SplashViewModelFactory(
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SplashViewModel(usuarioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}