package com.example.chinese_app_ar.ui.lections.lessons

data class LessonsModel(
    var grammar: String,
    var hieroglyphics: String,
    var title: String,
    var phonetics: String,
    var conspect: String,
    var id: String
){
    constructor() : this("", "", "", "", "", "")
}
