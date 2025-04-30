package com.example.chinese_app_ar.ui.lections.levels

import com.example.chinese_app_ar.ui.lections.lessons.LessonsModel

data class LevelsModel(
    val level: String,
    val url: String,
    val lessons: List<String>
) {
    constructor() : this("", "", listOf())
}