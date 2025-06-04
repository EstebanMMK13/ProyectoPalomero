package com.example.proyectopalomero.ui.theme.screens.Chats

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.proyectopalomero.R
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.utils.MiNavigationBar
import com.example.proyectopalomero.data.utils.Routes
import com.example.proyectopalomero.data.utils.formatearHora
import com.example.proyectopalomero.navigation.safeNavigate
import com.example.proyectopalomero.ui.theme.theme.fuenteRetro
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    navHostController: NavHostController,
    chatsViewModel: ChatViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val usuarioActual = usuarioViewModel.usuario.collectAsState().value
    val listaChats  = chatsViewModel.chats.collectAsState().value
    val usuariosMap = chatsViewModel.usuariosChatMap

    val isLoading = remember { mutableStateOf(true) }

    // Cargar los chats cuando tengamos el usuario actual
    LaunchedEffect(usuarioActual) {
        usuarioActual?.id?.let {
            chatsViewModel.obtenerChats(it)
        }
    }

    // Si hay chats, marcamos que ya no está cargando
    LaunchedEffect(listaChats) {
        if (listaChats.isNotEmpty()) {
            isLoading.value = false
        }
    }

    Scaffold(
        topBar = {ChatsTopAppBar(scrollBehavior)},
        bottomBar = { MiNavigationBar(navHostController) },
        floatingActionButton = { ChatsFab(navHostController) }
    )
    { innerPadding ->

        if (isLoading.value) {
            // Muestra un indicador de carga
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else {
            // Mostrar la lista de chats
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(listaChats) { chat ->

                    // Obtener el ID del otro usuario
                    val otroUsuarioId = chat.usuarios?.firstOrNull { it != usuarioActual?.id }

                    // Obtener objeto UsuarioFire
                    val usuario = usuariosMap[otroUsuarioId]

                    Card(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth()
                            .pointerInput(true) {
                                detectTapGestures(
                                    onLongPress = { chatsViewModel.borrarChat(chat.id!!) },
                                    onTap = {chatsViewModel.seleccionarChat(chat)
                                    navHostController.safeNavigate(Routes.MENSAJES)
                                    })
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = usuario?.nickname ?: "",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = formatearHora(chat.fechaMensaje ?: Timestamp.now()),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = chat.ultimoMensaje ?: "",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior
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
                text = "CHATS",
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
fun ChatsFab(navHostController: NavHostController) {
    FloatingActionButton(
        onClick = {navHostController.safeNavigate(Routes.NUEVO_CHAT) },
        containerColor = MaterialTheme.colorScheme.background,
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Filled.Email,
            contentDescription = "FAB action",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}
