package com.example.proyectopalomero.ui.theme.screens.Feed

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectopalomero.UsuarioViewModel

import com.example.proyectopalomero.data.model.PublicacionFire
import com.example.proyectopalomero.data.repository.PublicacionesRepository
import com.example.proyectopalomero.data.utils.Routes
import com.example.proyectopalomero.data.utils.utilsMostrarToast
import com.example.proyectopalomero.navigation.safeNavigate
import com.google.firebase.Timestamp


//Función para crear una publicación
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarPublicacionScreen(modifier: Modifier, navController: NavController,feedViewModel: FeedViewModel,usuarioViewModel: UsuarioViewModel) {

    val usuarioActual = usuarioViewModel.usuario.collectAsState().value

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    var texto by remember { mutableStateOf("") }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(top = 30.dp, end = 10.dp, start = 10.dp)) {
        TextField(
            value = texto,
            onValueChange = { texto = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RectangleShape,
            placeholder = { Text("Escribe aquí...") },
            singleLine = false, // Permite múltiples líneas

            //Propiedades para cerrar el teclado cuando se presione enter
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            )
        )
        //Botón para publicar
        Button(
            modifier = Modifier
                .padding(top = 15.dp, start = 230.dp)
                .width(110.dp)
                .height(40.dp),
            onClick = {

               var publicacion = PublicacionFire(
                   usuario = usuarioActual?.id,
                   contenido = texto,
                   comentarios = 0,
                   listaMeGustas = mutableListOf(),
                   fechaCreacion = Timestamp.now()
               )

                feedViewModel.agregarPublicacion(publicacion)
                utilsMostrarToast(context, "Se ha publicado correctamente")
                navController.safeNavigate(Routes.FEED)

            },
            shape = RectangleShape
        ) {
            Text(
                text = "Publicar",
                color = Color.Black,
                fontSize = 11.sp
            )
        }
    }
}