package com.example.chinese_app_ar.ui.ar_room.description

data class Categories(
    val transport: List<Vocabulary>,
    val animals: List<Vocabulary>
)

data class Vocabulary(
    val russianWord: String,
    val englishWord: String,
    val chineseWord: ChineseWord,
    val sentences: List<Sentences>
)

data class ChineseWord(
    val hieroglyph: String,
    val pinyin: String
)

data class Sentences(
    val chinese: String,
    val pinyin: String,
    val russian: String
)
