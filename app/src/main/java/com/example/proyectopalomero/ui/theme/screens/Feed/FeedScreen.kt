package com.example.proyectopalomero.ui.theme.screens.Feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.proyectopalomero.R
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.utils.MiNavigationBar
import com.example.proyectopalomero.data.utils.Routes
import com.example.proyectopalomero.navigation.FabConfig
import com.example.proyectopalomero.navigation.safeNavigate
import com.example.proyectopalomero.ui.theme.Components.Publicacion.MostrarPublicacion
import com.example.proyectopalomero.ui.theme.theme.fuenteRetro


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    temaOscuro: Boolean,
    navHostController: NavHostController,
    usuarioViewModel: UsuarioViewModel,
    feedViewModel: FeedViewModel
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val isLoading by feedViewModel.isLoading.collectAsState(initial = true)
    val publicaciones by feedViewModel.publicaciones.collectAsState()
    val usuariosMap = feedViewModel.usuariosMap

    val usuarioActual = usuarioViewModel.usuario.collectAsState().value

    LaunchedEffect(Unit) {
        val usuario = feedViewModel.obtenerUsuarioActual()
        usuarioViewModel.establecerUsuario(usuario)
        feedViewModel.obtenerPublicaciones()
    }

    Scaffold(
        topBar = { FeedTopAppBar(scrollBehavior = scrollBehavior, nicknameTop = usuarioActual?.nickname ?: "Desconocido") },
        bottomBar = { MiNavigationBar(navHostController)},
        floatingActionButton = { FeedFab(navHostController) },

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
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
            onClick = {navHostController.safeNavigate(Routes.AGREGAR_PUBLICACION)},
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
