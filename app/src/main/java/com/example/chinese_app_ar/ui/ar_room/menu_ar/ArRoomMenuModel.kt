package com.example.chinese_app_ar.ui.ar_room.menu_ar

import com.google.firebase.firestore.PropertyName

data class ArRoomMenuModel(
    val category: String,
    val url: String,
    @get:PropertyName("3d_models") @set:PropertyName("3d_models") var models3d: List<String>
) {
    constructor() : this("", "", listOf())
}
