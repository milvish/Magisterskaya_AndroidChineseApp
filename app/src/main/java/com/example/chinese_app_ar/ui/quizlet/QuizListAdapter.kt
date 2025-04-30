package com.example.chinese_app_ar.ui.quizlet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavGraph
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.chinese_app_ar.MainActivity
import com.example.chinese_app_ar.R
import com.example.chinese_app_ar.databinding.QuizItemRecyclerRowBinding
//import com.example.chinese_app_ar.NavGraphDirections

class QuizListAdapter(private val quizModelList : List<QuizModel>) :
    RecyclerView.Adapter<QuizListAdapter.MyViewHolder>() {


    class MyViewHolder(private val binding: QuizItemRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model : QuizModel){
            binding.quizTitleText.text = model.title
            binding.quizSubtitleText.text = model.subtitle
            binding.quizTimeText.text = model.time + " min"


            val context = binding.root.context
            binding.root.setOnClickListener {
                val fragment = QuizletQuestionFragment()
                fragment.questionModelList = model.questionList
                fragment.time = model.time

                val bundle = Bundle()
                val updatedQuizModel = QuizModel(model.id, model.title, model.subtitle, model.time, model.questionList)
                bundle.putSerializable("quizModel", model)
                bundle.putString("time", model.time)


                Log.d("questionModelList", fragment.questionModelList.toString())
                Log.d("time", fragment.time)


                /*
                val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.nav_host_fragment_content_main, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
                */


                val navController = Navigation.findNavController(it)
                navController.navigate(R.id.action_nav_quizlet_to_quizletQuestionFragment2, bundle)


            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = QuizItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return quizModelList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(quizModelList[position])
    }
}