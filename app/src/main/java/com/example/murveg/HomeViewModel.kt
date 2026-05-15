package com.example.murveg

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.Locale

// Тип фильтра по растительности, используется в UI и фильтрации
enum class PlantFilterType {
    ALL,   // все растения
    TREE,  // только деревья
    SHRUB, // только кустарники
    HERB   // только травы
}

// Состояние главного экрана, на которое подписан UI
data class HomeUiState(
    val searchQuery: String = "",                 // текст в строке поиска
    val searchError: String? = null,              // сообщение об ошибке ввода (например, слишком длинный запрос)
    val allPlants: List<Plant> = emptyList(),     // полный список растений
    val filteredPlants: List<Plant> = emptyList(),// список после фильтрации и поиска
    val currentFilter: PlantFilterType = PlantFilterType.ALL, // текущий фильтр по типу

    // Состояния UI
    val isLoading: Boolean = false,               // идёт ли загрузка данных
    val globalError: String? = null              // общая ошибка загрузки/данных
)

// нормализация строк для поиска: убираем пробелы по краям, приводим ё→е и к нижнему регистру
private fun normalizeForSearch(text: String): String {
    return text
        .trim()                                   // обрезаем пробелы в начале/конце
        .replace('ё', 'е')                        // заменяем ё/Ё на е/Е для устойчивого поиска
        .replace('Ё', 'Е')
        .lowercase(Locale.getDefault())           // приводим к нижнему регистру
}

class HomeViewModel : ViewModel() {

    // Текущее состояние экрана, обёрнутое в mutableState, чтобы Compose реагировал на изменения
    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        uiState = uiState.copy(isLoading = true)  // при старте показываем "загрузка"
        loadPlants()                              // инициализируем список растений
    }

    // Загрузка/инициализация списка растений
    private fun loadPlants() {
        try {
            // Заглушечные данные — в будущем можно заменить на данные из Room/сети
            val plants = listOf(
                Plant(
                    id = 1,
                    name = "Берёза",
                    type = PlantType.TREE,
                    description = "Распространённое дерево Мурманской области.",
                    conditions = "Растёт на солнечных участках, нетребовательна к почве.",
                    tags = listOf("лес", "листопадное")
                ),
                Plant(
                    id = 2,
                    name = "Ель",
                    type = PlantType.TREE,
                    description = "Хвойное дерево, образует тёмнохвойную тайгу.",
                    conditions = "Влажная почва, полутень.",
                    tags = listOf("хвойное", "лес")
                ),
                Plant(
                    id = 3,
                    name = "Голубика",
                    type = PlantType.SHRUB,
                    description = "Невысокий кустарник с съедобными ягодами.",
                    conditions = "Кислые почвы, болота, торфяники.",
                    tags = listOf("ягоды", "тундра")
                )
            )

            // Заполняем состояние и снимаем флаг загрузки
            uiState = uiState.copy(
                allPlants = plants,
                filteredPlants = plants,          // по умолчанию показываем всё
                isLoading = false,
                globalError = null
            )
        } catch (e: Exception) {
            // В случае ошибки при загрузке устанавливаем текст глобальной ошибки
            uiState = uiState.copy(
                isLoading = false,
                globalError = "Не удалось загрузить данные. Попробуйте позже."
            )
        }
    }

    // Обработка изменения текста поиска
    fun onSearchQueryChange(newQuery: String) {
        val trimmed = newQuery.trimStart()        // убираем только лидирующие пробелы

        // Простая валидация: ограничение длины запроса
        val error = when {
            trimmed.length > 50 ->
                "Слишком длинный запрос (максимум 50 символов)"
            else -> null
        }

        // Обновляем текст и ошибку
        uiState = uiState.copy(
            searchQuery = trimmed,
            searchError = error
        )

        // Если ошибок нет — применяем фильтрацию
        if (error == null) {
            applyFilters()
        }
    }

    // Сброс поиска
    fun onClearSearch() {
        uiState = uiState.copy(
            searchQuery = "",                    // очищаем запрос
            searchError = null                  // и ошибку
        )
        applyFilters()                          // пересчитываем список без поиска
    }

    // Пользователь выбрал новый фильтр (Все/Деревья/Кустарники/Травы)
    fun onFilterSelected(filter: PlantFilterType) {
        uiState = uiState.copy(
            currentFilter = filter
        )
        applyFilters()                          // пересчитываем список с новым фильтром
    }

    // Основная логика фильтрации по типу и поисковому запросу
    private fun applyFilters() {
        val query = normalizeForSearch(uiState.searchQuery) // нормализованный текст поиска

        // Сначала фильтруем по типу растения
        val base = when (uiState.currentFilter) {
            PlantFilterType.ALL  -> uiState.allPlants
            PlantFilterType.TREE -> uiState.allPlants.filter { it.type == PlantType.TREE }
            PlantFilterType.SHRUB -> uiState.allPlants.filter { it.type == PlantType.SHRUB }
            PlantFilterType.HERB -> uiState.allPlants.filter { it.type == PlantType.HERB }
        }

        // Затем фильтруем по поисковому запросу (если он не пустой)
        val filtered = if (query.isBlank()) {
            base                                  // пустой запрос → только фильтр по типу
        } else {
            base.filter { plant ->
                val name = normalizeForSearch(plant.name)           // имя растения
                val description = normalizeForSearch(plant.description) // описание
                val tags = plant.tags.map { normalizeForSearch(it) }    // теги

                // Совпадение по имени, описанию или любому тегу
                name.contains(query) ||
                        description.contains(query) ||
                        tags.any { it.contains(query) }
            }
        }

        // Обновляем отфильтрованный список
        uiState = uiState.copy(filteredPlants = filtered)
    }

    // --------- логика деталей и избранного ---------

    // Получаем растение по id для экрана деталей
    fun getPlantById(id: Long): Plant? =
        uiState.allPlants.find { it.id == id }

    // Переключение состояния "избранное" у растения
    fun toggleFavorite(id: Long) {
        val updated = uiState.allPlants.map { plant ->
            if (plant.id == id) {
                plant.copy(isFavorite = !plant.isFavorite) // инвертируем флаг избранного
            } else {
                plant
            }
        }
        uiState = uiState.copy(allPlants = updated)
        applyFilters()                                      // обновляем отображаемый список
    }

    // Получение только избранных растений
    fun getFavorites(): List<Plant> =
        uiState.allPlants.filter { it.isFavorite }

    // Поиск только среди избранных растений
    fun searchInFavorites(query: String): List<Plant> {
        val normalized = normalizeForSearch(query)
        val base = getFavorites()                           // базовый список избранных

        if (normalized.isBlank()) return base               // пустой запрос - все избранные

        return base.filter { plant ->
            val name = normalizeForSearch(plant.name)
            val description = normalizeForSearch(plant.description)
            val tags = plant.tags.map { normalizeForSearch(it) }

            name.contains(normalized) ||
                    description.contains(normalized) ||
                    tags.any { it.contains(normalized) }
        }
    }
}

