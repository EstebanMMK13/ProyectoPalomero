package com.example.proyectopalomero.data.repository

import com.example.proyectopalomero.data.dao.ChatsDao
import com.example.proyectopalomero.data.model.MensajeDto
import com.example.proyectopalomero.data.model.MensajeFire

class ChatsRepository(
    private val chatsDao: ChatsDao
) {

    fun obtenerChats(idUsuario: String) = chatsDao.obtenerChats(idUsuario)

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