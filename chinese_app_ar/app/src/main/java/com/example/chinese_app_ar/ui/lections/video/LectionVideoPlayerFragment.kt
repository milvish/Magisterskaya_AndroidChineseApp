package com.example.chinese_app_ar.ui.ar_room.video

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.findNavController
import com.example.chinese_app_ar.R
import com.google.firebase.firestore.FirebaseFirestore

class LectionVideoPlayerFragment : Fragment() {

    private var fileName: String = ""

    private var uri: Uri? = null
    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: Player? = null

    private var playWhenReady = false
    private var mediaItemIndex = 0
    private var playbackPosition = 0L

    private lateinit var viewModel: LectionVideoPlayerViewModel

    private lateinit var rootView: View
    private lateinit var grammarExoplayerView: PlayerView
    private lateinit var phoneticsExoplayerView: PlayerView
    private lateinit var hieroglyphicsExoplayerView: PlayerView
    private lateinit var testButton: Button

    private var phoneticsPlayer: ExoPlayer? = null
    private var grammarPlayer: ExoPlayer? = null
    private var hieroglyphicsPlayer: ExoPlayer? = null

    companion object {
        const val TAG = "ArVideoPlayerFragment"
        const val ARG_FILE_NAME = "fileName"

        fun newInstance(lessonModelItem: String): LectionVideoPlayerFragment {
            val fragment = LectionVideoPlayerFragment()
            val args = Bundle()
            args.putString(ARG_FILE_NAME, lessonModelItem)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_current_lesson, container, false)
        grammarExoplayerView = rootView.findViewById(R.id.grammarExoplayerView) // Replace with actual view ID
        phoneticsExoplayerView = rootView.findViewById(R.id.phoneticsExoplayerView)
        hieroglyphicsExoplayerView = rootView.findViewById(R.id.hieroglyphicsExoplayerView)
        testButton = rootView.findViewById(R.id.testButton)




        // Retrieve the fileName from the arguments
        fileName = arguments?.getString(ARG_FILE_NAME) ?: ""
        Log.d("fileName onCreateView", fileName)

        initializeViewModel()
        initializePhoneticsPlayer(fileName)
        initializeGrammarPlayer(fileName)
        initializeHieroglyphicsPlayer(fileName)

        testButton.setOnClickListener(){
            findNavController().navigate(R.id.nav_quizlet)
        }


        return rootView
    }


    private fun initializeViewModel() {
        viewModel = ViewModelProvider(this)[LectionVideoPlayerViewModel::class.java]

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LectionVideoPlayerViewModel::class.java)
        // TODO: Use the ViewModel
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Build.VERSION.SDK_INT <= 23 || player == null) {
            initializePhoneticsPlayer(fileName)
            initializeGrammarPlayer(fileName)
            initializeHieroglyphicsPlayer(fileName)
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            releasePlayers()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            releasePlayers()
        }
    }

    private fun initializePhoneticsPlayer(fileName: String) {
        phoneticsPlayer = ExoPlayer.Builder(requireActivity()).build().also { exoPlayer ->
            phoneticsExoplayerView.player = exoPlayer
            fetchVideoUrlAndPreparePlayer(exoPlayer, fileName, "phonetics")
        }

    }

    private fun initializeGrammarPlayer(fileName: String) {
        grammarPlayer = ExoPlayer.Builder(requireActivity()).build().also { exoPlayer ->
            grammarExoplayerView.player = exoPlayer
            fetchVideoUrlAndPreparePlayer(exoPlayer, fileName, "grammar")
        }

    }

    private fun initializeHieroglyphicsPlayer(fileName: String) {
        hieroglyphicsPlayer = ExoPlayer.Builder(requireActivity()).build().also { exoPlayer ->
            hieroglyphicsExoplayerView.player = exoPlayer
            fetchVideoUrlAndPreparePlayer(exoPlayer, fileName, "hieroglyphics")
        }

    }

    @OptIn(UnstableApi::class)
    private fun fetchVideoUrlAndPreparePlayer(exoPlayer: ExoPlayer, fileName: String, type: String) {

        //Log.d("Initialize", "fileName $fileName")
        //Toast.makeText(context, "Initialize. fileName: $fileName", Toast.LENGTH_SHORT).show()
        FirebaseFirestore.getInstance()
            .collection("lessons")
            .document(fileName)
            .get()
            .addOnSuccessListener { document ->
                val url = document.getString(type)
                Log.d("video url", url.toString())
                if (url == "") {
                    Log.e(TAG, "Video URL is null, type $type")
                    //Toast.makeText(requireContext(), "Video URL is null", Toast.LENGTH_SHORT).show()
                    when (type) {
                        "phonetics" -> hidePlayerView(phoneticsExoplayerView)
                        "grammar" -> hidePlayerView(grammarExoplayerView)
                        "hieroglyphics" -> hidePlayerView(hieroglyphicsExoplayerView)
                    }

                } else {
                    uri = Uri.parse(url)
                    Log.d(TAG, "Video URL: $url")
                    val mediaItem = MediaItem.Builder()
                        .setUri(uri)
                        .setMimeType(MimeTypes.BASE_TYPE_VIDEO)
                        .build()
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.seekTo(playbackPosition)
                    exoPlayer.addListener(playbackStateListener)
                    exoPlayer.prepare()
                    when (type) {
                        "phonetics" -> {
                            visionComponent(R.id.titlePhonetics, R.id.descriptionPhonetics, R.id.iconPhonetics, phoneticsExoplayerView)

                        }
                        "grammar" -> {
                            visionComponent(R.id.titleGrammar, R.id.descriptionGrammar, R.id.iconGrammar, grammarExoplayerView)
                        }
                        "hieroglyphics" -> {
                            visionComponent(R.id.titleHieroglyphics, R.id.descriptionHieroglyphics, R.id.iconHieroglyphics, hieroglyphicsExoplayerView)
                        }
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "get failed with ", exception)
                Toast.makeText(requireContext(), "Failed to fetch video URL", Toast.LENGTH_SHORT).show()
                when (type) {
                    "phonetics" -> hidePlayerView(phoneticsExoplayerView)
                    "grammar" -> hidePlayerView(grammarExoplayerView)
                    "hieroglyphics" -> hidePlayerView(hieroglyphicsExoplayerView)
                }
            }
    }
    private fun releasePlayers() {
        phoneticsPlayer?.let { player ->
            playbackPosition = player.currentPosition
            mediaItemIndex = player.currentMediaItemIndex
            playWhenReady = player.playWhenReady
            player.removeListener(playbackStateListener)
            player.release()
        }
        phoneticsPlayer = null

        grammarPlayer?.let { player ->
            playbackPosition = player.currentPosition
            mediaItemIndex = player.currentMediaItemIndex
            playWhenReady = player.playWhenReady
            player.removeListener(playbackStateListener)
            player.release()
        }
        grammarPlayer = null

        hieroglyphicsPlayer?.let { player ->
            playbackPosition = player.currentPosition
            mediaItemIndex = player.currentMediaItemIndex
            playWhenReady = player.playWhenReady
            player.removeListener(playbackStateListener)
            player.release()
        }
        hieroglyphicsPlayer = null
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        WindowInsetsControllerCompat(requireActivity().window, phoneticsExoplayerView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }


    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")
        }
    }

    private fun hidePlayerView(playerView: PlayerView) {
        playerView.visibility = View.GONE
    }

    private fun visionComponent (idTitle: Int, idDescription: Int, idIcon: Int, playerView: PlayerView){
        val title = rootView.findViewById<TextView>(idTitle)
        title.visibility = View.VISIBLE
        val description = rootView.findViewById<TextView>(idDescription)
        description.visibility = View.VISIBLE
        val icon = rootView.findViewById<ImageView>(idIcon)
        icon.visibility = View.VISIBLE
        icon.setOnClickListener{
            if (description.visibility == View.VISIBLE)
            {
                description.visibility = View.GONE
                playerView.visibility = View.GONE
            } else {
                description.visibility = View.VISIBLE
                playerView.visibility = View.VISIBLE
            }
        }
    }


}