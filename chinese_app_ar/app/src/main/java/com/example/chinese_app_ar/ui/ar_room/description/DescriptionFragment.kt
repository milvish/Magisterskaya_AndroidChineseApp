package com.example.chinese_app_ar.ui.ar_room.description

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chinese_app_ar.R
import com.example.chinese_app_ar.databinding.FragmentDescriptionBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson

private const val ARG_PARAM1 = "name"

class DescriptionFragment : DialogFragment() {

    private var name: String? = null
    private lateinit var rootView: View

    private var _binding: FragmentDescriptionBinding? = null
    private val binding get() = _binding!!

    private lateinit var storage: FirebaseStorage
    private lateinit var modelRef: StorageReference


    companion object {
        const val TAG = "DescriptionFragment"
        @JvmStatic
        /*
        fun newInstance(name: String) =
            DescriptionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, name)
                }
            }

         */
        fun newInstance(): DescriptionFragment{
            return DescriptionFragment()

        }
        lateinit var category: String
        lateinit var word: String
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            name= it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentDescriptionBinding.inflate(inflater, container, false)
        var root: View = binding.root

        FirebaseApp.initializeApp(requireContext())
        storage = FirebaseStorage.getInstance()
        modelRef = storage.getReference().child("ar_models/description.json")

        loadJsonFromFirebase()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val layoutParams = view.layoutParams
        layoutParams.width = 900
        layoutParams.height = 1300

        // Установка прозрачности фона окна (от 0 - полностью прозрачно, до 1 - непрозрачно)
        val window = dialog?.window
        window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background2) // Прозрачный фон
        window?.setDimAmount(0.4f)

        //view.background = resources.getDrawable(R.drawable.rounded_corners_background)


        view.layoutParams = layoutParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadJsonFromFirebase() {
        modelRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            val json = String(bytes)
            parseJson(json)
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to load JSON", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseJson(json: String) {
        val gson = Gson()
        val categories = gson.fromJson(json, Categories::class.java)

        when (category) {
            "animals" -> {
                val animals = filterByCategoryAndWord(categories.animals, word)
                updateUI(animals)
            }
            "transport" -> {
                val transport = filterByCategoryAndWord(categories.transport, word)
                updateUI(transport)
            }
        }
    }

    private fun filterByCategoryAndWord(vocabularyList: List<Vocabulary>, word: String): List<Vocabulary> {
        return vocabularyList.filter { it.englishWord == word }
    }

    private fun updateUI(items: List<Vocabulary>) {
        if (items.isNotEmpty()) {
            val firstItem = items.first()
            binding.wordTextView.text = firstItem.russianWord
            binding.hieroglyphTextView.text = firstItem.chineseWord.hieroglyph
            binding.pinyinTextView.text = firstItem.chineseWord.pinyin

            val sentences = items.flatMap { it.sentences }
            val adapter = SentencesAdapter(sentences)
            binding.sentencesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.sentencesRecyclerView.adapter = adapter
        }
    }


}