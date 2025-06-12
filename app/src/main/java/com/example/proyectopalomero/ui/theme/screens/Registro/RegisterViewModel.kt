package com.example.proyectopalomero.ui.theme.screens.Registro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.repository.UsuarioRepository
import com.example.proyectopalomero.data.utils.EstadoUI
import com.example.proyectopalomero.data.utils.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.proyectopalomero.data.utils.errorGeneral
import com.example.proyectopalomero.data.utils.errorSnackBar

class RegisterViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _estadoUI = MutableStateFlow<EstadoUI<Boolean>>(EstadoUI.Vacio)
    val estadoUI: StateFlow<EstadoUI<Boolean>> = _estadoUI.asStateFlow()

    fun registrarUsuarioCompleto(usuario: UsuarioFire, password: String) {
        _estadoUI.value = EstadoUI.Cargando

        viewModelScope.launch {
            val validacion = usuarioRepository.validarRegistro(usuario, password)
            if (validacion != null) {
                _estadoUI.value = EstadoUI.Error(validacion,errorSnackBar)
                return@launch
            }
            when (val nicknameCheck = usuarioRepository.verificarNicknameExistente(usuario.nickname!!)) {
                is Resultado.Exito -> {
                    if (nicknameCheck.datos == true) {
                        _estadoUI.value = EstadoUI.Error("El usuario ya existe",errorSnackBar)
                        return@launch
                    }
                }
                is Resultado.Error -> {
                    _estadoUI.value = EstadoUI.Error(nicknameCheck.mensaje,errorSnackBar)
                    return@launch
                }
            }

            when (val resultado = usuarioRepository.registrarUsuario(usuario, password)) {
                is Resultado.Exito -> {
                    _estadoUI.value = EstadoUI.Exito(true)
                }
                is Resultado.Error -> {
                    _estadoUI.value = EstadoUI.Error(resultado.mensaje,errorSnackBar)
                }
            }
        }
    }

    fun limpiarEstado() {
        _estadoUI.value = EstadoUI.Vacio
    }
}

class RegisterViewModelFactory(
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(usuarioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}