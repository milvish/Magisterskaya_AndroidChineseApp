package com.example.chinese_app_ar.ui.lections.lessons

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chinese_app_ar.R
import com.example.chinese_app_ar.databinding.LessonsListItemRecyclerViewBinding
import com.google.firebase.firestore.FirebaseFirestore

class LessonsListAdapter (private val lessonIdList: List<String>, private val level: String):
    RecyclerView.Adapter<LessonsListAdapter.MyViewHolder>(){


    interface OnLessonModelClickListener {
        fun onLessonModelClicked(lessonModelItem: String)
    }

    private var listener: OnLessonModelClickListener? = null

    class MyViewHolder(private val binding: LessonsListItemRecyclerViewBinding, private val level:String) : RecyclerView.ViewHolder(binding.root) {
        var lesId: String = ""

        fun bindData(lessonId: String) {

            FirebaseFirestore.getInstance().collection("lessons")
                .document(lessonId).get()
                .addOnSuccessListener {
                    val lesson = it.toObject(LessonsModel::class.java)
                    lesson?.apply {
                        binding.lessonTitle.text = title.replace("Lesson", "Урок")
                        binding.lessonItem.background = ContextCompat.getDrawable(itemView.context, R.drawable.rounded_corner_lessons)
                        when (level){
                            "HSK 1" -> binding.lessonItem.background = ContextCompat.getDrawable(itemView.context, R.drawable.rounded_corner_lessons)
                            "HSK 2" -> binding.lessonItem.background = ContextCompat.getDrawable(itemView.context, R.drawable.rounded_corner_lessons2)
                            else -> binding.lessonItem.background = ContextCompat.getDrawable(itemView.context, R.drawable.rounded_corner_lessons3)
                        }

                        lesId = id
                        /*
                        binding.root.setOnClickListener {
                            MyExoplayer.startPlaying(binding.root.context, song)
                            it.context.startActivity(
                                Intent(
                                    it.context,
                                    PlayerActivity::class.java
                                )
                            )
                        }

                         */
                    }
                }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LessonsListItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding, level)
    }

    override fun getItemCount(): Int {
        return lessonIdList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(lessonIdList[position])
        holder.itemView.setOnClickListener{
            listener?.onLessonModelClicked(holder.lesId)
            Log.d("lessonID", holder.lesId)
        }
    }

    fun setOnLessonModelClickListener(listener: OnLessonModelClickListener){
        this.listener = listener
    }
}