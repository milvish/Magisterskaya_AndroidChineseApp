package com.example.chinese_app_ar.ui.quizlet

import android.os.Parcel
import android.os.Parcelable
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable

data class QuizModel(
    val id : String,
    val title : String,
    val subtitle : String,
    val time : String,
    val questionList : List<QuestionModel>
): Serializable{
    constructor() : this("","","","", emptyList())
}

data class QuestionModel(
    val question : String,
    val options : List<String>,
    val correct : String,
): Serializable{
    constructor() : this ("", emptyList(),"")
}



