package com.example.proyectopalomero.data.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.proyectopalomero.navigation.safeNavigate
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun utilsMostrarToast(context: Context, mensaje: String) {
    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
}

object Routes {
    const val SPLASH = "SplashScreen"
    const val LOGIN = "LoginScreen"
    const val REGISTER = "RegisterScreen"
    const val FEED = "FeedScreen"
    const val AGREGAR_PUBLICACION = "AgregarPublicacionScreen"
    const val PERFIL = "PerfilScreen"
    const val EDITAR_PERFIL = "EditarPerfilScreen"
    const val TIEMPO = "TiempoScreen"
    const val CHATS = "ChatsScreen"
    const val MENSAJES = "MensajesScreen"
    const val NUEVO_MENSAJE = "NuevoMensajeScreen"
}

fun formatearHora(timestamp: Timestamp): String {
    val date = timestamp.toDate()

    val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formatoFecha = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

    val ahora = Calendar.getInstance()
    val fechaTimestamp = Calendar.getInstance().apply { time = date }

    val mismaFecha = ahora.get(Calendar.YEAR) == fechaTimestamp.get(Calendar.YEAR) &&
            ahora.get(Calendar.DAY_OF_YEAR) == fechaTimestamp.get(Calendar.DAY_OF_YEAR)

    return if (mismaFecha) {
        formatoHora.format(date) // Solo hora si es hoy
    } else {
        formatoFecha.format(date) // Solo día/mes/año si es otro día
    }
}

var UtilsEsOscuro : Boolean = true

fun UtilsCambiarTema(cambio: Boolean) {
    UtilsEsOscuro = cambio
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiNavigationBar(navController: NavController) {

    val temaOscuro = UtilsEsOscuro

    val items = listOf("Feed", "Mensajes", "Tiempo", "Perfil")
    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Email, Icons.Default.LocationOn, Icons.Filled.AccountBox)
    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.Email, Icons.Default.LocationOn, Icons.Outlined.AccountBox)

    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route

    // Calcular selectedItem basado en la ruta actual
    val selectedItem = remember(currentRoute) {
        when (currentRoute) {
            Routes.FEED -> 0
            Routes.CHATS -> 1
            Routes.TIEMPO -> 2
            Routes.PERFIL -> 3
            else -> 0
        }
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.height(56.dp)
    ) {
        items.forEachIndexed { indice, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == indice) selectedIcons[indice] else unselectedIcons[indice],
                        contentDescription = item,
                        tint = if (temaOscuro) Color.White else Color.Black
                    )
                },
                selected = selectedItem == indice,
                onClick = {
                    if (selectedItem != indice) { // Evita navegaciones repetidas
                        when (indice) {
                            0 -> navController.safeNavigate(Routes.FEED)
                            1 -> navController.safeNavigate(Routes.CHATS)
                            2 -> navController.safeNavigate(Routes.TIEMPO)
                            3 -> navController.safeNavigate(Routes.PERFIL)
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = if (temaOscuro) Color.LightGray else Color.Gray,
                    selectedIconColor = if (temaOscuro) Color.White else MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
