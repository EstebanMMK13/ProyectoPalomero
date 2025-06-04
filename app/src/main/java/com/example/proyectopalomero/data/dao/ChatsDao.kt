package com.example.proyectopalomero.data.dao

import android.system.Os.close
import android.util.Log
import com.example.proyectopalomero.data.model.ChatDto
import com.example.proyectopalomero.data.model.ChatFire
import com.example.proyectopalomero.data.model.MensajeDto
import com.example.proyectopalomero.data.model.MensajeFire
import com.example.proyectopalomero.data.model.PublicacionFire
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

        awaitClose {
            listener.remove()
        }
    }

    suspend fun buscarChatEntreUsuarios(idUsuario1: String, idUsuario2: String): ChatFire? {
        val chatsRef = firestore.collection("chats")
        val usuariosOrdenados = listOf(idUsuario1, idUsuario2).sorted()

        val resultado = chatsRef
            .whereEqualTo("usuarios", usuariosOrdenados)
            .get()
            .await()

        return resultado.documents.firstOrNull()?.let { doc ->
            doc.toObject(ChatFire::class.java)?.copy(id = doc.id)
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

    suspend fun crearChat(chat: ChatDto): ChatFire {
        val chatsCollection = firestore.collection("chats")
        val nuevoDocumento = chatsCollection.document()  // crea un nuevo ID

        val nuevoChat = ChatFire(
            id = nuevoDocumento.id,
            usuarios = chat.usuarios,
            fechaMensaje = Timestamp.now(),
            ultimoMensaje = chat.ultimoMensaje
        )
        nuevoDocumento.set(nuevoChat).await()
        return nuevoChat
    }

    suspend fun borrarChat(idChat: String) = firestore.collection("chats").document(idChat).delete().await()

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

                trySend(mensajes)
            }

        awaitClose {
            listener.remove()
        }
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


}

