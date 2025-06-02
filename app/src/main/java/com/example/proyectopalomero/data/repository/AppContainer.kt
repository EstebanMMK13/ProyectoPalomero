package com.example.proyectopalomero.data.repository

import com.example.proyectopalomero.data.dao.ChatsDao
import com.example.proyectopalomero.data.dao.PublicacionesDao
import com.example.proyectopalomero.data.dao.UsuarioDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AppContainer {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // DAOs
    private val usuarioDao = UsuarioDao(firestore, auth)
    private val publicacionesDao = PublicacionesDao(firestore)
    private val chatsDao = ChatsDao(firestore)


    // Repositorios

    val usuarioRepository = UsuarioRepository(usuarioDao)
    val publicacionesRepository = PublicacionesRepository(publicacionesDao)
    val chatsRepository = ChatsRepository(chatsDao)
}

