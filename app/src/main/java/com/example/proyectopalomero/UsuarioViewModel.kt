package com.example.proyectopalomero
import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.ui.theme.theme.ThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private var _usuario = MutableStateFlow<UsuarioFire?>(null)
    val usuario: StateFlow<UsuarioFire?> = _usuario

    private val _temaOscuro = MutableStateFlow(false)
    val temaOscuro: StateFlow<Boolean> = _temaOscuro

    init {
        viewModelScope.launch {
            ThemePreference.getThemeMode(context).collect {
                _temaOscuro.value = it
            }
        }
    }

    fun establecerUsuario(usuario: UsuarioFire) {
        val actual = _usuario.value ?: UsuarioFire()

        _usuario.value = actual.copy(
            id = usuario.id ?: actual.id,
            nombre = usuario.nombre ?: actual.nombre,
            nickname = usuario.nickname ?: actual.nickname,
            correo = usuario.correo ?: actual.correo,
            fotoPerfil = usuario.fotoPerfil ?: actual.fotoPerfil
        )
    }

    fun limpiarUsuario() {
        _usuario.value = null
    }

    fun cambiarTema() {
        viewModelScope.launch {
            val nuevoValor = !_temaOscuro.value
            ThemePreference.saveThemeMode(context, nuevoValor)
        }
    }
}