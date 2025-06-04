package com.example.proyectopalomero.data.dao

import com.example.proyectopalomero.data.model.UsuarioDto
import com.example.proyectopalomero.data.model.UsuarioFire
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsuarioDao(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    fun getCurrentUser() = auth.currentUser

    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun signOut (){
        auth.signOut()
    }

    suspend fun comprobarUsuarioExiste(): Boolean {
        return try {
            var usuario = firestore.collection("usuarios").document(auth.currentUser?.uid.toString()).get().await()
            return usuario.exists()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerUsuarios(): List<UsuarioFire> {
        val query = firestore.collection("usuarios").get().await()
        return query.documents.map { doc ->
            doc.toObject(UsuarioFire::class.java)?.copy(id = doc.id)
                ?: UsuarioFire(nickname = "Usuario desconocido")
        }
    }

    suspend fun obtenerUsuarioActual(): UsuarioFire {
        val id = auth.currentUser?.uid
        val doc = firestore.collection("usuarios").document(id?: "").get().await()
        return doc.toObject(UsuarioFire::class.java)?.copy(id = doc.id) ?: UsuarioFire(nickname = "Usuario desconocido")
    }

    suspend fun obtenerUsuarioPorId(idUsuario: String): UsuarioFire? {
        val doc = firestore.collection("usuarios").document(idUsuario).get().await()
        return doc.toObject(UsuarioFire::class.java)?.copy(id = doc.id)
    }

    suspend fun obtenerUsuarioPorNickname(nickname: String): UsuarioFire? {
        val query = firestore.collection("usuarios").whereEqualTo("nickname", nickname).get().await()
        return query.documents.firstOrNull()?.toObject(UsuarioFire::class.java)?.copy(id = query.documents.first().id)
    }

    suspend fun registrarUsuario(usuario: UsuarioDto, password: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(usuario.correo.toString(), password).await()
            firestore.collection("usuarios").document(auth.uid.toString()).set(usuario).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun verificarNicknameExistente(nickname: String): Boolean {
        val query = firestore.collection("usuarios")
            .whereEqualTo("nickname", nickname)
            .get()
            .await()
        return !query.isEmpty
    }

    suspend fun actualizarUsuario(idUsuario: String, nuevoUsuario: Map<String, String?>) {
        firestore.collection("usuarios").document(idUsuario).update(nuevoUsuario).await()
    }
}