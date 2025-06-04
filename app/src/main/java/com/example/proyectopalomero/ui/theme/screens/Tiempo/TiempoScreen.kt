package com.example.proyectopalomero.ui.theme.screens.Tiempo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.proyectopalomero.data.Api.NetworkResponse
import com.example.proyectopalomero.data.model.WeatherModel
import com.example.proyectopalomero.data.utils.MiNavigationBar

// Pantalla de Tiempo
@Composable
fun WeatherScreen(
    snackbarHostState: SnackbarHostState,
    navHostController: NavHostController,
    viewModel: WeatherViewModel
) {

    var ciudad by remember { mutableStateOf("") }

    val weatherResult = viewModel.weatherResult.observeAsState() // Obtiene el resultado de la petición

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        bottomBar = { MiNavigationBar(navHostController) },

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                    value = ciudad,
                    onValueChange = { ciudad = it },
                    label = { Text(text = "Busca una ciudad (ingles)") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    )
                )
                IconButton(onClick = {
                    viewModel.getData(ciudad)
                    keyboardController?.hide()
                }) {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Busca")
                }
            }
            // Dependiendo del resultado de la petición, muestra el contenido correspondiente
            when (val result = weatherResult.value) {
                // Si la petición ha fallado se muestra el error
                is NetworkResponse.Error -> {
                    Text(
                        text = result.message
                    )
                }
                // Si la petición esta cargando se muestra el indicador de cargando
                NetworkResponse.Loading -> {
                    CircularProgressIndicator()
                }

                // Si la petición ha sido exitosa se muestra el contenido
                is NetworkResponse.Success -> {
                    DetallesTiempo(datos = result.data)
                }

                is NetworkResponse.Empty -> {}

                null -> {}
            }
        }
    }

}

// Contenido de la pantalla
@Composable
fun DetallesTiempo(datos: WeatherModel) {

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Icono Sitio",
                    modifier = Modifier.size(40.dp)
                )
                Text(text = datos.location.name, fontSize = 30.sp) // Nombre de la ciudad
            }
            Text(text = datos.location.country, fontSize = 15.sp) // Pais de la ciudad
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "${datos.current.temp_c} °C ", // Temperatura
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            AsyncImage( //Imagen del tiempo
                modifier = Modifier.size(100.dp),
                model = "https:${datos.current.condition.icon}".replace("64x64", "128x128"), //Ajustar la imagen
                contentDescription = "Imagen Tiempo"
            )
            Text(
                text = datos.current.condition.text, // Descripcion del tiempo
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(15.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ColocarContenido("Humedad", datos.current.humidity + " %") // Humedad
                        ColocarContenido("Velocidad del viento", datos.current.wind_kph + " km/h") // Velocidad del viento
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ColocarContenido("UV", datos.current.uv) // UV, radiacion solar
                        ColocarContenido("Precipitacion", datos.current.precip_mm) // Precipitacion
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ColocarContenido("Hora Local", datos.location.localtime.split(" ")[1])
                        ColocarContenido("Fecha Local", datos.location.localtime.split(" ")[0])
                    }
                }
            }
        }
    }
}

@Composable
fun ColocarContenido(key: String, value: String) {

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = key, fontWeight = FontWeight.SemiBold)
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}