package com.example.proyectopalomero.ui.theme.screens.Feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.proyectopalomero.R
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.utils.EstadoUI
import com.example.proyectopalomero.data.utils.MiNavigationBar
import com.example.proyectopalomero.data.utils.Routes
import com.example.proyectopalomero.navigation.safeNavigate
import com.example.proyectopalomero.ui.theme.Components.Publicacion.MostrarPublicacion
import com.example.proyectopalomero.ui.theme.theme.fuenteRetro


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    snackbarHostState: SnackbarHostState,
    navHostController: NavHostController,
    usuarioViewModel: UsuarioViewModel,
    feedViewModel: FeedViewModel
) {
    val estadoUI by feedViewModel.estadoUI.observeAsState(initial = EstadoUI.Vacio)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val publicaciones by feedViewModel.publicaciones.collectAsState()
    val usuariosMap = feedViewModel.usuariosMap

    val usuarioActual = usuarioViewModel.usuario.collectAsState().value

    LaunchedEffect(Unit) {
        val usuario = feedViewModel.obtenerUsuarioActual()
        usuarioViewModel.establecerUsuario(usuario)
        feedViewModel.obtenerPublicaciones()
    }

    Scaffold(
        topBar = {
            FeedTopAppBar(
                scrollBehavior = scrollBehavior,
                nicknameTop = usuarioActual?.nickname ?: "Desconocido"
            )
        },
        bottomBar = { MiNavigationBar(navHostController) },
        floatingActionButton = { FeedFab(navHostController) },

        ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (estadoUI) {
                is EstadoUI.Cargando -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is EstadoUI.Error -> {
                    Text(
                        text = (estadoUI as EstadoUI.Error).mensaje,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        fontFamily = fuenteRetro
                    )
                }

                is EstadoUI.Exito -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                    ) {
                        items(publicaciones.size) { index ->
                            val publicacion = publicaciones[index]
                            val usuario = usuariosMap[publicacion.usuario]
                            if (usuario != null) {
                                MostrarPublicacion(
                                    publicacion,
                                    usuario,
                                    usuarioActual ?: UsuarioFire(nickname = "Desconocido"),
                                    feedViewModel
                                )
                            }
                        }
                    }
                }

                is EstadoUI.Vacio -> {}
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    nicknameTop: String
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.secondary,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        ),
        navigationIcon = {
            Image(
                modifier = Modifier
                    .size(70.dp)
                    .fillMaxWidth(),
                painter = painterResource(R.drawable.palomero_logo),
                contentDescription = "Logo"
            )
        },
        title = {
            Text(
                text = nicknameTop,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 70.dp),
                textAlign = TextAlign.Center,
                fontFamily = fuenteRetro
            )
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun FeedFab(navHostController: NavHostController) {
    FloatingActionButton(
        onClick = { navHostController.safeNavigate(Routes.AGREGAR_PUBLICACION) },
        containerColor = MaterialTheme.colorScheme.background,
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "FAB action",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}
