package com.example.proyectopalomero.ui.theme.screens.Chats

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.model.ChatDto
import com.example.proyectopalomero.data.model.ChatFire
import com.example.proyectopalomero.data.model.MensajeFire
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.repository.ChatsRepository
import com.example.proyectopalomero.data.repository.UsuarioRepository
import com.example.proyectopalomero.data.utils.EstadoUI
import com.example.proyectopalomero.data.utils.Resultado
import com.example.proyectopalomero.data.utils.errorGeneral
import com.example.proyectopalomero.data.utils.errorSnackBar
import com.google.firebase.Timestamp
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatsRepository: ChatsRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _estadoUI = MutableStateFlow<EstadoUI<Boolean>>(EstadoUI.Vacio)
    val estadoUI: StateFlow<EstadoUI<Boolean>> = _estadoUI.asStateFlow()

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
            _estadoUI.value = EstadoUI.Cargando
            chatsRepository.obtenerChats(usuarioId).collect { chatsObtenidos ->
                val chatsOrdenados = chatsObtenidos.sortedByDescending { it.fechaMensaje }
                _chats.value = chatsOrdenados

                val userIds = chatsOrdenados
                    .mapNotNull { chat -> chat.usuarios?.firstOrNull { it != usuarioId } }
                    .distinct()

                val usuarios = mutableMapOf<String, UsuarioFire?>()

                for (id in userIds) {
                    when (val resultado = usuarioRepository.obtenerUsuarioPorId(id)) {
                        is Resultado.Exito -> {
                            _estadoUI.value = EstadoUI.Exito(true)
                            usuarios[id] = resultado.datos
                        }

                        is Resultado.Error -> {
                            _estadoUI.value = EstadoUI.Error(
                                "Error al obtener usuario: ${resultado.mensaje}",
                                errorGeneral
                            )
                            usuarios[id] = null
                        }
                    }
                }
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
            when (val resultado = usuarioRepository.obtenerUsuarioPorId(idUsuario)) {
                is Resultado.Exito -> {
                    _estadoUI.value = EstadoUI.Exito(true)
                    _usuarioChat.value = resultado.datos
                }

                is Resultado.Error -> {
                    _estadoUI.value = EstadoUI.Error(
                        "Error al obtener usuario: ${resultado.mensaje}",
                        errorSnackBar
                    )
                }
            }

        }
    }

    fun cargarUsuarios(idUsuario: String) {
        viewModelScope.launch {
            when (val resultado = usuarioRepository.obtenerUsuarios()) {
                is Resultado.Exito -> {
                    _estadoUI.value = EstadoUI.Exito(true)
                    _listaUsuarios.value = resultado.datos.filter { it.id != idUsuario }
                }

                is Resultado.Error -> {
                    _estadoUI.value = EstadoUI.Error(
                        "Error al cargar usuarios: ${resultado.mensaje}",
                        errorSnackBar
                    )
                }
            }
        }
    }

    fun buscarUsuario(nickname: String) {

        if (!usuarioRepository.comprobarNickName(nickname)) {
            _estadoUI.value = EstadoUI.Error("El nickname debe comenzar con @", errorSnackBar)
        } else {
            viewModelScope.launch {
                when (val resultado = usuarioRepository.obtenerUsuarioPorNickname(nickname)) {
                    is Resultado.Exito -> {
                        _usuarioNuevoChat.value = resultado.datos
                        _listaUsuarios.value =
                            if (resultado.datos != null) {listOf(resultado.datos)}
                            else { emptyList() }
                        _estadoUI.value = EstadoUI.Exito(true)
                    }
                    is Resultado.Error -> {
                        _estadoUI.value = EstadoUI.Error(
                            "Error al buscar el usuario: ${resultado.mensaje}",
                            errorSnackBar
                        )
                    }
                }
            }
        }
    }

    fun comprobarChat(idUsuario1: String, idUsuario2: String) {
        viewModelScope.launch {
            val resultado = chatsRepository.obtenerChatPorUsuarios(idUsuario1, idUsuario2)
            when (resultado) {
                is Resultado.Exito -> {
                    if (resultado.datos != null) {
                        _chatSeleccionado.value = resultado.datos
                    } else {
                        // Crear nuevo chat
                        val usuariosOrdenados = listOf(idUsuario1, idUsuario2).sorted()
                        val nuevoChat = ChatDto(
                            usuarios = usuariosOrdenados,
                            fechaUMensaje = Timestamp.now(),
                            ultimoMensaje = ""
                        )
                        val chatCreado = chatsRepository.crearChat(nuevoChat)
                        when (chatCreado) {
                            is Resultado.Exito -> {
                                _estadoUI.value = EstadoUI.Exito(true)
                                _chatSeleccionado.value = chatCreado.datos
                            }

                            is Resultado.Error -> {
                                _estadoUI.value = EstadoUI.Error(
                                    "Error al crear el chat: ${chatCreado.mensaje}",
                                    errorSnackBar
                                )
                            }
                        }
                    }
                }

                is Resultado.Error -> {
                    _estadoUI.value = EstadoUI.Error(
                        "Error al buscar el chat: ${resultado.mensaje}",
                        errorSnackBar
                    )
                }
            }
        }
    }

    fun borrarChat(idChat: String) {
        viewModelScope.launch {
            when (val resultado = chatsRepository.borrarChat(idChat)) {
                is Resultado.Exito -> {
                    _estadoUI.value = EstadoUI.Exito(true)
                }

                is Resultado.Error -> {
                    _estadoUI.value = EstadoUI.Error(
                        "Error al borrar el chat: ${resultado.mensaje}",
                        errorSnackBar
                    )
                }
            }
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
            return ChatViewModel(chatsRepository, usuarioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}