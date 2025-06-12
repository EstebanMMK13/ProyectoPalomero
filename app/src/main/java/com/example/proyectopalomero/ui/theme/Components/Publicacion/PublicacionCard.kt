package com.example.proyectopalomero.ui.theme.Components.Publicacion

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.example.proyectopalomero.data.model.PublicacionFire
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.ui.theme.theme.amarilloSecundario
import kotlinx.coroutines.launch


@Composable
fun MostrarPublicacion(
    publicacion: PublicacionFire,
    usuario: UsuarioFire,
    usuarioActual: UsuarioFire,
    acciones: PublicacionActions
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)

    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            UsuarioHeader(usuario, usuarioActual, acciones, publicacion)
            Spacer(modifier = Modifier.height(2.dp))
            ContenidoPublicacion(publicacion)
            Spacer(modifier = Modifier.height(2.dp))
            AccionesPublicacion(publicacion, usuario, acciones)
        }
    }
}

@Composable
fun UsuarioHeader(
    usuario: UsuarioFire,
    usuarioActual: UsuarioFire,
    acciones: PublicacionActions,
    publicacion: PublicacionFire
) {
    val esPropia = usuario.id == usuarioActual.id
    var expandirImagen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = usuario.fotoPerfil,
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .pointerInput(true) {
                    detectTapGestures(onLongPress = { expandirImagen = true })
                }
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = usuario.nombre ?: "Nombre desconocido",
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = usuario.nickname ?: "Nickname desconocido",
                color = MaterialTheme.colorScheme.secondary
            )
        }

        if (esPropia) {
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Opciones")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Eliminar") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Delete,
                                tint = amarilloSecundario,
                                contentDescription = "Eliminar"
                            )
                        },
                        onClick = {
                            acciones.eliminarPublicacion(publicacion.id!!)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    if (expandirImagen) {
        Dialog(onDismissRequest = { expandirImagen = false }) {
            Box(
                modifier = Modifier.background(Color.Black.copy(alpha = 0.0f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = usuario.fotoPerfil,
                    contentDescription = "Imagen ampliada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }
    }
}


@Composable
fun ContenidoPublicacion(publicacion: PublicacionFire) {
    Text(
        text = publicacion.contenido ?: "",
        fontSize = 15.sp,
       // textAlign = TextAlign.Left,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 70.dp, end = 20.dp, bottom = 6.dp)
    )
}

@Composable
fun AccionesPublicacion(
    publicacion: PublicacionFire,
    usuario: UsuarioFire,
    acciones: PublicacionActions
) {
    var meGusta by remember(publicacion.listaMeGustas, usuario.id) {
        mutableStateOf(acciones.leGustaAlUsuario(publicacion, usuario.id ?: ""))
    }

    val animacionMeGusta by animateFloatAsState(if (meGusta) 1.5f else 1.0f)
    val colorFav = if (meGusta) Color.Red else amarilloSecundario

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 70.dp, end = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Me gusta",
            tint = colorFav,
            modifier = Modifier
                .clickable {
                    acciones.alternarMeGusta(publicacion, usuario.id ?: "")
                    meGusta = !meGusta
                }
                .scale(animacionMeGusta)
                .size(28.dp)
        )
        Spacer(modifier = Modifier.width(9.dp))
        Text(
            text = "${publicacion.listaMeGustas?.size ?: 0}",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


