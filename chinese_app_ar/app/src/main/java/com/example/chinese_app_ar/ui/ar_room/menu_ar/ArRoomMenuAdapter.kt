package com.example.chinese_app_ar.ui.ar_room.menu_ar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.chinese_app_ar.R
import com.example.chinese_app_ar.databinding.ArRoomMenuItemRecyclerRowBinding
import com.example.chinese_app_ar.ui.ar_room.description.DescriptionFragment
import com.example.chinese_app_ar.ui.ar_room.lesson.ArRoomFragment

class ArRoomMenuAdapter(private val arRoomMenuList: List<ArRoomMenuModel>) :
    RecyclerView.Adapter<ArRoomMenuAdapter.MyViewHolder>(){


    class MyViewHolder(private val binding: ArRoomMenuItemRecyclerRowBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bindData(arRoomMenu: ArRoomMenuModel){
            binding.arRoomMenuImageView.contentDescription = arRoomMenu.category
            Glide.with(binding.arRoomMenuImageView).load(arRoomMenu.url)
                .apply(
                    RequestOptions().transform(RoundedCorners(32))
                )
                .into(binding.arRoomMenuImageView)
            //Log.i("LESSONS", levels.lessons.size.toString())


            val context = binding.root.context
            binding.root.setOnClickListener {
                ArRoomFragment.category = arRoomMenu.category
                DescriptionFragment.category = arRoomMenu.category
                val navController = Navigation.findNavController(it)
                navController.navigate(R.id.action_arRoomMenuFragment_to_nav_slideshow)
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ArRoomMenuItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arRoomMenuList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(arRoomMenuList[position])
    }
}
