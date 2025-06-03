package com.example.proyectopalomero.ui.theme.screens.Chats.Mensajes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.repository.AppContainer
import com.example.proyectopalomero.ui.theme.screens.Chats.ChatViewModel
import com.example.proyectopalomero.ui.theme.screens.Chats.ChatViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MensajesScreen(modifier: Modifier, usuarioViewModel: UsuarioViewModel, idChat: String) {

    val mensajesViewModel: MensajesViewModel = viewModel(
        factory = MensajesViewModelFactory(AppContainer.chatsRepository)
    )

    val usuarioActual = usuarioViewModel.usuario.collectAsState().value
    val listaMensajes = mensajesViewModel.mensajes.collectAsState().value
    var nuevoMensaje by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        mensajesViewModel.cargarMensajes(idChat)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                        mensajesViewModel.enviarMensaje(idChat, nuevoMensaje.trim(), usuarioActual.id!!)
                        nuevoMensaje = ""
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar")
            }
        }
    }
}
