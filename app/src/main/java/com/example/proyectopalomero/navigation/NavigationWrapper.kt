package com.example.proyectopalomero.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.repository.AppContainer
import com.example.proyectopalomero.data.utils.Routes
import com.example.proyectopalomero.ui.theme.screens.Chats.ChatViewModel
import com.example.proyectopalomero.ui.theme.screens.Chats.ChatViewModelFactory
import com.example.proyectopalomero.ui.theme.screens.Chats.ChatsScreen
import com.example.proyectopalomero.ui.theme.screens.Chats.MensajesScreen
import com.example.proyectopalomero.ui.theme.screens.Chats.NuevoChatScreen
import com.example.proyectopalomero.ui.theme.screens.Feed.AgregarPublicacionScreen
import com.example.proyectopalomero.ui.theme.screens.Feed.FeedScreen
import com.example.proyectopalomero.ui.theme.screens.Feed.FeedViewModel
import com.example.proyectopalomero.ui.theme.screens.Feed.FeedViewModelFactory
import com.example.proyectopalomero.ui.theme.screens.Login.LoginScreen
import com.example.proyectopalomero.ui.theme.screens.Perfil.EditarPerfilScreen
import com.example.proyectopalomero.ui.theme.screens.Perfil.PerfilScreen
import com.example.proyectopalomero.ui.theme.screens.Perfil.PerfilViewModel
import com.example.proyectopalomero.ui.theme.screens.Perfil.PerfilViewModelFactory
import com.example.proyectopalomero.ui.theme.screens.Registro.RegisterScreen
import com.example.proyectopalomero.ui.theme.screens.Tiempo.WeatherScreen
import com.example.proyectopalomero.ui.theme.screens.Tiempo.WeatherViewModel
import com.example.proyectopalomero.ui.theme.screens.splash.SplashScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationWrapper(
    temaOscuro: Boolean,
    snackbarHostState: SnackbarHostState,
    weatherViewModel: WeatherViewModel,
    usuarioViewModel: UsuarioViewModel,
) {
    lateinit var navHostController: NavHostController

    navHostController = rememberNavController()
    val currentBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    var cerrarSesion = remember { mutableStateOf(false) }

    val feedViewModel: FeedViewModel = viewModel(
        factory = FeedViewModelFactory(AppContainer.publicacionesRepository, AppContainer.usuarioRepository)
    )

    val perfilViewModel: PerfilViewModel = viewModel(
        factory = PerfilViewModelFactory(AppContainer.usuarioRepository, AppContainer.publicacionesRepository)
    )

    val chatsViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(AppContainer.chatsRepository, AppContainer.usuarioRepository)
    )

    NavHost(navController = navHostController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) { SplashScreen(navHostController,usuarioViewModel) }
        composable(route = Routes.LOGIN) {
            LoginScreen(
                snackbarHostState,
                navHostController,
                usuarioViewModel
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(snackbarHostState,navHostController)
        }
        composable(Routes.FEED,
           // enterTransition = { AnimacionEntrada() },
          //  exitTransition = { AnimacionSalida() }
        ) {
            FeedScreen(snackbarHostState,navHostController,usuarioViewModel ,feedViewModel)
        }
        composable(Routes.PERFIL,
          //  enterTransition = { AnimacionEntrada() },
          //  exitTransition = { AnimacionSalida() }
        ) {
            PerfilScreen(snackbarHostState,navHostController,usuarioViewModel,perfilViewModel,cerrarSesion)
        }
        composable(Routes.AGREGAR_PUBLICACION,
          //  enterTransition = { AnimacionEntrada() },
          //  exitTransition = { AnimacionSalida() }
        ) {
            AgregarPublicacionScreen(snackbarHostState,navHostController,usuarioViewModel)
        }
        composable(Routes.EDITAR_PERFIL) {
            EditarPerfilScreen(snackbarHostState,navHostController,usuarioViewModel,perfilViewModel)
        }
        composable(Routes.TIEMPO,
           // enterTransition = { AnimacionEntrada() },
           // exitTransition = { AnimacionSalida() }
        ) {
            WeatherScreen(snackbarHostState,navHostController,weatherViewModel)
        }
        composable(Routes.CHATS,
          //  enterTransition = { AnimacionEntrada() },
          //  exitTransition = { AnimacionSalida() }
        ) {
            ChatsScreen(snackbarHostState,navHostController,chatsViewModel,usuarioViewModel)
        }
        composable(Routes.MENSAJES,
            //  enterTransition = { AnimacionEntrada() },
            //  exitTransition = { AnimacionSalida() }
        ) {
            MensajesScreen(snackbarHostState,navHostController,chatsViewModel,usuarioViewModel)
        }
        composable(Routes.NUEVO_CHAT,
            //  enterTransition = { AnimacionEntrada() },
            //  exitTransition = { AnimacionSalida() }
        ) {
            NuevoChatScreen(snackbarHostState,navHostController,usuarioViewModel,chatsViewModel)
        }

    }

    if (cerrarSesion.value) {
        usuarioViewModel.limpiarUsuario()
        feedViewModel.limpiarDatos()
        chatsViewModel.limpiarDatos()
        perfilViewModel.limpiarDatos()
        weatherViewModel.limpiarDatos()

        cerrarSesion.value = false
    }


}
