package com.example.proyectopalomero.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.proyectopalomero.data.utils.Routes

fun NavController.safeNavigate(route: String) {
    this.navigate(route) {
        launchSingleTop = true
        popUpTo(Routes.FEED) {
            inclusive = false
        }
    }
}

val pantallasSinTopBar = listOf(
    Routes.SPLASH,
    Routes.LOGIN,
    Routes.REGISTER,
)

val pantallasSinBottomBar = listOf(
    Routes.SPLASH,
    Routes.LOGIN,
    Routes.REGISTER,
)

val pantallasSinFab = listOf(
    Routes.SPLASH,
    Routes.LOGIN,
    Routes.REGISTER,
    Routes.TIEMPO,
    Routes.EDITAR_PERFIL,
    Routes.AGREGAR_PUBLICACION
)

data class FabConfig(val icon: ImageVector, val onClick: () -> Unit)

fun getFabConfig(route: String?, navController: NavController): FabConfig? {
    return when (route) {
        Routes.CHATS -> FabConfig(Icons.Filled.Email) {
            // Acción para nuevo mensaje
        }
        else -> FabConfig(Icons.Filled.Add) {
            navController.navigate(Routes.AGREGAR_PUBLICACION) {
                launchSingleTop = true
            }
        }
    }
}