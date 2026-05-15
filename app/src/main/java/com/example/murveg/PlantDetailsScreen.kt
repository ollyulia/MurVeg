package com.example.murveg

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.res.colorResource

@Composable
fun PlantDetailsScreen(
    navController: NavController,    // навигация (назад, переход к другим растениям)
    plantId: Long,                   // id выбранного растения
    viewModel: HomeViewModel         // общий ViewModel с данными
) {
    // пробуем найти растение по id; если его нет — возвращаемся назад
    val plant = viewModel.getPlantById(plantId) ?: run {
        navController.popBackStack()
        return
    }

    val scrollState = rememberScrollState() // состояние вертикального скролла

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars) // отступ от статус-бара
            .padding(horizontal = 20.dp)                  // такие же боковые отступы, как на других экранах
            .verticalScroll(scrollState)                  // контент прокручивается вертикально
    ) {
        Spacer(modifier = Modifier.height(16.dp))         // отступ сверху (как на Home/Favorites)

        // верхняя панель: стрелка "назад" + название + иконка избранного
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {  // вернутьcя на предыдущий экран
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_from_favorites)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = plant.name,                       // название растения
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)           // занимает всё оставшееся место по ширине
            )

            IconButton(onClick = { viewModel.toggleFavorite(plant.id) }) { // переключить "избранное"
                Icon(
                    imageVector = if (plant.isFavorite)
                        Icons.Default.Favorite          // заполненное сердце
                    else
                        Icons.Default.FavoriteBorder,   // пустое сердце
                    contentDescription = stringResource(R.string.favorites_cd)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // блок под картинку (пока заглушка)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),                          // фиксированная высота под изображение
            shape = RoundedCornerShape(12.dp)
        ) {
            // TODO: показать изображение по imageResId, если будет
        }

        Spacer(modifier = Modifier.height(16.dp))

        // описание растения
        Text(
            text = plant.description,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // условия произрастания
        Text(
            text = plant.conditions,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // кнопка "Похожие" — просто визуальное оформление заголовка блока
        Button(
            onClick = { /* только визуально, без действия */ },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.filters_button_bg),   // тот же зеленый, что и у "Фильтров"
                contentColor = colorResource(R.color.filters_button_text)
            )
        ) {
            Text(text = stringResource(R.string.similar))
        }

        // горизонтальный ряд похожих растений
        SimilarPlantsRow(
            currentPlant = plant,
            allPlants = viewModel.uiState.allPlants,     // берём все растения из состояния
            onPlantClick = { otherId ->
                navController.navigate("plant/$otherId") // переход к деталям выбранного похожего растения
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Горизонтальный список похожих растений.
 * Растение считается похожим, если у него есть хотя бы один общий тег с текущим.
 */
@Composable
private fun SimilarPlantsRow(
    currentPlant: Plant,                  // растение, для которого ищем похожие
    allPlants: List<Plant>,               // полный список растений
    onPlantClick: (Long) -> Unit          // обработчик клика по похожему растению (id → навигация)
) {
    val currentTags = currentPlant.tags.toSet()   // множество тегов текущего растения
    val similar = allPlants
        .filter { it.id != currentPlant.id }      // исключаем текущее растение
        .filter { plant ->
            plant.tags.any { it in currentTags }  // оставляем только те, у кого есть общий тег
        }

    if (similar.isEmpty()) return                 // если похожих нет — ничего не рисуем

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),   // горизонтальный скролл карточек
        horizontalArrangement = Arrangement.spacedBy(8.dp) // расстояние между карточками
    ) {
        similar.forEach { plant ->
            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { onPlantClick(plant.id) } // клик по карточке → открыть детали
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = plant.name,               // показываем только название похожего растения
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}