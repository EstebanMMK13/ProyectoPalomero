package com.example.proyectopalomero.ui.theme.screens.Perfil

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.model.PublicacionFire
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.utils.EstadoUI
import com.example.proyectopalomero.data.utils.EstadoUIHandler
import com.example.proyectopalomero.data.utils.MiNavigationBar
import com.example.proyectopalomero.data.utils.Routes
import com.example.proyectopalomero.navigation.safeNavigate
import com.example.proyectopalomero.ui.theme.Components.Publicacion.MostrarPublicacion
import com.example.proyectopalomero.ui.theme.screens.Feed.FeedFab
import com.example.proyectopalomero.ui.theme.screens.Feed.FeedTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    snackbarHostState: SnackbarHostState,
    navHostController: NavHostController,
    usuarioViewModel: UsuarioViewModel,
    perfilViewModel: PerfilViewModel,
    cerrarSesion: MutableState<Boolean>
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val usuarioActual by usuarioViewModel.usuario.collectAsState()
    val estadoUI by perfilViewModel.estadoUI.collectAsState()

    // Escuchar publicaciones para el usuario actual
    LaunchedEffect(usuarioActual?.id) {
        usuarioActual?.id?.let { perfilViewModel.observarPublicacionesPorUsuario(it) }
    }

    // Mostrar error en snackbar si hay
    LaunchedEffect(estadoUI) {
        if (estadoUI is EstadoUI.Error) {
            val mensaje = (estadoUI as EstadoUI.Error).mensaje
            snackbarHostState.showSnackbar(mensaje)
        }
    }

    Scaffold(
        topBar = { FeedTopAppBar(scrollBehavior = scrollBehavior, nicknameTop = usuarioActual?.nickname ?: "Desconocido") },
        bottomBar = { MiNavigationBar(navHostController) },
        floatingActionButton = { FeedFab(navHostController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        EstadoUIHandler(estadoUI,snackbarHostState) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MiPerfil(
                    usuario = usuarioActual,
                    navHostController = navHostController,
                    perfilViewModel = perfilViewModel,
                    usuarioViewModel = usuarioViewModel,
                    cerrarSesion = cerrarSesion
                )
                MisPublicaciones(scrollBehavior = scrollBehavior, usuario = usuarioActual, perfilViewModel = perfilViewModel)
            }
        }
    }
}


//Funcion para mostrar el perfil del usuario
@Composable
fun MiPerfil(
    modifier: Modifier = Modifier,
    usuario: UsuarioFire?,
    navHostController: NavHostController,
    perfilViewModel: PerfilViewModel,
    usuarioViewModel: UsuarioViewModel,
    cerrarSesion: MutableState<Boolean>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Cabecera: Imagen y detalles del usuario
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = usuario?.fotoPerfil,
                modifier = Modifier
                    .weight(1f)
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentDescription = "Imagen usuario",
                contentScale = ContentScale.Crop
            )


            Spacer(modifier = Modifier.width(15.dp))

            Column(
                modifier = modifier.weight(1.5f)
            ) {
                Text(
                    text = usuario?.nombre ?: "Usuario desconocido",
                    fontSize = 25.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Text(
                    text = usuario?.nickname ?: "Nickname desconocido",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            IconButton(
                modifier = modifier.weight(0.5f),
                onClick = { usuarioViewModel.cambiarTema() }) {
                val icon = if (usuarioViewModel.temaOscuro.collectAsState().value) Icons.Filled.LightMode else Icons.Filled.DarkMode
                Icon(imageVector = icon, contentDescription = "Cambiar tema")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Button(
                //Boton para editar perfil
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp),
                onClick = { navHostController.safeNavigate(Routes.EDITAR_PERFIL) },
            ) {
                Text(
                    text = "Editar perfil",
                    fontSize = 15.sp
                )
            }

            Button(
                //Boton para cerrar sesión
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color.Red
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp),
                onClick = {
                    perfilViewModel.singOut()
                    cerrarSesion.value = true
                    navHostController.navigate(Routes.LOGIN) {
                        launchSingleTop = true
                        popUpTo(0) { inclusive = true }
                    }
                    usuarioViewModel.limpiarUsuario()
                }
            ) {
                Text(
                    text = "Cerrar sesión",
                    fontSize = 15.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPublicaciones(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    usuario: UsuarioFire?,
    perfilViewModel: PerfilViewModel
) {
    val publicaciones by perfilViewModel.publicaciones.collectAsState()

    if (publicaciones.isEmpty()) {
        Text(
            text = "No tienes publicaciones aún.",
            modifier = modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            items(publicaciones.size) { index ->
                val publicacion = publicaciones[index]
                if (usuario != null) {
                    MostrarPublicacion(publicacion, usuario, usuario, perfilViewModel)
                }
            }
        }
    }
}

