package com.example.proyectopalomero.ui.theme.screens.Perfil

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.proyectopalomero.BackEnd.ComprobarNickName
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.utils.Routes
import com.example.proyectopalomero.data.utils.utilsMostrarToast
import com.example.proyectopalomero.navigation.safeNavigate


//Pantalla de edición de perfil
@Composable
fun EditarPerfilScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    usuarioViewModel: UsuarioViewModel,
    perfilViewModel: PerfilViewModel
) {

    val context = LocalContext.current
    val rvState = rememberLazyStaggeredGridState()
    val keyboardController = LocalSoftwareKeyboardController.current
    rememberCoroutineScope()

    val usuarioActual = usuarioViewModel.usuario.collectAsState().value
    var nombre by rememberSaveable { mutableStateOf(usuarioActual?.nombre ?: "") }
    var nickname by rememberSaveable { mutableStateOf(usuarioActual?.nickname ?: "") }
    var imagen by rememberSaveable { mutableStateOf(usuarioActual?.fotoPerfil ?: "") }
    var nuevaPassword by rememberSaveable { mutableStateOf("") }
    var cambiarImagen by remember { mutableStateOf(false) }

    Scaffold(

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                AsyncImage(
                    model = imagen,
                    contentDescription = "Imagen usuario",
                    modifier = Modifier
                        .size(200.dp)
                        //.padding(top = 10.dp, bottom = 20.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    cambiarImagen = true // Muestra tus imágenes predefinidas
                                }
                            )
                        },
                    contentScale = ContentScale.Crop
                )
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(text = "Nombre") },
                    shape = RectangleShape,
                    modifier = Modifier.padding(bottom = 20.dp),

                    //Propiedades para cerrar el teclado cuando se presione enter
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    )
                )

                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text(text = "Nickname") },
                    shape = RectangleShape,
                    modifier = Modifier.padding(bottom = 20.dp),
                    //Propiedades para cerrar el teclado cuando se presione enter
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    )
                )

                OutlinedTextField(
                    value = nuevaPassword,
                    onValueChange = { nuevaPassword = it },
                    label = { Text(text = "Nueva contraseña") },
                    shape = RectangleShape,
                    visualTransformation = PasswordVisualTransformation(), // Oculta la contraseña
                    //Propiedades para cerrar el teclado cuando se presione enter
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    )
                )

                Row {
                    Button(
                        onClick = {
                            if (ComprobarNickName(nickname)) { //Comprobamos si el nickname es válido

                                val  usuarioEditado = UsuarioFire(
                                    nickname = nickname,
                                    nombre = nombre,
                                    fotoPerfil = imagen
                                )
                                usuarioViewModel.establecerUsuario(usuarioEditado)
                                perfilViewModel.actualizarUsuario(usuarioActual?.id!!, usuarioEditado)
                                navController.safeNavigate(Routes.PERFIL)
                            } else {
                                utilsMostrarToast(context,"El usuario debe empezar con @")
                            }
                        },
                        modifier = Modifier.padding(20.dp),
                        shape = RectangleShape
                    ) {
                        Text(
                            text = "Guardar"
                        )
                    }

                    Button(
                        onClick = { navController.safeNavigate(Routes.PERFIL) },
                        modifier = Modifier.padding(20.dp),
                        shape = RectangleShape
                    ) {
                        Text(
                            text = "Cancelar"
                        )
                    }
                }
            }
        }

        //Dialogo para mostrar las fotos
        if (cambiarImagen) {

            Dialog(onDismissRequest = { cambiarImagen = false }) {
                LazyVerticalStaggeredGrid(
                    state = rvState,
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    items(perfilViewModel.listaDeAvatares().size) { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = perfilViewModel.listaDeAvatares()[index],
                                contentDescription = "Imagen usuario",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    .clickable {
                                        imagen = perfilViewModel.listaDeAvatares()[index]
                                        cambiarImagen = false
                                    }
                            )
                        }
                    }
                }
            }

        }

    }



}