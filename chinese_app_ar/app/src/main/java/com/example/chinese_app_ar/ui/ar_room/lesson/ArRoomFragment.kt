package com.example.chinese_app_ar.ui.ar_room.lesson
//import com.example.chinese_app_ar.ui.ar.behavior
//import com.example.chinese_app_ar.ui.ar.toggle
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chinese_app_ar.R
import com.example.chinese_app_ar.databinding.FragmentArRoomBinding
import com.example.chinese_app_ar.ui.ar_room.description.DescriptionFragment
import com.example.chinese_app_ar.ui.ar_room.video.ArVideoPlayerFragment
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException

class ArRoomFragment : Fragment(), ArModelsAdapter.OnArModelClickListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var modelRef: StorageReference
    private lateinit var arFragment: ArFragment
    private var renderable: ModelRenderable? = null

    private var currentScaleFactor = 1.0f

    private var _binding: FragmentArRoomBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val bottomSheetScene get() = binding.bottomSheetScene
    private val bottomSheetNode get() = binding.bottomSheetNode
    lateinit var arModelsAdapter: ArModelsAdapter
    private lateinit var modelArrayList: ArrayList<ArRoom3dModel>

    companion object {
        fun newInstance() = ArRoomFragment()
        lateinit var category: String
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArRoomBinding.inflate(inflater, container, false)
        val root: View = binding.root

        modelArrayList = arrayListOf()
        FirebaseApp.initializeApp(requireContext())
        db = FirebaseFirestore.getInstance()

        storage = FirebaseStorage.getInstance()
        modelRef = storage.getReference().child("ar_models/shiba.glb")

        arFragment = childFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        EventChangeListener(category)
        initSceneBottomSheet()



        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            if (!tryPlaceModel(hitResult)) {
                Toast.makeText(
                    requireContext(),
                    "Tap on an existing model to perform some action",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(requireContext(),
                    "On 3d model tapped",
                    Toast.LENGTH_SHORT).show()
            }
        }


        return root
    }

    private fun tryPlaceModel(hitResult: HitResult): Boolean {
        renderable?.let { renderable ->
            val anchorNode = AnchorNode(hitResult.createAnchor())
            TransformableNode(arFragment.transformationSystem).apply {
                setParent(anchorNode)
                setRenderable(renderable)
                scaleController.maxScale = 0.3f // Максимальный масштаб модели
                scaleController.minScale = 0.01f // Минимальный масштаб модели
                localScale = Vector3(currentScaleFactor, currentScaleFactor, currentScaleFactor)
            }.also {
                arFragment.arSceneView.scene.addChild(anchorNode)
            }
            return true
        }
        return false
    }





private fun downloadModel(name: String) {
    //Toast.makeText(context, "$name.glb", Toast.LENGTH_SHORT).show()

    // Get the URL from Firestore
    db.collection("3d_models").document(name).get()
        .addOnSuccessListener { document ->
            if (document != null && document.contains("model")) {
                val modelUrl = document.getString("model")
                //Toast.makeText(context, "$modelUrl", Toast.LENGTH_SHORT).show()
                //Log.d("MODEL URL", "modelUrl: $modelUrl")
                if (!modelUrl.isNullOrEmpty()) {
                    try {
                        val file = File.createTempFile("out", "glb")

                        // Download the model file from the URL
                        downloadFileFromUrl(modelUrl, file, name)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Failed to get model URL", Toast.LENGTH_SHORT).show()
            Log.e("Firestore Error", "Error getting model URL", e)
        }
}

    private fun downloadFileFromUrl(url: String, file: File, name: String) {
        FirebaseStorage.getInstance().getReferenceFromUrl(url).getFile(file)
            .addOnSuccessListener {
                buildModel(file, name)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to download model", Toast.LENGTH_SHORT).show()
            }
    }



    private fun buildModel(file: File, name: String) {
        val renderableSource = RenderableSource.builder()
            .setSource(requireContext(), Uri.parse(file.path), RenderableSource.SourceType.GLB)
            .setRecenterMode(RenderableSource.RecenterMode.ROOT)
            .build()

        ModelRenderable.builder()
            .setSource(requireContext(), renderableSource)
            .setRegistryId(file.path)
            .build()
            .thenAccept { modelRenderable ->
                Toast.makeText(requireContext(), "Model $name built", Toast.LENGTH_SHORT).show()
                renderable = modelRenderable
            }
    }

    private fun initSceneBottomSheet() =  with(bottomSheetScene) {
        //behavior().state = BottomSheetBehavior.STATE_EXPANDED
        //header.root.setOnClickListener { behavior().toggle() }

        arModelsAdapter = ArModelsAdapter(modelArrayList, this@ArRoomFragment)
        body.arModelRecyclerView.apply {
            adapter = arModelsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        arModelsAdapter.setOnArModelClickListener(this@ArRoomFragment)

        header.description.setOnClickListener{
            val descriptionFragment = DescriptionFragment.newInstance()
            descriptionFragment.show(parentFragmentManager, DescriptionFragment.TAG)
        }


    }



    private fun EventChangeListener(category: String) {
        var categoryList = emptyList<String>()

        db.collection("3d_categories").document(category).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Extract the "3d_models" field from the document
                    val models = document.get("3d_models") as? List<String>
                    if (models != null) {
                        categoryList = models

                        addModelsSnapshotListener(categoryList)
                    }
                    Log.d("CATEGORY LIST", "$categoryList")
                } else {
                    Log.e("Firestore Error", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Error", exception.message.toString())
            }


    }

    private fun addModelsSnapshotListener(categoryList: List<String>) {

        db.collection("3d_models").orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?) {
                    if (error != null){
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            val model = dc.document.toObject(ArRoom3dModel::class.java)
                            if (categoryList.contains(model.name)) {
                                modelArrayList.add(model)
                            }
                        }
                    }

                    arModelsAdapter.notifyDataSetChanged()
                }
            })
    }

    override fun onArModelClicked(arRoom3dModel: ArRoom3dModel, position: Int) {
        // Handle the click event here
        // You can access the selected ArModelItem and its position
        downloadModel(arRoom3dModel.name)
        bottomSheetScene.header.playPause.setOnClickListener{
            Toast.makeText(requireContext(), arRoom3dModel.video, Toast.LENGTH_SHORT).show()
            ArVideoPlayerFragment.newInstance(arRoom3dModel.name).show(requireActivity().supportFragmentManager, ArVideoPlayerFragment.TAG)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}