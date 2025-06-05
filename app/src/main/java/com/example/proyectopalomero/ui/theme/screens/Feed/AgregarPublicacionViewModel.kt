package com.example.proyectopalomero.ui.theme.screens.Feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.model.PublicacionFire
import com.example.proyectopalomero.data.repository.PublicacionesRepository
import com.example.proyectopalomero.data.repository.UsuarioRepository
import com.example.proyectopalomero.data.utils.EstadoUI
import com.example.proyectopalomero.data.utils.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AgregarPublicacionViewModel(
    private val publicacionesRepository: PublicacionesRepository
) : ViewModel(){

    private val _estadoUI = MutableStateFlow<EstadoUI<Boolean>>(EstadoUI.Vacio)
    val estadoUI: StateFlow<EstadoUI<Boolean>> = _estadoUI.asStateFlow()

    fun agregarPublicacion(publicacion: PublicacionFire) {
        viewModelScope.launch {
            when (val resultado = publicacionesRepository.agregarPublicacion(publicacion)){
                is Resultado.Exito -> {
                    publicacion.id = resultado.datos
                    _estadoUI.value = EstadoUI.Exito(true)
                }
                is Resultado.Error -> {
                    _estadoUI.value = EstadoUI.Error("Error al agregar publicacion: ${resultado.mensaje}")
                }
            }
        }
    }

    fun comprobarPublicacion(publicacion: String) : Boolean{
        if(publicacion.isEmpty()) {
            _estadoUI.value = EstadoUI.Error("La publicacion no puede estar vacia")
            return false
        }else{
            return true
        }
    }

    fun limipiarEstado(){
        _estadoUI.value = EstadoUI.Vacio
    }
}

class AgregarPublicacionViewModelFactory(
    private val publicacionesRepository: PublicacionesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgregarPublicacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgregarPublicacionViewModel(publicacionesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}