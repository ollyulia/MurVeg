package com.example.murveg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.murveg.ui.theme.MurVegTheme
import androidx.navigation.NavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MurVegTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onNavigateToFavorites: () -> Unit) {
    // Состояние для хранения текста поиска
    val searchText = remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Основной контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Окно поиска
            OutlinedTextField(
                value = searchText.value,
                onValueChange = { searchText.value = it },
                label = { Text("Введите название растения") },
                modifier = Modifier.fillMaxWidth()
            )

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 70.dp), // увеличьте top по необходимости
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = onNavigateToFavorites) {
                Text("Избранное")
            }
        }

    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onNavigateToFavorites = { navController.navigate("favorites") })
        }
        composable("favorites") {
            LikesScreen(navController = navController) }
    }
}

@Composable
fun LikesScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier
                .align(Alignment.TopStart) // Прижать кнопку к верху слева
                .padding(16.dp, top = 70.dp) // Отступы сверху и слева для красоты
        ) {
            Text("выход")
        }
    }
}
