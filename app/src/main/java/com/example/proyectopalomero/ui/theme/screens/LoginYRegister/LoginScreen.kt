package com.example.proyectopalomero.ui.theme.screens.LoginYRegister

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectopalomero.R
import com.example.proyectopalomero.UsuarioViewModel
import com.example.proyectopalomero.data.model.UsuarioFire
import com.example.proyectopalomero.data.repository.AppContainer
import com.example.proyectopalomero.data.utils.*
import com.example.proyectopalomero.navigation.safeNavigate
import com.example.proyectopalomero.ui.theme.theme.fuenteRetro
import com.example.proyectopalomero.ui.theme.theme.naranjaPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    usuarioViewModel: UsuarioViewModel
) {
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(AppContainer.usuarioRepository)
    )

    val loginExitoso by loginViewModel.loginExitoso.observeAsState()
    val mensajeError by loginViewModel.mensajeError.observeAsState()
    val isLoading by loginViewModel.isLoading.observeAsState(false)

    var email by remember { mutableStateOf("estebanmm15@palomero.com") }
    var password by remember { mutableStateOf("123456") }

    val keyboardController = LocalSoftwareKeyboardController.current
    LocalContext.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val boxContraints = this

        val screenWidth = boxContraints.maxWidth
        val screenHeight = boxContraints.maxHeight

        val density = LocalDensity.current

        val spacingSmall = 16.dp
        val spacingMedium = 24.dp

        val textFieldWidth = screenWidth * 0.8f
        val buttonWidth = screenWidth * 0.4f
        val logoSize = screenWidth * 0.75f

        val titleFontSize = with(density) { (screenHeight * 0.05f).toSp() }
        val labelFontSize = with(density) { (screenHeight * 0.02f).toSp() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.palomero_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(logoSize)
                    .padding(top = spacingMedium)
            )

            Text(
                text = "Login",
                fontSize = titleFontSize,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = fuenteRetro,
                modifier = Modifier.padding(top = spacingSmall)
            )

            // Email
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.Black, fontSize = labelFontSize) },
                modifier = Modifier
                    .width(textFieldWidth)
                    .padding(top = spacingSmall),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray
                ),
                shape = RectangleShape,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )

            // Password
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = Color.Black, fontSize = labelFontSize) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .width(textFieldWidth)
                    .padding(top = spacingSmall),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray
                ),
                shape = RectangleShape,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = spacingSmall))
            }else{
                Button(
                    onClick = { loginViewModel.login(email, password) },
                    modifier = Modifier
                        .padding(top = spacingMedium)
                        .width(buttonWidth)
                ) {
                    Text(text = "Login", color = MaterialTheme.colorScheme.onBackground)
                }

                Row(
                    modifier = Modifier
                        .padding(top = spacingMedium)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿No tienes cuenta?",
                        color = naranjaPrimario,
                        fontSize = labelFontSize
                    )
                    Button(
                        onClick = {
                            navController.safeNavigate(Routes.REGISTER)
                        },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .width(buttonWidth)
                    ) {
                        Text("Regístrate", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }
    }

    // Navegar o mostrar errores
    LaunchedEffect(loginExitoso, mensajeError) {
        if (loginExitoso == true) {
            navController.navigate(Routes.FEED) {
                popUpTo(0) { inclusive = true }
            }
            loginViewModel.limpiarEstado()
        }
        mensajeError?.let {
            snackbarHostState.showSnackbar(it)
            loginViewModel.limpiarEstado()
        }
    }
}