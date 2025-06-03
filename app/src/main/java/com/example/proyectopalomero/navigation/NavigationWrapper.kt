package com.example.proyectopalomero.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.repository.AppContainer
import com.example.proyectopalomero.data.utils.Routes
import com.example.proyectopalomero.ui.theme.screens.Chats.ChatViewModel
import com.example.proyectopalomero.ui.theme.screens.Chats.ChatViewModelFactory
import com.example.proyectopalomero.ui.theme.screens.Chats.ChatsScreen
import com.example.proyectopalomero.ui.theme.screens.Chats.Mensajes.MensajesScreen
import com.example.proyectopalomero.ui.theme.screens.Feed.AgregarPublicacionScreen
import com.example.proyectopalomero.ui.theme.screens.Feed.FeedScreen
import com.example.proyectopalomero.ui.theme.screens.Feed.FeedViewModel
import com.example.proyectopalomero.ui.theme.screens.Feed.FeedViewModelFactory
import com.example.proyectopalomero.ui.theme.screens.LoginYRegister.LoginScreen
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
    navHostController: NavHostController,
    innerPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    weatherViewModel: WeatherViewModel,
    usuarioViewModel: UsuarioViewModel,
) {
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
                Modifier.padding(innerPadding),
                snackbarHostState,
                navHostController,
                usuarioViewModel
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(Modifier.padding(innerPadding),snackbarHostState,navHostController)
        }
        composable(Routes.FEED,
           // enterTransition = { AnimacionEntrada() },
          //  exitTransition = { AnimacionSalida() }
        ) {
            FeedScreen(Modifier.padding(innerPadding),usuarioViewModel,feedViewModel)
        }
        composable(Routes.PERFIL,
          //  enterTransition = { AnimacionEntrada() },
          //  exitTransition = { AnimacionSalida() }
        ) {
            PerfilScreen(temaOscuro,Modifier.padding(innerPadding), navHostController,usuarioViewModel,perfilViewModel,cerrarSesion)
        }
        composable(Routes.AGREGAR_PUBLICACION,
          //  enterTransition = { AnimacionEntrada() },
          //  exitTransition = { AnimacionSalida() }
        ) {
            AgregarPublicacionScreen(Modifier.padding(innerPadding), navHostController, feedViewModel,usuarioViewModel)
        }
        composable(Routes.EDITAR_PERFIL) {
            EditarPerfilScreen(Modifier.padding(innerPadding), navHostController,usuarioViewModel,perfilViewModel)
        }
        composable(Routes.TIEMPO,
           // enterTransition = { AnimacionEntrada() },
           // exitTransition = { AnimacionSalida() }
        ) {
            WeatherScreen(Modifier.padding(innerPadding), weatherViewModel)
        }
        composable(Routes.CHATS,
          //  enterTransition = { AnimacionEntrada() },
          //  exitTransition = { AnimacionSalida() }
        ) {
            ChatsScreen(Modifier.padding(innerPadding), navHostController,chatsViewModel,usuarioViewModel)
        }
        composable(
            route = "${Routes.MENSAJES}/{idChat}",
            arguments = listOf(navArgument("idChat") { type = NavType.StringType })
        ) { backStackEntry ->
            val idPublicacion = backStackEntry.arguments?.getString("idChat")
            MensajesScreen(Modifier.padding(innerPadding), usuarioViewModel, idPublicacion!!)
        }

    }

    if (cerrarSesion.value) {
        usuarioViewModel.limpiarUsuario()
        feedViewModel.limpiarDatos()
        chatsViewModel.limipiarDatos()
        perfilViewModel.limpiarDatos()
        weatherViewModel.limpiarDatos()

        cerrarSesion.value = false
    }


}
