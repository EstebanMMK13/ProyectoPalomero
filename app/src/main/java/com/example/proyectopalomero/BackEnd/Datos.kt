package com.example.proyectopalomero.BackEnd

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.proyectopalomero.data.model.Publicacion
import com.example.proyectopalomero.data.model.Usuario
import com.example.proyectopalomero.R
import com.example.proyectopalomero.ui.theme.theme.amarilloSecundario

//Archivo de datos de la app, las diferentes clases como la del Uusario, la de la publicación y diferentes listas

var usuarioActual: Usuario? = null

//Usuarios predefinidos por defecto
var usuarioEsteban = Usuario(
    "Esteban Martínez",
    "@EstebanMM15",
    "esteban@gmail.com",
    "12345",
    R.drawable.perfil_gengar,
    1
)
var usuarioMauri =
    Usuario("Mauricio", "@mauri123", "mauri@gmail.com", "54321", R.drawable.perfil_pidgey,2)

var usuarioEmi = Usuario(
    "Rosa Delgado",
    "@rosadelgado",
    "rosadelgado@gmail.com",
    "7890",
    R.drawable.perfil_riolu,
    3
)

var listaUsuarios = mutableListOf<Usuario>(
    usuarioEsteban,
    usuarioMauri,
    usuarioEmi,
)

fun ComprobarCorreoValido(correo: String): Boolean {
    // Expresión regular para validar un correo electrónico
    val patron = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"

    // Retorna verdadero si el correo coincide con el patrón, falso si no lo hace
    return correo.matches(Regex(patron))
}

//Función para comprobar si un nickname es válido
fun ComprobarNickName(nickname: String): Boolean {

    var valido = true

    if (!nickname.startsWith("@")){
        valido = false
    }
    return valido
}

fun ComprobarUsuarioExiste(nickname: String): Boolean {

    var existe = false
    for (usuario in listaUsuarios){
        if (nickname == usuario.nickname){
            existe = true
            break
        }
    }
    return existe
}

//Función para comprobar si un usuario es válido,
//Comprueba que el nickname y la contraseña sean iguales a algún usuario de la lista
fun ComprobarUsuarioLogin(nickname: String, password: String): Boolean {

    var valido: Boolean = false

    for (usuario in listaUsuarios) {

        if (nickname == usuario.nickname && password == usuario.password) {
            usuarioActual = usuario
            valido = true
            break
        }
    }
    return valido
}


//Función para buscar las publicaciones de un usuario y cargarlas despues en la pantalla de Perfil
fun BuscarPublicacionesUsuario(usuario: Usuario): MutableList<Publicacion> {

    var listaTusPublicaciones = mutableListOf<Publicacion>()

    for (publicacion in listaPublicaciones) {
        if (publicacion.usuario == usuario) {
            listaTusPublicaciones.add(publicacion)
        }
    }
    return listaTusPublicaciones
}

//Función para eliminar una publicación
fun EliminarPublicacion(publicacion: Publicacion) {
    listaPublicaciones.remove(publicacion)
}


fun ComprobarMeGustaUsuario(publicacion: Publicacion, usuario: Usuario): Boolean {
    return publicacion.listaMeGusta?.contains(usuario) == true
}

//Función para mostrar las publicaciones en la pantalla de Feed y Perfil
@Composable
fun MostrarPublicaciones(
    modifier: Modifier,
    lista: MutableList<Publicacion>
) {
    // Convertir la lista inicial en una lista reactiva
    val listaPubli = remember { mutableStateListOf(*lista.toTypedArray()) }

    // Mapa para mantener el estado de "me gusta" por cada publicación
    val estadosMeGusta = remember { mutableStateMapOf<Int, MutableState<Boolean>>() }

    LazyColumn(modifier = modifier) {
        itemsIndexed(listaPubli) { indice, publicacion ->
            val usuario = publicacion.usuario

            // Inicializar estado de "me gusta" en el mapa si no existe
            val meGustaState = estadosMeGusta.getOrPut(publicacion.id) {
                mutableStateOf(usuarioActual?.let { ComprobarMeGustaUsuario(publicacion, it) } == true)
            }

            val meGusta = meGustaState.value
            val colorMeGusta by animateColorAsState(if (meGusta) Color.Red else Color.White)
            val animacionMeGusta = animateFloatAsState(if (meGusta) 1.5f else 1.0f)
            var expandirImagen by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp, top = 3.dp, bottom = 3.dp)
                    .fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Encabezado
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Foto de perfil del usuario
                        Image(
                            modifier = Modifier
                                .size(width = 100.dp, height = 90.dp)
                                .padding(start = 10.dp, end = 20.dp, top = 10.dp)
                                .clip(RoundedCornerShape(25))
                                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(25))
                                .pointerInput(true) {
                                    detectTapGestures(onLongPress = { expandirImagen = true })
                                },
                            painter = painterResource(usuario.fotoPerfil),
                            contentDescription = "Imagen usuario",
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 15.dp)
                        ) {
                            Text(
                                text = usuario.nombre,
                                fontSize = 20.sp
                            )

                            Text(
                                text = usuario.nickname,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        // Botón de eliminar para el creador de la publicación
                        if (usuario == usuarioActual) {
                            var expanded by remember { mutableStateOf(false) }

                            Column(
                                modifier = Modifier
                                    .padding(top = 7.dp, end = 15.dp)
                                    .wrapContentWidth()
                            ) {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "Botón eliminar",
                                    )
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Eliminar",
                                                color = MaterialTheme.colorScheme.onBackground,
                                                fontSize = 15.sp
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "Eliminar",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        },
                                        onClick = {
                                            listaPubli.remove(publicacion)
                                            EliminarPublicacion(publicacion)
                                            estadosMeGusta.remove(publicacion.id)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Texto de la publicación
                    Text(
                        text = publicacion.texto,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 30.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    // Icono de "Me gusta" con contador
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    modifier = Modifier
                                        .padding(bottom = 12.dp, start = 5.dp)
                                        .size(25.dp),
                                    contentColor = Color.Black,
                                    containerColor = colorMeGusta
                                ) {
                                    Text("${publicacion.like}")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                tint = amarilloSecundario,
                                contentDescription = "Fav",
                                modifier = Modifier
                                    .clickable {
                                        if (usuarioActual != null) {
                                            val nuevoEstado = !meGusta
                                            meGustaState.value = nuevoEstado

                                            if (nuevoEstado) {
                                                if (!ComprobarMeGustaUsuario(publicacion, usuarioActual!!)) {
                                                    publicacion.listaMeGusta.add(usuarioActual!!)
                                                    publicacion.like += 1
                                                }
                                            } else {
                                                if (ComprobarMeGustaUsuario(publicacion, usuarioActual!!)) {
                                                    publicacion.listaMeGusta.remove(usuarioActual!!)
                                                    publicacion.like -= 1
                                                }
                                            }
                                        }
                                    }
                                    .padding(bottom = 15.dp, start = 10.dp)
                                    .scale(animacionMeGusta.value)
                            )
                        }
                    }
                }
            }

            // Diálogo para ampliar imagen
            if (expandirImagen) {
                Dialog(onDismissRequest = { expandirImagen = false }) {
                    Box(
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .size(300.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)),
                            painter = painterResource(usuario.fotoPerfil),
                            contentDescription = "Imagen ampliada",
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}



//Fotos de prueba predefinidas
var listaDeFotos = mutableListOf(
    R.drawable.perfil_bulbasaur,
    R.drawable.perfil_charmander,
    R.drawable.perfil_squirtle,
    R.drawable.perfil_corphis,
    R.drawable.perfil_gengar,
    R.drawable.perfil_growlithe,
    R.drawable.perfil_metapod,
    R.drawable.perfil_mew,
    R.drawable.perfil_munlax,
    R.drawable.perfil_pidgey,
    R.drawable.perfil_pikachu,
    R.drawable.perfil_pysduck,
    R.drawable.perfil_riolu,
    R.drawable.perfil_ursaring
)

//Publicaciones predefinidas por defecto
var listaPublicaciones = mutableListOf(
    Publicacion(
        1, usuarioMauri,
        "¡Hola! Estoyy emocionado de estar en Palomero. Me parece una plataforma muy interesante para compartir mis pensamientos y conectarme con otros usuarios.",
        12, false
    ),
    Publicacion(2, usuarioEmi, "¿Qué es Palomero? ¿Cómo funciona?", 2, false),
    Publicacion(
        3, usuarioEsteban,
        "Me encanta la comunidad de Palomero. ¡Es muy activa! Me gusta ver cómo la gente se apoya mutuamente y comparte sus ideas y experiencias.",
        1, false
    ),
    Publicacion(
        4, usuarioEmi,
        "¿Alguien sabe cómo puedo mejorar mi perfil en Palomero? Quiero hacer que sea más atractivo y que la gente se sienta atraída a visitarlo.",
        0, false
    ),
    Publicacion(
        5, usuarioMauri,
        "Estoy pensando en escribir un artículo sobre mis experiencias en Palomero. ¿Alguien tiene algún consejo sobre cómo hacer que sea más interesante?",
        5, false
    ),
    Publicacion(
        6, usuarioEsteban,
        "Acabo de descubrir la función de mensajería en Palomero. ¡Es muy útil! Ahora puedo comunicarme con mis amigos y seguidores de manera más directa.",
        3, false
    ),
    Publicacion(
        7, usuarioEmi,
        "Me gustaría saber más sobre la historia de Palomero. ¿Alguien sabe cómo se creó y quiénes son los fundadores?",
        0, false
    ),
    Publicacion(
        8, usuarioMauri,
        "Estoy pensando en crear un grupo en Palomero para discutir temas de interés común. ¿Alguien tiene algún consejo sobre cómo hacer que sea exitoso?",
        2, false
    ),
    Publicacion(
        9, usuarioEsteban,
        "He recibido un mensaje de un usuario que me ha gustado mucho. ¡Es muy motivador! Me hace querer seguir publicando y conectándome con la comunidad.",
        1, false
    ),
    Publicacion(10, usuarioEsteban, "Publicado mi primer palomo. Estoy muy emocionado de ver cómo la gente reacciona a mis publicaciones.", 7, false)
)

data class Chat(
    val usuario: Usuario,
    val ultimoMensaje: String,
    val hora: String
)


var listaChatsEjemplo = mutableListOf(

    Chat(
        usuario = usuarioEmi,
        ultimoMensaje = "Esto es para mi.",
        hora = "9:34"
    ),
    Chat(
        usuario = usuarioEsteban,
        ultimoMensaje = "Te envié el documento ayer.",
        hora = "12:02"
    ),
    Chat(
        usuario = usuarioMauri,
        ultimoMensaje = "Eso suena genial",
        hora = "19:55"
    )
)

