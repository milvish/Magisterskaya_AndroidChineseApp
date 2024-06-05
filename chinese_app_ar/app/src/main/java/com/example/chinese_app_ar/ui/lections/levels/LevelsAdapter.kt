package com.example.chinese_app_ar.ui.lections.levels

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.chinese_app_ar.R
import com.example.chinese_app_ar.databinding.LevelsItemRecyclerRowBinding
import com.example.chinese_app_ar.ui.lections.lessons.LessonsFragment

class LevelsAdapter (private val levelsList: List<LevelsModel>) :
    RecyclerView.Adapter<LevelsAdapter.MyViewHolder>(){


        class MyViewHolder(private val binding: LevelsItemRecyclerRowBinding):
                RecyclerView.ViewHolder(binding.root){

                    fun bindData(levels: LevelsModel){
                        binding.levelTextView.text = levels.level
                        Glide.with(binding.levelImageView).load(levels.url)
                            .apply(
                                RequestOptions().transform(RoundedCorners(32))
                            )
                            .into(binding.levelImageView)
                        //Log.i("LESSONS", levels.lessons.size.toString())


                        val context = binding.root.context
                        binding.root.setOnClickListener {
                            LessonsFragment.level = levels
                            /*
                            val fragmentManager = (context as FragmentActivity).supportFragmentManager
                            val transaction = fragmentManager.beginTransaction()
                            transaction.replace(R.id.nav_levels, LessonsFragment.newInstance() as Fragment)
                            transaction.addToBackStack(null)
                            transaction.commit()

                             */
                            val navController = Navigation.findNavController(it)
                            navController.navigate(R.id.action_nav_levels_to_nav_lessons)
                        }

                    }
                }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val binding = LevelsItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return MyViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return levelsList.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindData(levelsList[position])
        }
    }

