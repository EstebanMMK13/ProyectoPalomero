package com.example.proyectopalomero.data.dao

import android.util.Log
import com.example.proyectopalomero.data.model.ChatDto
import com.example.proyectopalomero.data.model.ChatFire
import com.example.proyectopalomero.data.model.MensajeDto
import com.example.proyectopalomero.data.model.MensajeFire
import com.example.proyectopalomero.data.utils.Resultado
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatsDao(
    private val firestore: FirebaseFirestore
) {

    fun obtenerChats(idUsuario: String): Flow<List<ChatFire>> = callbackFlow {
        val ref = firestore.collection("chats")
            .whereArrayContains("usuarios", idUsuario)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val chats = snapshot?.documents
                ?.mapNotNull { doc ->
                    val chat = doc.toObject(ChatFire::class.java)
                    chat?.copy(id = doc.id)
                }
                ?: emptyList()

            trySend(chats).isSuccess
        }

        awaitClose { listener.remove() }
    }

    suspend fun buscarChatEntreUsuarios(idUsuario1: String, idUsuario2: String): Resultado<ChatFire?> {
        return try {
            val usuariosOrdenados = listOf(idUsuario1, idUsuario2).sorted()
            val resultado = firestore.collection("chats")
                .whereEqualTo("usuarios", usuariosOrdenados)
                .get()
                .await()

            val chat = resultado.documents.firstOrNull()?.let { doc ->
                doc.toObject(ChatFire::class.java)?.copy(id = doc.id)
            }

            Resultado.Exito(chat)
        } catch (e: Exception) {
            Resultado.Error("Error al buscar el chat entre usuarios", e)
        }
    }

    suspend fun crearChat(chat: ChatDto): Resultado<ChatFire> {
        return try {
            val chatsCollection = firestore.collection("chats")
            val nuevoDocumento = chatsCollection.document()

            val nuevoChat = ChatFire(
                id = nuevoDocumento.id,
                usuarios = chat.usuarios,
                fechaMensaje = Timestamp.now(),
                ultimoMensaje = chat.ultimoMensaje
            )

            nuevoDocumento.set(nuevoChat).await()
            Resultado.Exito(nuevoChat)
        } catch (e: Exception) {
            Resultado.Error("Error al crear el chat", e)
        }
    }

    suspend fun borrarChat(idChat: String): Resultado<Unit> {
        return try {
            firestore.collection("chats").document(idChat).delete().await()
            Resultado.Exito(Unit)
        } catch (e: Exception) {
            Resultado.Error("Error al borrar el chat", e)
        }
    }

    fun obtenerMensajes(idChat: String): Flow<List<MensajeFire>> = callbackFlow {
        val listener = firestore
            .collection("chats")
            .document(idChat)
            .collection("mensajes")
            .orderBy("fecha")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val mensajes = snapshot?.documents
                    ?.mapNotNull { it.toObject(MensajeFire::class.java) }
                    ?: emptyList()

                trySend(mensajes).isSuccess
            }

        awaitClose { listener.remove() }
    }

    fun enviarMensaje(idChat: String, mensaje: MensajeDto) {
        firestore
            .collection("chats")
            .document(idChat)
            .collection("mensajes")
            .document()
            .set(mensaje)
            .addOnSuccessListener {
                Log.d("ChatDao", "Mensaje enviado con éxito")
            }
            .addOnFailureListener {
                Log.e("ChatDao", "Error al enviar el mensaje", it)
            }
    }

    fun actualizarChat(idChat: String, mensaje: String) {
        val chatRef = firestore.collection("chats").document(idChat)
        chatRef.update(
            mapOf(
                "fechaMensaje" to Timestamp.now(),
                "ultimoMensaje" to mensaje
            )
        )
    }
}


