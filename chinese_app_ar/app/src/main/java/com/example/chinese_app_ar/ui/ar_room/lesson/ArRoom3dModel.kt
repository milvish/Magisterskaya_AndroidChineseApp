package com.example.chinese_app_ar.ui.ar_room.lesson

data class ArRoom3dModel(
    var icon: String = "",
    var model: String = "",
    var name: String = "",
    var video: String = ""
) {
    constructor() : this("", "", "", "")
}