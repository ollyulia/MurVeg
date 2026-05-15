package com.example.murveg

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()          // контроллер навигации для всего приложения
    val homeViewModel: HomeViewModel = viewModel()       // общий ViewModel, между экранами

    // NavHost описывает граф навигации
    NavHost(
        navController = navController,
        startDestination = "home"                        // стартовый экран приложения
    ) {

        // Экран главного списка растений
        composable("home") {
            HomeScreen(
                onNavigateToFavorites = {
                    navController.navigate("favorites")  // переход на экран избранного
                },
                onNavigateToDetails = { plantId ->
                    navController.navigate("plant/$plantId") // переход на детали конкретного растения
                },
                viewModel = homeViewModel               // передаём общий ViewModel
            )
        }

        // Экран избранного
        composable("favorites") {
            FavoritesScreen(
                navController = navController,          // нужен для кнопки "назад" и перехода к деталям
                viewModel = homeViewModel               // тот же ViewModel, чтобы избранное было общим
            )
        }

        // Экран деталей растения с аргументом plantId в роуте
        composable(
            route = "plant/{plantId}",                  // шаблон маршрута
            arguments = listOf(
                navArgument("plantId") { type = NavType.LongType } // описываем тип аргумента
            )
        ) { backStackEntry ->
            // Достаём аргумент plantId из backStackEntry
            val plantId = backStackEntry.arguments?.getLong("plantId")
                ?: return@composable                     // если id нет —  не рисуем экран

            PlantDetailsScreen(
                navController = navController,          // для кнопки "назад" и навигации по похожим
                plantId = plantId,                      // id выбранного растения
                viewModel = homeViewModel               // тот же ViewModel, доступ ко всем растениям
            )
        }
    }
}