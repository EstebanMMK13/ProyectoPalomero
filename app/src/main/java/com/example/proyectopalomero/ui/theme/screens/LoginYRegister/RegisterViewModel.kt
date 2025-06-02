package com.example.proyectopalomero.ui.theme.screens.LoginYRegister

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _registroExito = MutableLiveData<Boolean>()
    val registroExito: LiveData<Boolean> = _registroExito

    private val _errorMensaje = MutableLiveData<String?>()
    val errorMensaje: LiveData<String?> = _errorMensaje

    fun registrarUsuarioCompleto(usuario: UsuarioFire, password: String) {
        viewModelScope.launch {
            val validacion = usuarioRepository.validarRegistro(usuario, password)
            if (validacion != null) {
                _errorMensaje.postValue(validacion)
                _registroExito.postValue(false)
                return@launch
            }

            val existeNickname = usuarioRepository.verificarNicknameExistente(usuario.nickname!!)
            if (existeNickname) {
                _errorMensaje.postValue("El usuario ya existe")
                _registroExito.postValue(false)
                return@launch
            }

            val exito = usuarioRepository.registrarUsuario(usuario, password)
            if (exito) {
                _registroExito.postValue(true)
                _errorMensaje.postValue(null)
            } else {
                _registroExito.postValue(false)
                _errorMensaje.postValue("Error en el registro")
            }
        }
    }
    fun limpiarEstado() {
        _registroExito.value = false
        _errorMensaje.value = null
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