package com.example.proyectopalomero.data.dao

import android.system.Os.close
import android.util.Log
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
        val ref1 = firestore.collection("chats")
            .whereEqualTo("idUsuario1", idUsuario)

        val ref2 = firestore.collection("chats")
            .whereEqualTo("idUsuario2", idUsuario)

        val listener1 = ref1.addSnapshotListener { snapshot1, error1 ->
            if (error1 != null) {
                close(error1)
                return@addSnapshotListener
            }

            val snapshot2 = ref2.get()
            snapshot2.addOnSuccessListener { result2 ->
                val documentos = (snapshot1?.documents ?: emptyList()) + result2.documents

                val chats = documentos
                    .distinctBy { it.id }
                    .mapNotNull { doc ->
                        val chat = doc.toObject(ChatFire::class.java)
                        chat?.copy(id = doc.id)
                    }

                trySend(chats).isSuccess
            }.addOnFailureListener {
                close(it)
            }
        }

        // Cancelación del flow
        awaitClose {
            listener1.remove()
        }
    }


    fun actualizarChat(idChat : String, mensaje: String){
        val chatRef = firestore.collection("chats").document(idChat)
        chatRef.update("fechaMensaje", Timestamp.now(), "ultimoMensaje",mensaje)
    }


    fun obtenerMensajes(idChat: String): Flow<List<MensajeFire>> = callbackFlow {
        val listener = firestore
            .collection("chats")
            .document(idChat)
            .collection("mensajes")
            .orderBy("fecha") // Ordena por fecha si tienes un campo timestamp
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
        firestore.collection("chats").document(idChat).collection("mensajes").document().set(mensaje)
            .addOnSuccessListener {
                Log.d("ChatDao", "Mensaje enviado con éxito")
            }
            .addOnFailureListener {
                Log.e("ChatDao", "Error al enviar el mensaje", it)
            }
    }


}

