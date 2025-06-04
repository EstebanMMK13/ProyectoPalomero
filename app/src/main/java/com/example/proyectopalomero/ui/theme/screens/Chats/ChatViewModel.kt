package com.example.proyectopalomero.ui.theme.screens.Chats

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.model.ChatDto
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatsRepository: ChatsRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    // Chats y usuarios en chats
    private val _chats = MutableStateFlow<List<ChatFire>>(emptyList())
    val chats: StateFlow<List<ChatFire>> = _chats

    private val _usuariosChatMap = mutableStateMapOf<String, UsuarioFire?>()
    val usuariosChatMap: Map<String, UsuarioFire?> get() = _usuariosChatMap

    private val _chatSeleccionado = MutableStateFlow<ChatFire?>(null)
    val chatSeleccionado: StateFlow<ChatFire?> = _chatSeleccionado.asStateFlow()

    // Mensajes del chat seleccionado
    private val _mensajes = MutableStateFlow<List<MensajeFire>>(emptyList())
    val mensajes: StateFlow<List<MensajeFire>> = _mensajes

    private var jobMensajes: Job? = null

    // Usuario con quien se está chateando
    private val _usuarioChat = MutableStateFlow<UsuarioFire?>(null)
    val usuarioChat: StateFlow<UsuarioFire?> get() = _usuarioChat

    // Usuarios para nueva conversación
    private val _listaUsuarios = MutableLiveData<List<UsuarioFire>>()
    val listaUsuarios: LiveData<List<UsuarioFire>> = _listaUsuarios

    private val _usuarioNuevoChat = MutableStateFlow<UsuarioFire?>(null)
    val usuarioNuevoChat: StateFlow<UsuarioFire?> get() = _usuarioNuevoChat


    fun seleccionarChat(chat: ChatFire) {
        _chatSeleccionado.value = chat
        cargarMensajes()
    }

    fun obtenerChats(usuarioId: String) {
        viewModelScope.launch {
            chatsRepository.obtenerChats(usuarioId).collect { chatsObtenidos ->

                val chatsOrdenados = chatsObtenidos.sortedByDescending { it.fechaMensaje }
                _chats.value = chatsOrdenados
                val userIds = chatsOrdenados
                    .mapNotNull { chat -> chat.usuarios?.firstOrNull { it != usuarioId } }
                    .distinct()
                val usuarios = userIds.associateWith { id -> usuarioRepository.obtenerUsuarioPorId(id) }
                _usuariosChatMap.clear()
                _usuariosChatMap.putAll(usuarios)
            }
        }
    }


    fun cargarMensajes() {
        val chatId = _chatSeleccionado.value?.id ?: return

        jobMensajes?.cancel()  // Cancelar cualquier carga previa de mensajes

        jobMensajes = viewModelScope.launch {
            chatsRepository.obtenerMensajes(chatId).collect { nuevosMensajes ->
                _mensajes.value = nuevosMensajes
            }
        }
    }

    fun enviarMensaje(idChat: String, mensaje: String, usuarioActual: String) {
        viewModelScope.launch {
            val mensajeFire = MensajeFire(
                idUsuario = usuarioActual,
                mensaje = mensaje,
                fecha = Timestamp.now()
            )
            chatsRepository.enviarMensaje(idChat, mensajeFire)
        }
    }

    fun limpiarSeleccion() {
        jobMensajes?.cancel()  // Cancelar la colección activa
        _chatSeleccionado.value = null
        _mensajes.value = emptyList()
    }

    fun cargarUsuarioChat(idUsuario: String) {
        viewModelScope.launch {
            val usuario = usuarioRepository.obtenerUsuarioPorId(idUsuario)
            _usuarioChat.value = usuario
        }
    }

    fun cargarUsuarios(idUsuario : String) {
        viewModelScope.launch {
            val usuarios = usuarioRepository.obtenerUsuarios()
            _listaUsuarios.value = usuarios.filter { it.id != idUsuario }
        }
    }

    fun buscarUsuario(nickname: String) {
        viewModelScope.launch {
            val usuario = usuarioRepository.obtenerUsuarioPorNickname(nickname)
            _usuarioNuevoChat.value = usuario
            _listaUsuarios.value = if (usuario != null) listOf(usuario) else emptyList()
        }
    }

    fun comprobarChat(idUsuario1: String, idUsuario2: String) {
        viewModelScope.launch {
            val chatExistente = chatsRepository.obtenerChatPorUsuarios(idUsuario1, idUsuario2)
            if (chatExistente != null) {
                _chatSeleccionado.value = chatExistente
            } else {
                // Crear nuevo chat
                val usuariosOrdenados = listOf(idUsuario1, idUsuario2).sorted()
                val nuevoChat = ChatDto(
                    usuarios = usuariosOrdenados,
                    horaUltimoMensaje = Timestamp.now(),
                    ultimoMensaje = ""
                )
                val chatCreado = chatsRepository.crearChat(nuevoChat)
                _chatSeleccionado.value = chatCreado
            }
        }
    }

    fun borrarChat(idChat : String){
        viewModelScope.launch {
            chatsRepository.borrarChat(idChat)
        }
    }

    fun limpiarDatos() {
        _chats.value = emptyList()
        _usuariosChatMap.clear()
        _mensajes.value = emptyList()
        _usuarioChat.value = null
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