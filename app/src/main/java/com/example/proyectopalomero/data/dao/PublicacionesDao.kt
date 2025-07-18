package com.example.proyectopalomero.data.dao

import android.util.Log
import com.example.proyectopalomero.data.model.PublicacionDTO
import com.example.proyectopalomero.data.model.PublicacionFire
import com.example.proyectopalomero.data.utils.Resultado
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
class PublicacionesDao(
    private val firestore: FirebaseFirestore
) {

    fun obtenerPublicacionesEnTiempoReal(): Flow<List<PublicacionFire>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = firestore.collection("publicaciones")
            .orderBy("fechaCreacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val publicaciones = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PublicacionFire::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(publicaciones)
            }

        awaitClose { listenerRegistration.remove() }
    }

    fun obtenerPublicacionesPorUsuarioEnTiempoReal(idUsuario: String): Flow<List<PublicacionFire>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = firestore.collection("publicaciones")
            .whereEqualTo("usuario", idUsuario)
            .orderBy("fechaCreacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val publicaciones = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PublicacionFire::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(publicaciones)
            }

        awaitClose { listenerRegistration.remove() }
    }

    suspend fun agregarPublicacion(publicacion: PublicacionDTO): Resultado<String> {
        return try {
            val nuevoDocumento = firestore.collection("publicaciones").document()
            val id = nuevoDocumento.id
            nuevoDocumento.set(publicacion).await()
            Resultado.Exito(id)
        } catch (e: Exception) {
            Resultado.Error("Error agregando publicación", e)
        }
    }

    suspend fun eliminarPublicacion(idPublicacion: String): Resultado<Unit> {
        return try {
            firestore.collection("publicaciones").document(idPublicacion).delete().await()
            Resultado.Exito(Unit)
        } catch (e: Exception) {
            Resultado.Error("Error eliminando publicación", e)
        }
    }

    suspend fun darMeGustaPublicacion(idPublicacion: String, idUsuario: String): Resultado<Unit> {
        return try {
            firestore.collection("publicaciones")
                .document(idPublicacion)
                .update("listaMeGustas", FieldValue.arrayUnion(idUsuario))
                .await()
            Resultado.Exito(Unit)
        } catch (e: Exception) {
            Resultado.Error("Error al dar me gusta", e)
        }
    }

    suspend fun quitarMeGustaPublicacion(idPublicacion: String, idUsuario: String): Resultado<Unit> {
        return try {
            firestore.collection("publicaciones")
                .document(idPublicacion)
                .update("listaMeGustas", FieldValue.arrayRemove(idUsuario))
                .await()
            Resultado.Exito(Unit)
        } catch (e: Exception) {
            Resultado.Error("Error al quitar me gusta", e)
        }
    }
}
