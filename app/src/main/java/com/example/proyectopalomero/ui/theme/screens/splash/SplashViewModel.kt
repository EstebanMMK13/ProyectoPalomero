package com.example.proyectopalomero.ui.theme.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.repository.UsuarioRepository
import com.example.proyectopalomero.data.utils.Resultado

class SplashViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    fun isUserLoggedIn(): Boolean {
        return usuarioRepository.getCurrentUser()
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

    suspend fun comprobarUsuarioExiste(): Boolean {

        when (val resultado = usuarioRepository.comprobarUsuarioExiste()) {
            is Resultado.Exito -> {
                return resultado.datos
            }
            is Resultado.Error -> {
                throw Exception("Error al obtener usuario actual: ${resultado.mensaje}")
            }
        }
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