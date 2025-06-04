package com.example.proyectopalomero.ui.theme.screens.Login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.repository.UsuarioRepository
import com.example.proyectopalomero.data.utils.EstadoUI
import com.example.proyectopalomero.data.utils.Resultado
import kotlinx.coroutines.launch

class LoginViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() {

    private val _estadoUI = MutableLiveData<EstadoUI<Boolean>>(EstadoUI.Vacio)
    val estadoUI: LiveData<EstadoUI<Boolean>> = _estadoUI

    fun login(email: String, password: String) {
        _estadoUI.value = EstadoUI.Cargando
        viewModelScope.launch {
            when (val resultado = usuarioRepository.login(email, password)) {
                is Resultado.Exito -> {
                    _estadoUI.postValue(EstadoUI.Exito(true))
                }
                is Resultado.Error -> {
                    _estadoUI.postValue(EstadoUI.Error(resultado.mensaje))
                }
            }
        }
    }

    fun limpiarEstado() {
        _estadoUI.value = EstadoUI.Vacio
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