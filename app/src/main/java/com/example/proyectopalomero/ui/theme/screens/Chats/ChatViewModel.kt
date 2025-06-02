package com.example.proyectopalomero.ui.theme.screens.Chats

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.model.ChatFire
import com.example.proyectopalomero.data.model.PublicacionFire
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.repository.ChatsRepository
import com.example.proyectopalomero.data.repository.PublicacionesRepository
import com.example.proyectopalomero.data.repository.UsuarioRepository
import com.example.proyectopalomero.ui.theme.screens.Feed.FeedViewModel
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

    private var datosCargados = false

    fun obtenerChats(usuarioId: String) {
        if (datosCargados) return

        viewModelScope.launch {
            try {
                val chatsObtenidos = chatsRepository.obtenerChats(usuarioId)
                _chats.value = chatsObtenidos

                // Obtener los IDs del "otro" usuario por cada chat
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

                datosCargados = true
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error al obtener chats: ${e.message}")
            }
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