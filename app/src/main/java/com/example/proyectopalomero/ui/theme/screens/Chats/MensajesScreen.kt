package com.example.proyectopalomero.ui.theme.screens.Chats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.utils.Routes
import com.example.proyectopalomero.navigation.safeNavigate
import com.example.proyectopalomero.ui.theme.theme.fuenteRetro
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensajesScreen(
    navHostController: NavHostController,
    chatsViewModel: ChatViewModel,
    usuarioViewModel: UsuarioViewModel
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val usuarioActual = usuarioViewModel.usuario.collectAsState().value
    val chatSeleccionado = chatsViewModel.chatSeleccionado
    val usuarioChat = chatsViewModel.usuarioChat.collectAsState().value
    val listaMensajes = chatsViewModel.mensajes.collectAsState().value
    var nuevoMensaje by remember { mutableStateOf("") }

    LaunchedEffect(chatSeleccionado.id) {
        val idUsuarioActual = usuarioActual?.id
        val idUsuarioOtro = when (idUsuarioActual) {
            chatSeleccionado.idUsuario1 -> chatSeleccionado.idUsuario2
            chatSeleccionado.idUsuario2 -> chatSeleccionado.idUsuario1
            else -> null
        }

        if (idUsuarioOtro != null) {
            chatsViewModel.cargarMensajes()
            chatsViewModel.cargarUsuarioChat(idUsuarioOtro)
        }
    }

    Scaffold(
        topBar = {
            if (usuarioChat != null) {
                MensajesTopAppBar(scrollBehavior, usuarioChat,navHostController)
            } else {
                TopAppBar(title = { Text("Cargando...") })
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            // Lista de mensajes
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                reverseLayout = true
            ) {
                val mensajesOrdenados = listaMensajes.sortedByDescending { it.fecha?.toDate() }

                items(mensajesOrdenados.size) { mensajes ->

                    val mensaje = mensajesOrdenados[mensajes]
                    val esMio = mensaje.idUsuario == usuarioActual?.id

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (esMio) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    color = if (esMio) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(10.dp)
                                .widthIn(max = 250.dp)
                        ) {
                            Text(
                                text = mensaje.mensaje,
                                color = if (esMio) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            mensaje.fecha?.toDate()?.let { fecha ->
                                Text(
                                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(fecha),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (esMio) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Campo de texto e icono de enviar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = nuevoMensaje,
                    onValueChange = { nuevoMensaje = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje...") },
//                colors = TextFieldDefaults.textFieldColors(
//                    containerColor = MaterialTheme.colorScheme.surface
//                )
                )
                IconButton(
                    onClick = {
                        if (nuevoMensaje.isNotBlank() && usuarioActual != null) {
                            chatsViewModel.enviarMensaje(chatSeleccionado.id!!, nuevoMensaje.trim(), usuarioActual.id!!)
                            nuevoMensaje = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar")
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensajesTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    usuarioChar : UsuarioFire,
    navHostController: NavHostController
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        ),
        navigationIcon = {
            IconButton(
                onClick = { navHostController.safeNavigate(Routes.CHATS) }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 48.dp), // espacio para no chocar con navigationIcon
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = usuarioChar.fotoPerfil,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )

                    Text(
                        text = usuarioChar.nickname ?: "",
                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = fuenteRetro),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )


}
