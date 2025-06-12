package com.example.proyectopalomero.ui.theme.screens.Feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.model.PublicacionFire
import com.example.proyectopalomero.data.repository.AppContainer
import com.example.proyectopalomero.data.utils.EstadoUI
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarPublicacionScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    usuarioViewModel: UsuarioViewModel
) {
    val agregarPublicacionViewModel: AgregarPublicacionViewModel = viewModel(
        factory = AgregarPublicacionViewModelFactory(AppContainer.publicacionesRepository)
    )

    val estadoUI by agregarPublicacionViewModel.estadoUI.collectAsStateWithLifecycle(initialValue = EstadoUI.Vacio)
    val usuarioActual = usuarioViewModel.usuario.collectAsState().value

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    var texto by remember { mutableStateOf("") }

    // Manejo de estado UI
    LaunchedEffect(estadoUI) {
        when (estadoUI) {
            is EstadoUI.Exito -> {
               // snackbarHostState.showSnackbar("Publicación creada con éxito")
                navController.popBackStack()
                agregarPublicacionViewModel.limipiarEstado()
            }
            is EstadoUI.Error -> {
                snackbarHostState.showSnackbar((estadoUI as EstadoUI.Error).mensaje)
                agregarPublicacionViewModel.limipiarEstado()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            TextField(
                value = texto,
                onValueChange = { texto = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RectangleShape,
                placeholder = { Text("Escribe aquí...") },
                singleLine = false,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        val publicacion = PublicacionFire(
                            usuario = usuarioActual?.id,
                            contenido = texto,
                            comentarios = 0,
                            listaMeGustas = mutableListOf(),
                            fechaCreacion = Timestamp.now()
                        )
                        if (agregarPublicacionViewModel.comprobarPublicacion(texto)){
                            agregarPublicacionViewModel.agregarPublicacion(publicacion)
                        }

                    },
                    modifier = Modifier
                        .width(110.dp)
                        .height(40.dp),
                    shape = RectangleShape
                ) {
                    Text("Publicar", color = Color.Black, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .width(110.dp)
                        .height(40.dp),
                    shape = RectangleShape
                ) {
                    Text("Cancelar", color = Color.Black, fontSize = 11.sp)
                }
            }
        }
    }
}
