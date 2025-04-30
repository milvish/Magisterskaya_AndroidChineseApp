package com.example.chinese_app_ar.ui.ar_room.description

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chinese_app_ar.R

class SentencesAdapter(private val sentences: List<Sentences>) :
    RecyclerView.Adapter<SentencesAdapter.SentenceViewHolder>() {

    class SentenceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chineseTextView: TextView = view.findViewById(R.id.chineseTextView)
        val pinyinTextView: TextView = view.findViewById(R.id.pinyinTextView)
        val russianTextView: TextView = view.findViewById(R.id.russianTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentenceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sentence, parent, false)
        return SentenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: SentenceViewHolder, position: Int) {
        val sentence = sentences[position]
        holder.chineseTextView.text = sentence.chinese
        holder.pinyinTextView.text = sentence.pinyin
        holder.russianTextView.text = sentence.russian
    }

    override fun getItemCount(): Int = sentences.size
}