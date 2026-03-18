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

// Главная активность приложения
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // Включаем отрисовку контента в области за вырезами и навигационной панелью (Edge-to-Edge)

        setContent {
            MurVegTheme { // Применяем тему приложения
                Surface(modifier = Modifier.fillMaxSize()) { // Заполняем всю область экрана поверхностью (фон)
                    AppNavigation() // Запускаем навигацию между экранами приложения
                }
            }
        }
    }
}

// Компонент HomeScreen — главный экран с поиском и кнопкой "Избранное"
@Composable
fun HomeScreen(onNavigateToFavorites: () -> Unit) {
    // Создаем состояние для отслеживания текста, введённого в поле поиска
    val searchText = remember { mutableStateOf("") }

    // Основной контейнер, заполняет весь размер экрана
    Box(modifier = Modifier.fillMaxSize()) {

        // Располагаем элементы по вертикали по центру
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Отступы внутри колонки
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Поле ввода текста с обводкой (OutlinedTextField)
            OutlinedTextField(
                value = searchText.value, // Текущее значение поля ввода
                onValueChange = { searchText.value = it }, // Обработчик изменения текста, обновляет состояние
                label = { Text("Введите название растения") }, // Подсказка внутри поля
                modifier = Modifier.fillMaxWidth() // Поле занимает всю ширину колонки
            )
        }

        // Горизонтальный ряд, расположен вверху с отступом сверху (70dp), по ширине экрана
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 70.dp),
            horizontalArrangement = Arrangement.End // Кнопка выравнена по правому краю
        ) {
            // Кнопка "Избранное", при нажатии вызывает переданный коллбэк для перехода
            Button(onClick = onNavigateToFavorites) {
                Text("Избранное")
            }
        }

    }
}

// Функция управления навигацией между экранами приложения
@Composable
fun AppNavigation() {
    val navController = rememberNavController() // Создаем контроллер навигации

    NavHost(navController, startDestination = "home") { // Начальная точка навигации — экран "home"
        composable("home") { // Маршрут "home"
            HomeScreen(onNavigateToFavorites = { navController.navigate("favorites") })
            // Отображаем главный экран, при нажатии кнопки "Избранное" навигация на экран favorites
        }
        composable("favorites") { // Маршрут "favorites"
            LikesScreen(navController = navController) // Экран с "Избранными" и кнопкой выхода
        }
    }
}

// Экран "Избранное"
@Composable
fun LikesScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier
                .align(Alignment.TopStart) // Прижать кнопку к верху слева
                .padding(16.dp,  top = 70.dp) // Отступы
        ) {
            Text("Выход")
        }
    }
}
