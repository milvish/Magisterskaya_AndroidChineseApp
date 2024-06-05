package com.example.chinese_app_ar.ui.ar_room.lesson

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chinese_app_ar.R
import com.example.chinese_app_ar.ui.ar_room.description.DescriptionFragment

class ArModelsAdapter(var arRoom3dModels: MutableList<ArRoom3dModel>, private val context: Fragment): RecyclerView.Adapter<ArModelsAdapter.ArModelViewHolder>() {

    interface OnArModelClickListener {
        fun onArModelClicked(arRoom3dModel: ArRoom3dModel, position: Int)
    }

    private var listener: OnArModelClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArModelViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ar_models_list_item, parent, false)
        return ArModelViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArModelViewHolder, position: Int) {
        var arRoom3dModel: ArRoom3dModel = arRoom3dModels[position]
        val imageUrl = arRoom3dModel.icon // assume this is the URL of the image
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.arIcon)
        //holder.arNameTextView.text = arModelItem.name
        holder.arIcon.setOnClickListener { view ->
            // Do something when the arIcon is clicked
            Toast.makeText(view.context, "${arRoom3dModel.name} downloaded", Toast.LENGTH_SHORT).show()
            listener?.onArModelClicked(arRoom3dModel, position)
            DescriptionFragment.word = arRoom3dModel.name
        }
    }

    override fun getItemCount(): Int {
        return arRoom3dModels.size
    }

    fun setOnArModelClickListener(listener: OnArModelClickListener) {
        this.listener = listener
    }

    class ArModelViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val arIcon: ImageView = itemView.findViewById(R.id.ar_icon)
        //val arNameTextView: TextView = itemView.findViewById(R.id.ar_name)

    }

}