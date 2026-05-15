package com.example.murveg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.murveg.ui.theme.MurVegTheme

// Главная активность приложения
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включаем отрисовку контента в области за вырезами и навигационной панелью (Edge-to-Edge)
        enableEdgeToEdge()

        setContent {
            // Применяем тему приложения
            MurVegTheme {
                // Поверхность, заполняющая весь экран
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Запускаем навигацию между экранами приложения
                    AppNavigation()
                }
            }
        }
    }
}
