package com.example.proyectopalomero.ui.theme.screens.Registro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectopalomero.R
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.repository.AppContainer
import com.example.proyectopalomero.data.utils.EstadoUI
import com.example.proyectopalomero.data.utils.Routes
import com.example.proyectopalomero.navigation.safeNavigate
import com.example.proyectopalomero.ui.theme.theme.fuenteRetro
import com.example.proyectopalomero.ui.theme.theme.naranjaPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavController
) {
    var registerViewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(AppContainer.usuarioRepository)
    )
    val estadoUI by registerViewModel.estadoUI.collectAsStateWithLifecycle(initialValue = EstadoUI.Vacio)

    val keyboardController = LocalSoftwareKeyboardController.current
    LocalContext.current

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val textFieldWidth = screenWidth * 0.8f
    val textFieldHeight = screenHeight * 0.07f
    val buttonWidth = screenWidth * 0.4f
    val imageSize = screenWidth * 0.4f
    val titleFontSize = with(density) { (screenHeight * 0.06f).toSp() }
    val labelFontSize = with(density) { (screenHeight * 0.02f).toSp() }
    val paddingTopSmall = screenHeight * 0.025f
    val paddingTopMedium = screenHeight * 0.05f
    screenHeight * 0.07f

    var nombre by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("@") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(imageSize)
                    .padding(top = paddingTopMedium),
                painter = painterResource(R.drawable.palomero_logo),
                contentDescription = "Logo"
            )

            Text(
                text = "Registrarse",
                modifier = Modifier.padding(top = paddingTopSmall),
                fontSize = titleFontSize,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = fuenteRetro
            )

            // Nombre
            Column {
                Text(
                    text = "Nombre",
                    color = naranjaPrimario,
                    fontSize = labelFontSize,
                    modifier = Modifier
                        .padding(top = paddingTopSmall)
                        .background(MaterialTheme.colorScheme.background)
                )
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(text = "Nombre", color = Color.Black) },
                    modifier = Modifier
                        .width(textFieldWidth)
                        .height(textFieldHeight),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.LightGray,
                        unfocusedContainerColor = Color.LightGray
                    ),
                    shape = RectangleShape,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    )
                )
            }

            // Nickname
            Column {
                Text(
                    text = "@nickname",
                    color = naranjaPrimario,
                    fontSize = labelFontSize,
                    modifier = Modifier
                        .padding(top = paddingTopSmall)
                        .background(MaterialTheme.colorScheme.background)
                )
                TextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text(text = "Nickname", color = Color.Black) },
                    modifier = Modifier
                        .width(textFieldWidth)
                        .height(textFieldHeight),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.LightGray,
                        unfocusedContainerColor = Color.LightGray
                    ),
                    shape = RectangleShape,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    )
                )
            }

            // Correo
            Column {
                Text(
                    text = "Correo",
                    color = naranjaPrimario,
                    fontSize = labelFontSize,
                    modifier = Modifier
                        .padding(top = paddingTopSmall)
                        .background(MaterialTheme.colorScheme.background)
                )
                TextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text(text = "Correo", color = Color.Black) },
                    modifier = Modifier
                        .width(textFieldWidth)
                        .height(textFieldHeight),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.LightGray,
                        unfocusedContainerColor = Color.LightGray
                    ),
                    shape = RectangleShape,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    )
                )
            }

            // Contraseña
            Column {
                Text(
                    text = "Contraseña",
                    color = naranjaPrimario,
                    fontSize = labelFontSize,
                    modifier = Modifier
                        .padding(top = paddingTopSmall)
                        .background(MaterialTheme.colorScheme.background)
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Contraseña", color = Color.Black) },
                    modifier = Modifier
                        .width(textFieldWidth)
                        .height(textFieldHeight),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.LightGray,
                        unfocusedContainerColor = Color.LightGray
                    ),
                    shape = RectangleShape,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    )
                )
            }

            // Botón Registrar
            Button(
                modifier = Modifier
                    .padding(top = paddingTopMedium)
                    .width(buttonWidth),
                onClick = {
                    val nuevoUsuario = UsuarioFire(
                        nombre = nombre,
                        nickname = nickname,
                        correo = correo
                    )
                    registerViewModel.registrarUsuarioCompleto(nuevoUsuario,password)
                }
            ) {
                Text(
                    text = "Registrar",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            // Botón Cancelar
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .padding(top = paddingTopSmall, start = screenWidth * 0.3f)
                    .width(buttonWidth),
                onClick = {
                    navController.safeNavigate(Routes.LOGIN)
                }
            ) {
                Text(
                    text = "Cancelar",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        LaunchedEffect(estadoUI) {
            when (val estado = estadoUI) {
                is EstadoUI.Exito -> {
                    snackbarHostState.showSnackbar("Registro exitoso")
                    registerViewModel.limpiarEstado()
                    navController.safeNavigate(Routes.LOGIN)
                }
                is EstadoUI.Error -> {
                    snackbarHostState.showSnackbar(estado.mensaje)
                    registerViewModel.limpiarEstado()
                }
                is EstadoUI.Cargando -> {
                    snackbarHostState.showSnackbar("Cargando...")
                }
               is EstadoUI.Vacio-> {}
            }
        }

    }
}




