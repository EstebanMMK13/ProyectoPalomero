package com.example.proyectopalomero.ui.theme.screens.Login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.repository.UsuarioRepository
import com.example.proyectopalomero.data.utils.EstadoUI
import com.example.proyectopalomero.data.utils.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.proyectopalomero.data.utils.errorGeneral
import com.example.proyectopalomero.data.utils.errorSnackBar

class LoginViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() {

    private val _estadoUI = MutableStateFlow<EstadoUI<Boolean>>(EstadoUI.Vacio)
    val estadoUI: StateFlow<EstadoUI<Boolean>> = _estadoUI.asStateFlow()

    fun login(email: String, password: String) {
        _estadoUI.value = EstadoUI.Cargando
        viewModelScope.launch {
            when (val resultado = usuarioRepository.login(email, password)) {
                is Resultado.Exito -> {
                    _estadoUI.value =EstadoUI.Exito(true)
                }
                is Resultado.Error -> {
                    _estadoUI.value =EstadoUI.Error(resultado.mensaje, errorSnackBar)
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