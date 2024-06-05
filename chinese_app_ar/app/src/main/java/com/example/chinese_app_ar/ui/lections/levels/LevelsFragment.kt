package com.example.chinese_app_ar.ui.lections.levels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.chinese_app_ar.databinding.FragmentLevelsBinding
import com.google.firebase.firestore.FirebaseFirestore

class LevelsFragment : Fragment() {
    var _binding: FragmentLevelsBinding? = null
    lateinit var levelsAdapter: LevelsAdapter
    private val binding get() = _binding!!
    var levelsList: List<LevelsModel> = emptyList()

    companion object {
        //fun newInstance() = LevelsFragment()

        fun newInstance(levels: List<LevelsModel>): LevelsFragment {
            val fragment = LevelsFragment()
            fragment.levelsList = levels
            return fragment
        }
    }

    private val viewModel: LevelsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getLevels()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLevelsBinding.inflate(inflater, container, false)
        levelsAdapter = LevelsAdapter(emptyList())
        binding.levelsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.levelsRecyclerView.adapter = levelsAdapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getLevels()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getLevels() {
        FirebaseFirestore.getInstance().collection("im_levels")
            .get().addOnSuccessListener {
                levelsList = it.toObjects(LevelsModel:: class.java)
                setupLevelsRecyclerView(levelsList)
            }
    }

    fun setupLevelsRecyclerView(levelsList: List<LevelsModel>){
        this.levelsList = levelsList
        levelsAdapter = LevelsAdapter(levelsList)
        binding.levelsRecyclerView.adapter = levelsAdapter
    }
}