package com.example.murveg

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding

/**
 * Экран избранного:
 *  - та же верхняя панель поиска (SearchTopBar),
 *    но со стрелкой "назад" слева и без иконок справа
 *  - список избранных растений, с возможностью удалить
 *  - переход на экран деталей растения
 *
 * Отступы сверху/сбоку такие же, как на главном экране,
 * поэтому шапка визуально стоит на том же месте на любых устройствах.
 */
@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    var query by remember { mutableStateOf("") }

    // Фильтрация избранных по строке поиска
    val favorites = remember(query, viewModel.uiState.allPlants) {
        viewModel.searchInFavorites(query)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // те же insets, что и в HomeScreen
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // верхняя панель: стрелка назад + поиск
        SearchTopBar(
            query = query,
            onQueryChange = { query = it },
            onSearchClick = { /*через remember */ },
            leftIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back_from_favorites)
                    )
                }
            },
            rightIcon = null // справа ничего нет
        )

        Spacer(modifier = Modifier.height(16.dp))

        // список избранного или "ничего не найдено"
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(stringResource(R.string.nothing_found))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favorites) { plant ->
                    FavoriteItemRow(
                        plant = plant,
                        onOpenDetails = {
                            navController.navigate("plant/${plant.id}")
                        },
                        onRemove = {
                            viewModel.toggleFavorite(plant.id)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Одна строка в списке избранного:
 *  - название и описание растения
 *  - нажатие по тексту открывает детали
 *  - кнопка корзины удаляет из избранного
 */
@Composable
private fun FavoriteItemRow(
    plant: Plant,
    onOpenDetails: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onOpenDetails)
            ) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = plant.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.remove_from_favorites)
                )
            }
        }
    }
}