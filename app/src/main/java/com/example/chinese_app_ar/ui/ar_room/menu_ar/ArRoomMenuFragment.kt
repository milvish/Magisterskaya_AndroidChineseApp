package com.example.chinese_app_ar.ui.ar_room.menu_ar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.chinese_app_ar.databinding.FragmentArRoomMenuBinding
import com.google.firebase.firestore.FirebaseFirestore

class ArRoomMenuFragment : Fragment() {

    var _binding: FragmentArRoomMenuBinding? = null
    lateinit var arRoomMenuAdapter: ArRoomMenuAdapter
    private val binding get() = _binding!!
    var arRoomMenuList: List<ArRoomMenuModel> = emptyList()
    companion object {
        fun newInstance() = ArRoomMenuFragment()

    }

    private val viewModel: ArRoomMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentArRoomMenuBinding.inflate(inflater, container, false)
        arRoomMenuAdapter = ArRoomMenuAdapter(emptyList())
        binding.arRoomMenuRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.arRoomMenuRecyclerView.adapter = arRoomMenuAdapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getArRoomMenu()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getArRoomMenu() {
        FirebaseFirestore.getInstance().collection("3d_categories")
            .get().addOnSuccessListener {
                arRoomMenuList = it.toObjects(ArRoomMenuModel:: class.java)
                setupArRoomMenuRecyclerView(arRoomMenuList)
            }
    }

    private fun setupArRoomMenuRecyclerView(arRoomMenuList: List<ArRoomMenuModel>){
        this.arRoomMenuList = arRoomMenuList
        arRoomMenuAdapter = ArRoomMenuAdapter(arRoomMenuList)
        binding.arRoomMenuRecyclerView.adapter = arRoomMenuAdapter
    }


}