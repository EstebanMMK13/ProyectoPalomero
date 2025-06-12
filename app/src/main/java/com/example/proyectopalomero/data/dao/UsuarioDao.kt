package com.example.proyectopalomero.data.dao

import android.util.Log
import com.example.proyectopalomero.data.model.UsuarioDto
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.utils.Resultado
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsuarioDao(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    fun getCurrentUser() = auth.currentUser

    suspend fun login(email: String, password: String): Resultado<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Resultado.Exito(true)
        } catch (e: FirebaseAuthInvalidUserException) {
            Resultado.Error("Usuario no encontrado", e)

        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Resultado.Error("Email o contraseña incorrectos", e)

        } catch (e: FirebaseNetworkException) {
            Resultado.Error("Sin conexión a internet")

        }catch (e: Exception) {
            Resultado.Error("Error inesperado: ${e.localizedMessage}", e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun comprobarUsuarioExiste(): Resultado<Boolean> {
        return try {
            val usuario = firestore.collection("usuarios")
                .document(auth.currentUser?.uid ?: "")
                .get()
                .await()
            Resultado.Exito(usuario.exists())
        } catch (e: Exception) {
            Resultado.Error("Error comprobando usuario", e)
        }
    }

    suspend fun obtenerUsuarios(): Resultado<List<UsuarioFire>> {
        return try {
            val query = firestore.collection("usuarios").get().await()
            val usuarios = query.documents.map { doc ->
                doc.toObject(UsuarioFire::class.java)?.copy(id = doc.id)
                    ?: UsuarioFire(nickname = "Usuario desconocido")
            }
            Resultado.Exito(usuarios)
        } catch (e: Exception) {
            Resultado.Error("Error obteniendo usuarios", e)
        }
    }

    suspend fun obtenerUsuarioActual(): Resultado<UsuarioFire> {
        return try {
            val id = auth.currentUser?.uid ?: ""
            val doc = firestore.collection("usuarios").document(id).get().await()
            val usuario = doc.toObject(UsuarioFire::class.java)?.copy(id = doc.id)
                ?: UsuarioFire(nickname = "Usuario desconocido")
            Resultado.Exito(usuario)
        } catch (e: Exception) {
            Resultado.Error("Error obteniendo usuario actual", e)
        }
    }

    suspend fun obtenerUsuarioPorId(idUsuario: String): Resultado<UsuarioFire?> {
        return try {
            val doc = firestore.collection("usuarios").document(idUsuario).get().await()
            val usuario = doc.toObject(UsuarioFire::class.java)?.copy(id = doc.id)
            Resultado.Exito(usuario)
        } catch (e: Exception) {
            Resultado.Error("Error obteniendo usuario por ID", e)
        }
    }

    suspend fun obtenerUsuarioPorNickname(nickname: String): Resultado<UsuarioFire?> {
        return try {
            val query = firestore.collection("usuarios")
                .whereEqualTo("nickname", nickname)
                .get()
                .await()
            val usuario = query.documents.firstOrNull()?.toObject(UsuarioFire::class.java)
                ?.copy(id = query.documents.first().id)
            Resultado.Exito(usuario)
        } catch (e: Exception) {
            Resultado.Error("Error obteniendo usuario por nickname", e)
        }
    }

    suspend fun registrarUsuario(usuario: UsuarioDto, password: String): Resultado<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(usuario.correo.toString(), password).await()
            firestore.collection("usuarios").document(auth.uid.toString()).set(usuario).await()
            Resultado.Exito(true)
        } catch (e: Exception) {
            Resultado.Error("Error registrando usuario", e)
        }
    }

    suspend fun verificarNicknameExistente(nickname: String): Resultado<Boolean> {
        return try {
            val query = firestore.collection("usuarios")
                .whereEqualTo("nickname", nickname)
                .get()
                .await()
            Resultado.Exito(!query.isEmpty)
        } catch (e: Exception) {
            Resultado.Error("Error verificando nickname", e)
        }
    }

    suspend fun actualizarUsuario(idUsuario: String, nuevoUsuario: Map<String, String?>): Resultado<Unit> {
        return try {
            firestore.collection("usuarios").document(idUsuario).update(nuevoUsuario).await()
            Resultado.Exito(Unit)
        } catch (e: Exception) {
            Resultado.Error("Error actualizando usuario", e)
        }
    }
}
