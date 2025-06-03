package com.example.proyectopalomero.ui.theme.screens.Chats

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.model.ChatFire
import com.example.proyectopalomero.data.model.MensajeFire
import com.example.proyectopalomero.data.model.PublicacionFire
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.repository.ChatsRepository
import com.example.proyectopalomero.data.repository.PublicacionesRepository
import com.example.proyectopalomero.data.repository.UsuarioRepository
import com.example.proyectopalomero.ui.theme.screens.Feed.FeedViewModel
import com.google.firebase.Timestamp
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatsRepository: ChatsRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _chats = MutableStateFlow<List<ChatFire>>(emptyList())
    val chats: StateFlow<List<ChatFire>> = _chats

    private val _usuariosChatMap = mutableStateMapOf<String, UsuarioFire?>()
    val usuariosChatMap: Map<String, UsuarioFire?> get() = _usuariosChatMap

    var chatSeleccionado = ChatFire()
    private val _mensajes = MutableStateFlow<List<MensajeFire>>(emptyList())
    val mensajes: StateFlow<List<MensajeFire>> = _mensajes

    private val _usuarioChat = MutableStateFlow<UsuarioFire?>(null)
    val usuarioChat: StateFlow<UsuarioFire?> get() = _usuarioChat

    private var datosCargados2 = false

    private var datosCargados = false

    fun obtenerChats(usuarioId: String) {
        viewModelScope.launch {
            chatsRepository.obtenerChats(usuarioId).collect { chatsObtenidos ->
                _chats.value = chatsObtenidos

                val userIds = chatsObtenidos.mapNotNull { chat ->
                    when (usuarioId) {
                        chat.idUsuario1 -> chat.idUsuario2
                        chat.idUsuario2 -> chat.idUsuario1
                        else -> null
                    }
                }.distinct()

                val usuarios = userIds.associateWith { id ->
                    usuarioRepository.obtenerUsuarioPorId(id)
                }

                _usuariosChatMap.clear()
                _usuariosChatMap.putAll(usuarios)
            }
        }
    }


    fun cargarMensajes() {
        if (datosCargados2) return

        viewModelScope.launch {
            datosCargados2 = true
            chatsRepository.obtenerMensajes(chatSeleccionado.id!!).collect { nuevosMensajes ->
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

    fun cargarUsuarioChat(idUsuario: String) {
        viewModelScope.launch {
            val usuario = usuarioRepository.obtenerUsuarioPorId(idUsuario)
            _usuarioChat.value = usuario
        }
    }

    fun limipiarDatos() {
        _chats.value = emptyList()
        _usuariosChatMap.clear()
        datosCargados = false
    }

}

class ChatViewModelFactory(
    private val chatsRepository: ChatsRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatsRepository,usuarioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}