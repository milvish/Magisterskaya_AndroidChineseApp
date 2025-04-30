package com.example.chinese_app_ar.ui.lections.lessons

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.chinese_app_ar.R
import com.example.chinese_app_ar.databinding.FragmentLessonsBinding
import com.example.chinese_app_ar.ui.ar_room.video.LectionVideoPlayerFragment
import com.example.chinese_app_ar.ui.lections.levels.LevelsModel

class LessonsFragment : Fragment(), LessonsListAdapter.OnLessonModelClickListener {

    companion object {
        fun newInstance() = LessonsFragment()
        lateinit var level: LevelsModel
    }

    private val viewModel: LessonsViewModel by viewModels()

    lateinit var binding: FragmentLessonsBinding
    lateinit var lessonsListAdapter: LessonsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLessonsBinding.inflate(layoutInflater, container, false)
        setupLessonsListRecyclerView()
        //binding.lessonsTextView.text = level.level

        Glide.with(binding.lessonsImageView).load(level.url)
            .apply(
                RequestOptions().transform(RoundedCorners(32))
            )
            .into(binding.lessonsImageView)
        setupLessonsListRecyclerView()
        return binding.root


        //return inflater.inflate(R.layout.fragment_lessons, container, false)
    }

    fun setupLessonsListRecyclerView(){
        lessonsListAdapter = LessonsListAdapter(level.lessons, level.level)
        binding.lessonsListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.lessonsListRecyclerView.adapter = lessonsListAdapter
        lessonsListAdapter.setOnLessonModelClickListener(this@LessonsFragment)
    }
/*
    override fun onLessonModelClicked(lessonModelItem: String) {
        Log.d("LessonsFragment filename", lessonModelItem)
        LectionVideoPlayerFragment.newInstance(lessonModelItem)
        val navController = Navigation.findNavController(requireView())
        navController.navigate(R.id.action_nav_lessons_to_lectionVideoPlayerFragment)
    }

 */
    override fun onLessonModelClicked(lessonModelItem: String) {
    Log.d("LessonsFragment filename", lessonModelItem)
    val navController = Navigation.findNavController(requireView())
    val bundle = Bundle().apply {
        putString(LectionVideoPlayerFragment.ARG_FILE_NAME, lessonModelItem)
    }
    navController.navigate(R.id.action_nav_lessons_to_lectionVideoPlayerFragment, bundle)
}


}