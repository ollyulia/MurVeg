package com.example.murveg

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource   // цвета из ресурсов
import androidx.compose.ui.res.stringResource // строки из ресурсов
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding

@Composable
fun SearchTopBar(
    query: String,                              // текущий текст поиска
    onQueryChange: (String) -> Unit,           // обработчик изменения текста
    onSearchClick: () -> Unit,                 // обработчик нажатия "Поиск"
    leftIcon: (@Composable (() -> Unit))? = null,   // необязательная иконка слева
    rightIcon: (@Composable (() -> Unit))? = null,  // необязательный блок справа
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),                  // панель занимает всю ширину экрана
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leftIcon != null) {
            leftIcon()                        // рисуем левую иконку, если передана
            Spacer(modifier = Modifier.width(8.dp))
        }

        TextField(
            value = query,
            onValueChange = onQueryChange,    // любое изменение текста прокидываем наверх
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,                     // иконка "лупа"
                    contentDescription = stringResource(R.string.search_cd),
                    tint = colorResource(R.color.search_field_text)        // цвет иконки
                )
            },
            placeholder = { Text(stringResource(R.string.search_hint)) },   // текст-подсказка
            singleLine = true,                                             // одна строка
            shape = RoundedCornerShape(490.dp),                            // "капсула"
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorResource(R.color.search_field_bg),
                unfocusedContainerColor = colorResource(R.color.search_field_bg),
                disabledContainerColor = colorResource(R.color.search_field_bg),
                focusedTextColor = colorResource(R.color.search_field_text),
                unfocusedTextColor = colorResource(R.color.search_field_text),
                cursorColor = colorResource(R.color.search_field_text)
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,     // текстовая клавиатура
                imeAction = ImeAction.Search          // кнопка "Поиск" справа
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearchClick() }        // вызываем обработчик при нажатии
            ),
            modifier = Modifier.weight(1f)            // поле занимает всё доступное место
        )

        if (rightIcon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            rightIcon()                               // рисуем правый блок, если передан
        }
    }
}

@Composable
fun HomeScreen(
    onNavigateToFavorites: () -> Unit,               // переход на экран избранного
    onNavigateToDetails: (Long) -> Unit,             // переход на детали по id растения
    viewModel: HomeViewModel = viewModel()
) {
    val uiState = viewModel.uiState                  // текущее состояние экрана

    var localQuery by remember(uiState.searchQuery) {
        mutableStateOf(uiState.searchQuery)          // локальное состояние поля поиска
    }

    Column(
        modifier = Modifier
            .fillMaxSize()                           // экран на всю высоту
            .windowInsetsPadding(WindowInsets.statusBars) // отступ от статус-бара
            .padding(horizontal = 20.dp)             // боковые отступы
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        SearchTopBar(
            query = localQuery,
            onQueryChange = { localQuery = it },     // обновляем локальный текст
            onSearchClick = { viewModel.onSearchQueryChange(localQuery) }, // запускаем поиск
            leftIcon = null,
            rightIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.onSearchQueryChange(localQuery) } // кнопка-стрелка
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = stringResource(R.string.search_action_cd)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = onNavigateToFavorites) {          // переход в избранное
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(R.string.favorites_cd)
                        )
                    }
                }
            }
        )

        if (uiState.searchError != null) {            // есть ошибка ввода
            Text(
                text = uiState.searchError,           // текст ошибки из ViewModel
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else if (uiState.searchQuery.isNotBlank()) { // есть введённый запрос без ошибки
            TextButton(
                onClick = {
                    localQuery = ""                   // очищаем локальное поле
                    viewModel.onClearSearch()        // сбрасываем поиск во ViewModel
                }
            ) {
                Text(stringResource(R.string.clear_search))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // фильтры (раскрывающийся список)
        FilterDropdown(
            currentFilter = uiState.currentFilter,    // текущий выбранный фильтр
            onFilterSelected = { viewModel.onFilterSelected(it) } // меняем фильтр во ViewModel
        )

        Spacer(modifier = Modifier.height(8.dp))

        val globalError = uiState.globalError        // возможная глобальная ошибка загрузки

        when {
            uiState.isLoading -> {                   // состояние "загрузка"
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    CircularProgressIndicator()      // кружок загрузки
                }
            }

            globalError != null -> {                 // состояние "ошибка загрузки"
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = globalError,          // показываем текст ошибки
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            uiState.filteredPlants.isEmpty() -> {    // состояние "пусто"
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(stringResource(R.string.nothing_found))
                }
            }

            else -> {                                // состояние "всё нормально, есть данные"
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // отступы между карточками
                ) {
                    items(uiState.filteredPlants) { plant ->          // перебор списка
                        PlantItem(
                            plant = plant,
                            onClick = { onNavigateToDetails(plant.id) } // клик → детали
                        )
                    }
                }
            }
        }
    }
}

/**
 * Выпадающий список фильтров по типу растительности.
 */
@Composable
fun FilterDropdown(
    currentFilter: PlantFilterType,                  // текущий выбранный фильтр
    onFilterSelected: (PlantFilterType) -> Unit      //  выбор фильтра
) {
    var expanded by remember { mutableStateOf(false) } // открыто ли меню

    // подпись на кнопке ("Фильтры" или название выбранного фильтра)
    val currentLabel = when (currentFilter) {
        PlantFilterType.ALL -> stringResource(R.string.filters)       // базовый текст
        PlantFilterType.TREE -> stringResource(R.string.filter_trees)
        PlantFilterType.SHRUB -> stringResource(R.string.filter_shrubs)
        PlantFilterType.HERB -> stringResource(R.string.filter_herbs)
    }

    Box {
        TextButton(
            onClick = { expanded = !expanded },      // по нажатию открываем/закрываем меню
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.textButtonColors(
                containerColor = colorResource(R.color.filters_button_bg),   // зелёный фон
                contentColor = colorResource(R.color.filters_button_text)    // чёрный текст
            )
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,                       // иконка "фильтр"
                contentDescription = stringResource(R.string.filters_cd)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(currentLabel)                                               // подпись на кнопке
        }

        DropdownMenu(
            expanded = expanded,                         // отображать ли меню
            onDismissRequest = { expanded = false }      // закрытие по клику вне меню
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.filter_all)) },  // пункт "Все"
                onClick = {
                    onFilterSelected(PlantFilterType.ALL)              // сбрасываем фильтр
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.filter_trees)) },
                onClick = {
                    onFilterSelected(PlantFilterType.TREE)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.filter_shrubs)) },
                onClick = {
                    onFilterSelected(PlantFilterType.SHRUB)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.filter_herbs)) },
                onClick = {
                    onFilterSelected(PlantFilterType.HERB)
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun PlantItem(
    plant: Plant,                                   // модель растения
    onClick: () -> Unit                             // обработчик клика по карточке
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)           // вся карточка кликабельна
    ) {
        Column(
            modifier = Modifier.padding(12.dp)      // внутренний отступ
        ) {
            Text(
                text = plant.name,                  // название растения
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = plant.description,           // краткое описание
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (plant.type) {          // тип растительности по enum
                    PlantType.TREE -> stringResource(R.string.plant_type_tree)
                    PlantType.SHRUB -> stringResource(R.string.plant_type_shrub)
                    PlantType.HERB -> stringResource(R.string.plant_type_herb)
                },
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}