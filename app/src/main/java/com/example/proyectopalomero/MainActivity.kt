package com.example.proyectopalomero

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectopalomero.ui.theme.screens.Tiempo.WeatherViewModel
import com.example.proyectopalomero.data.utils.Routes
import com.example.proyectopalomero.data.utils.UtilsCambiarTema
import com.example.proyectopalomero.navigation.FabConfig
import com.example.proyectopalomero.navigation.NavigationWrapper
import com.example.proyectopalomero.navigation.getFabConfig
import com.example.proyectopalomero.navigation.pantallasSinBottomBar
import com.example.proyectopalomero.navigation.pantallasSinFab
import com.example.proyectopalomero.navigation.pantallasSinTopBar
import com.example.proyectopalomero.navigation.safeNavigate
import com.example.proyectopalomero.ui.theme.theme.ProyectoPalomeroTheme
import com.example.proyectopalomero.ui.theme.theme.fuenteRetro
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {


    val usuarioViewModel: UsuarioViewModel by viewModels {
        object : ViewModelProvider.AndroidViewModelFactory(application) {}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val temaOscuro by usuarioViewModel.temaOscuro.collectAsState()
            ProyectoPalomeroTheme(darkTheme = temaOscuro) {
                window.setBackgroundDrawableResource(android.R.color.transparent)
                UtilsCambiarTema(temaOscuro)
                val weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
                val snackbarHostState = remember { SnackbarHostState() }
                NavigationWrapper(
                    temaOscuro,
                    snackbarHostState = snackbarHostState,
                    weatherViewModel = weatherViewModel,
                    usuarioViewModel
                )
            }
        }
    }

}

fun AnimacionEntrada(): EnterTransition {
    return expandHorizontally(
        animationSpec = tween(
            700, easing = LinearEasing
        )
    )
}

fun AnimacionSalida(): ExitTransition {
    return shrinkHorizontally(
        animationSpec = tween(
            700, easing = LinearEasing
        )
    )
}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MiTopAppBar(
//    scrollBehavior: TopAppBarScrollBehavior,
//    nicknameTop: String
//) {
//        TopAppBar(
//            colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = MaterialTheme.colorScheme.background,
//                titleContentColor = MaterialTheme.colorScheme.secondary,
//                scrolledContainerColor = MaterialTheme.colorScheme.background
//            ),
//            navigationIcon = {
//                Image(
//                    modifier = Modifier
//                        .size(70.dp)
//                        .fillMaxWidth(),
//                    painter = painterResource(R.drawable.palomero_logo),
//                    contentDescription = "Logo"
//                )
//            },
//            title = {
//                Text(
//                    text = nicknameTop,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(end = 70.dp),
//                    textAlign = TextAlign.Center,
//                    fontFamily = fuenteRetro
//                )
//            },
//            scrollBehavior = scrollBehavior
//        )
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MiNavigationBar(navController: NavController, temaOscuro: Boolean) {
//
//    var selectedItem by remember { mutableIntStateOf(0) }
//    val items = listOf("Feed", "Mensajes", "Tiempo", "Perfil")
//    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Email, Icons.Default.LocationOn, Icons.Filled.AccountBox)
//    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.Email, Icons.Default.LocationOn, Icons.Outlined.AccountBox)
//
//    val currentDestination = navController.currentBackStackEntryFlow.collectAsState(navController.currentBackStackEntry).value
//
//    NavigationBar(
//        containerColor = MaterialTheme.colorScheme.background,
//        modifier = Modifier.height(56.dp)
//    ) {
//        items.forEachIndexed { indice, item ->
//            NavigationBarItem(
//                icon = {
//                    Icon(
//                        if (selectedItem == indice) selectedIcons[indice] else unselectedIcons[indice],
//                        contentDescription = item,
//                        tint = if (temaOscuro) Color.White else Color.Black
//                    )
//                },
//                selected = selectedItem == indice,
//                onClick = {
//                    selectedItem = indice
//                    when (indice) {
//                        0 -> navController.safeNavigate(Routes.FEED)
//                        1 -> navController.safeNavigate(Routes.CHATS)
//                        2 -> navController.safeNavigate(Routes.TIEMPO)
//                        3 -> navController.safeNavigate(Routes.PERFIL)
//                    }
//                },
//                colors = NavigationBarItemDefaults.colors(
//                    indicatorColor = MaterialTheme.colorScheme.primary,
//                    unselectedIconColor = if (temaOscuro) Color.LightGray else Color.Gray,
//                    selectedIconColor = if (temaOscuro) Color.White else MaterialTheme.colorScheme.primary
//                )
//            )
//        }
//    }
//
//    currentDestination?.destination?.route?.let { route ->
//        selectedItem = when (route) {
//            Routes.FEED -> 0
//            Routes.CHATS -> 1
//            Routes.TIEMPO -> 2
//            Routes.PERFIL -> 3
//            else -> selectedItem
//        }
//    }
//}
//
//
//
//
//
//@Composable
//fun MiFab(fabConfig: FabConfig?) {
//    if (fabConfig != null) {
//        FloatingActionButton(
//            onClick = fabConfig.onClick,
//            containerColor = MaterialTheme.colorScheme.background,
//            shape = CircleShape
//        ) {
//            Icon(
//                imageVector = fabConfig.icon,
//                contentDescription = "FAB action",
//                tint = MaterialTheme.colorScheme.secondary
//            )
//        }
//
//    }
//}

