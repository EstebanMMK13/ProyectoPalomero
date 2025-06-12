package com.example.proyectopalomero.ui.theme.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectopalomero.R
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.repository.AppContainer
import com.example.proyectopalomero.data.utils.Routes
import kotlinx.coroutines.delay

// Pantalla de carga
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(
    navHostController: NavController,
    usuarioViewModel: UsuarioViewModel,
) {

    val splashViewModel: SplashViewModel = viewModel(
        factory = SplashViewModelFactory(AppContainer.usuarioRepository)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .navigationBarsPadding(),
        ) {
            Image(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.Center),
                painter = painterResource(R.drawable.palomero),
                contentDescription = "Logo"
            )
            Text(
                text = "Created by Esteban ",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 35.dp),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp
            )
        }

    }

    LaunchedEffect(Unit) {
        delay(2000)
        if (splashViewModel.isUserLoggedIn() && splashViewModel.comprobarUsuarioExiste()) {
            val usuario = splashViewModel.obtenerUsuarioActual()
            usuarioViewModel.establecerUsuario(usuario)
            navHostController.navigate(Routes.FEED) {
                launchSingleTop = true
                popUpTo(0) { inclusive = true } // Limpia toda la pila (0 es la raíz)
            }
        } else {
            navHostController.navigate(Routes.LOGIN) {
                launchSingleTop = true
                popUpTo(0) { inclusive = true } // Limpia toda la pila (0 es la raíz)
            }
        }
    }
}

