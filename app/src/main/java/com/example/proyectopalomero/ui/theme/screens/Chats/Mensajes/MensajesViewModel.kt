package com.example.proyectopalomero.ui.theme.screens.Chats.Mensajes

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.model.ChatFire
import com.example.proyectopalomero.data.model.MensajeFire
import com.example.proyectopalomero.data.repository.ChatsRepository
import com.example.proyectopalomero.data.repository.UsuarioRepository
import com.example.proyectopalomero.ui.theme.screens.Chats.ChatViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MensajesViewModel (
    private val chatsRepository: ChatsRepository
): ViewModel(){

    private val _mensajes = MutableStateFlow<List<MensajeFire>>(emptyList())
    val mensajes: StateFlow<List<MensajeFire>> = _mensajes

    private var datosCargados = false

    fun cargarMensajes(idChat: String) {
        if (datosCargados) return

        viewModelScope.launch {
            datosCargados = true
            chatsRepository.obtenerMensajes(idChat).collect { nuevosMensajes ->
                _mensajes.value = nuevosMensajes
            }
        }
    }

    fun enviarMensaje(idChat: String, mensaje: String,usuarioActual: String) {

        viewModelScope.launch {

            val mensajeFire = MensajeFire(
                idUsuario = usuarioActual,
                mensaje = mensaje,
                fecha = Timestamp.now()
            )
            chatsRepository.enviarMensaje(idChat, mensajeFire)
        }
    }


}



class MensajesViewModelFactory(
    private val chatsRepository: ChatsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MensajesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MensajesViewModel(chatsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}