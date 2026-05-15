package com.example.murveg

// Тип растительности (дерево / куст / трава)
enum class PlantType {
    TREE,   // дерево
    SHRUB,  // кустарник
    HERB    // травянистое растение
}

// Доменная модель растения, используется во всём приложении
data class Plant(
    val id: Long,                    // уникальный идентификатор растения
    val name: String,                // название растения
    val type: PlantType,             // тип растительности (дерево/куст/трава)
    val description: String,         // краткое описание
    val conditions: String,          // условия произрастания
    val facts: String? = null,       // интересные факты (опционально)
    val imageResId: Int? = null,     // id ресурса изображения (опционально)
    val tags: List<String> = emptyList(), // теги для поиска и подбора похожих растений
    val isFavorite: Boolean = false  // признак, добавлено ли в избранное
)

