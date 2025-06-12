package com.example.proyectopalomero.data.repository

import com.example.proyectopalomero.data.dao.ChatsDao
import com.example.proyectopalomero.data.model.ChatDto
import com.example.proyectopalomero.data.model.ChatFire
import com.example.proyectopalomero.data.model.MensajeDto
import com.example.proyectopalomero.data.model.MensajeFire
import com.example.proyectopalomero.data.utils.Resultado

class ChatsRepository(
    private val chatsDao: ChatsDao
) {

    fun obtenerChats(idUsuario: String) = chatsDao.obtenerChats(idUsuario)

    suspend fun obtenerChatPorUsuarios(idUsuario1: String, idUsuario2: String): Resultado<ChatFire?> {
        return chatsDao.buscarChatEntreUsuarios(idUsuario1, idUsuario2)
    }

    suspend fun crearChat(chat : ChatDto) = chatsDao.crearChat(chat)
    suspend fun borrarChat(idChat: String) = chatsDao.borrarChat(idChat)

    fun obtenerMensajes(idChat: String) = chatsDao.obtenerMensajes(idChat)

    fun enviarMensaje(idChat: String, mensaje: MensajeFire){

        val dto = MensajeDto(
            idUsuario = mensaje.idUsuario,
            mensaje = mensaje.mensaje,
            fecha = mensaje.fecha
        )
        chatsDao.enviarMensaje(idChat,dto)
        chatsDao.actualizarChat(idChat,dto.mensaje)
    }

}