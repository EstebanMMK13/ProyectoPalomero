package com.example.proyectopalomero.data.repository

import android.util.Patterns
import com.example.proyectopalomero.data.dao.UsuarioDao
import com.example.proyectopalomero.data.model.UsuarioDto
import com.example.proyectopalomero.data.model.UsuarioFire


class UsuarioRepository(private val usuarioDao: UsuarioDao) {


    private var listaAvatares = listOf<String>(
        "https://i.postimg.cc/7Z8CYtWC/avatar-Paloma-Azul.jpg",
        "https://i.postimg.cc/15NnS1Jh/avatar-Paloma-Turquesa.jpg",
        "https://i.postimg.cc/HLDrWxqg/avatar-Paloma-Roja-Suave.jpg",
        "https://i.postimg.cc/RF3Ccz5R/avatar-Paloma-Roja-Fuerte.jpg",
        "https://i.postimg.cc/fL6zCgMh/avatar-Paloma-Ocre.jpg",
        "https://i.postimg.cc/j2PsFQjn/avatar-Paloma-Naranja.jpg",
        "https://i.postimg.cc/TYv2WSkV/avatar-Paloma-Rosa.jpg",
        "https://i.postimg.cc/GhPLPQNZ/avatar-Paloma-Negro.jpg",
        "https://i.postimg.cc/Jh0RLWnL/avatar-Paloma-Verde-Osucro.jpg",
        "https://i.postimg.cc/N0nBGP9r/avatar-Paloma-Verde.jpg",
        "https://i.postimg.cc/4xNg6Tp9/avatar-Paloma-Principal.jpg",
        "https://i.postimg.cc/rwnLgY9Y/avatar-Paloma-Secundario.jpg"
    )


    fun getCurrentUser(): Boolean {
        return usuarioDao.getCurrentUser() != null
    }

    suspend fun comprobarUsuarioExiste(): Boolean {
        return usuarioDao.comprobarUsuarioExiste()
    }

    suspend fun login(email: String, password: String): Boolean {
        return usuarioDao.login(email, password)
    }

    fun signOut(){
        usuarioDao.signOut()
    }

    fun comprobarNickName(nickname: String): Boolean {
        return nickname.startsWith("@")
    }

    suspend fun obtenerUsuarios(): List<UsuarioFire> {
        return usuarioDao.obtenerUsuarios()
    }

    suspend fun obtenerUsuarioActual(): UsuarioFire {
        return usuarioDao.obtenerUsuarioActual()
    }

    suspend fun obtenerUsuarioPorId(id: String): UsuarioFire? {
        return usuarioDao.obtenerUsuarioPorId(id)
    }

    suspend fun obtenerUsuarioPorNickname(nickname: String): UsuarioFire? {
        return usuarioDao.obtenerUsuarioPorNickname(nickname)
    }

    fun comprobarCorreoValido(correo: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }

    suspend fun verificarNicknameExistente(nickname: String): Boolean {
        return usuarioDao.verificarNicknameExistente(nickname)
    }

    suspend fun registrarUsuario(usuario: UsuarioFire, password: String): Boolean {
        usuario.fotoPerfil = listaAvatares.random()
        var usuarioDto = UsuarioDto(
            nombre = usuario.nombre,
            nickname = usuario.nickname,
            correo = usuario.correo,
            fotoPerfil = usuario.fotoPerfil
        )
        return usuarioDao.registrarUsuario(usuarioDto, password)
    }

    fun validarRegistro(usuario: UsuarioFire, password: String): String? {
        return when {
            usuario.nombre.isNullOrEmpty() -> "El nombre está vacío"
            !comprobarNickName(usuario.nickname ?: "") -> "El usuario debe empezar con @"
            !comprobarCorreoValido(usuario.correo ?: "") -> "El formato del correo no es válido"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    suspend fun actualizarUsuario(idUsuario: String, usuario: UsuarioFire) {

        val campos = mapOf(
            "nombre" to usuario.nombre,
            "nickname" to usuario.nickname,
            "correo" to usuario.correo,
            "fotoPerfil" to usuario.fotoPerfil
        )
        // Filtramos para quedarnos solo con campos que NO sean null
        val nuevoUsuario = campos.filterValues { it != null }
        usuarioDao.actualizarUsuario(idUsuario, nuevoUsuario)
    }

    fun listaDeAvatares(): List<String> {
        return listaAvatares
    }


}