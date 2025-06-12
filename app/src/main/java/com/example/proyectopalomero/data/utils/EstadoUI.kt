package com.example.proyectopalomero.data.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

sealed class EstadoUI<out T> {
    object Cargando : EstadoUI<Nothing>()
    object Vacio : EstadoUI<Nothing>()
    data class Exito<out T>(val datos: T) : EstadoUI<T>()
    data class Error(val mensaje: String, val tipo: TipoError = TipoError.SnackbarOnly // valor por defecto si quieres
    ) : EstadoUI<Nothing>() { enum class TipoError { SnackbarOnly, General } }
}

val errorGeneral = EstadoUI.Error.TipoError.General
val errorSnackBar = EstadoUI.Error.TipoError.SnackbarOnly

@Composable
fun <T> EstadoUIHandler(
    estadoUI: EstadoUI<T>,
    snackbarHostState: SnackbarHostState,
    onExito: @Composable () -> Unit
) {
    when (estadoUI) {
        is EstadoUI.Cargando -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is EstadoUI.Error -> {
            if (estadoUI.tipo == EstadoUI.Error.TipoError.SnackbarOnly) {
                LaunchedEffect(estadoUI) {
                    snackbarHostState.showSnackbar(estadoUI.mensaje)
                }
                onExito()
            }else{
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(estadoUI.mensaje)
                }
            }
        }
        is EstadoUI.Exito -> {
            onExito()
        }
        EstadoUI.Vacio -> Unit
    }
}
