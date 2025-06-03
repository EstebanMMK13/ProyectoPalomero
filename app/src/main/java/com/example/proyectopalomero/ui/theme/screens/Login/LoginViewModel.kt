package com.example.proyectopalomero.ui.theme.screens.Login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() {

    private val _loginExitoso = MutableLiveData<Boolean>()
    val loginExitoso: LiveData<Boolean> = _loginExitoso

    private val _mensajeError = MutableLiveData<String?>()
    val mensajeError: LiveData<String?> = _mensajeError

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val resultado = usuarioRepository.login(email, password)
                if (resultado) {
                    _loginExitoso.postValue(true)
                    _mensajeError.postValue(null)
                } else {
                    _loginExitoso.postValue(false)
                    _mensajeError.postValue("Usuario o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _loginExitoso.postValue(false)
                _mensajeError.postValue(e.message ?: "Error desconocido")
            }
            _isLoading.value = false
        }
    }

    fun limpiarEstado() {
        _loginExitoso.value = false
        _mensajeError.value = null
    }
}

class LoginViewModelFactory(
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(usuarioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}