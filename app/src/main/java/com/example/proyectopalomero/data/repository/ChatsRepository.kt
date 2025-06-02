package com.example.proyectopalomero.data.repository

import com.example.proyectopalomero.data.dao.ChatsDao

class ChatsRepository(
    private val chatsDao: ChatsDao
) {

    suspend fun obtenerChats(idUsuario: String) = chatsDao.obtenerChats(idUsuario)

}