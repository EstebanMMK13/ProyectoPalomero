package com.example.proyectopalomero.data.dao

import android.util.Log
import com.example.proyectopalomero.data.model.ChatFire
import com.example.proyectopalomero.data.model.MensajeFire
import com.example.proyectopalomero.data.model.PublicacionFire
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChatsDao(
    private val firestore: FirebaseFirestore
) {

    suspend fun obtenerChats(idUsuario: String): List<ChatFire> {
        return try {
            val snapshot1 = firestore.collection("chats")
                .whereEqualTo("idUsuario1", idUsuario)
                .get()
                .await()

            val snapshot2 = firestore.collection("chats")
                .whereEqualTo("idUsuario2", idUsuario)
                .get()
                .await()

            val documentos = (snapshot1.documents + snapshot2.documents).distinctBy { it.id }

            documentos.mapNotNull { doc ->
                val chat = doc.toObject(ChatFire::class.java)
                chat?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

}

