package com.example.proyectopalomero.ui.theme.screens.Chats

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.utils.EstadoUI
import com.example.proyectopalomero.data.utils.EstadoUIHandler
import com.example.proyectopalomero.data.utils.Routes
import com.example.proyectopalomero.navigation.safeNavigate

@Composable
fun NuevoChatScreen(
    snackbarHostState: SnackbarHostState,
    navHostController: NavHostController,
    usuarioViewModel: UsuarioViewModel,
    chatsViewModel: ChatViewModel
) {
    val estadoUI by chatsViewModel.estadoUI.collectAsStateWithLifecycle(initialValue = EstadoUI.Vacio)

    val usuarioActual = usuarioViewModel.usuario.collectAsState().value
    val listaUsuarios = chatsViewModel.listaUsuarios.observeAsState().value
    val chatSeleccionado by chatsViewModel.chatSeleccionado.collectAsState()

    var nombreUsuario by remember { mutableStateOf("@") }

    val isLoading = remember { mutableStateOf(true) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(usuarioActual) {
        usuarioActual?.id?.let {
            chatsViewModel.cargarUsuarios(usuarioActual.id!!)
        }
    }

    // Aquí navegamos cuando chatSeleccionado cambia y no es null
    LaunchedEffect(chatSeleccionado) {
        if (chatSeleccionado != null && chatSeleccionado?.id != null) {
            navHostController.safeNavigate(Routes.MENSAJES)
        }
    }

    LaunchedEffect(listaUsuarios) {
        if (listaUsuarios?.isNotEmpty() == true) {
            isLoading.value = false
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        EstadoUIHandler(estadoUI, snackbarHostState) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Barra de búsqueda
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = {
                            navHostController.safeNavigate(Routes.CHATS)
                            chatsViewModel.limpiarSeleccion()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = nombreUsuario,
                        onValueChange = { nombreUsuario = it },
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        label = { Text("Buscar por nickname") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        )
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    IconButton(onClick = {
                        chatsViewModel.buscarUsuario(nombreUsuario)
                        keyboardController?.hide()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Buscar"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de usuarios
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(listaUsuarios?.size ?: 0) { index ->

                        val usuario = listaUsuarios?.get(index)

                        Card(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    // Lanzar la búsqueda del chat, la navegación la hará el LaunchedEffect
                                    chatsViewModel.comprobarChat(usuario?.id!!, usuarioActual?.id!!)
                                },
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp)

                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = usuario?.fotoPerfil,
                                    contentDescription = "Foto de perfil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.primary,
                                            CircleShape
                                        )
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = usuario?.nombre ?: "Nombre desconocido",
                                        fontSize = 20.sp,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = usuario?.nickname ?: "Nickname desconocido",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }


    }


}