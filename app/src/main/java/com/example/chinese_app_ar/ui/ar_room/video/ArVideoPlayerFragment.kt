package com.example.chinese_app_ar.ui.ar_room.video

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.chinese_app_ar.R
import com.google.firebase.firestore.FirebaseFirestore

class ArVideoPlayerFragment : DialogFragment() {

    private  var fileName: String = ""

    private var uri: Uri? = null
    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: Player? = null

    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L

    private lateinit var viewModel: ArVideoPlayerViewModel

    private lateinit var rootView: View
    private lateinit var someView: PlayerView


    companion object {
        const val TAG = "ArVideoPlayerFragment"

        fun newInstance(fileName: String): ArVideoPlayerFragment {
            val fragment = ArVideoPlayerFragment()
            fragment.fileName = fileName
            return fragment
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_video_player_ar, container, false)
        someView = rootView.findViewById(R.id.exoplayerView) // Replace with actual view ID

        initializePlayer(fileName)
        initializeViewModel()

        return rootView

    }

    private fun initializeViewModel() {
        viewModel = ViewModelProvider(this)[ArVideoPlayerViewModel::class.java]
        // Set the ViewModel to the views manually
        //someView.text =

        // Set the lifecycle owner if necessary
        // lifecycle.addObserver(viewModel)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ArVideoPlayerViewModel::class.java)
        // TODO: Use the ViewModel
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Build.VERSION.SDK_INT <= 23 || player == null) {
            initializePlayer(fileName)
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer(fileName: String) {
        player = ExoPlayer.Builder(requireActivity()).build().also { exoPlayer ->
            someView.player = exoPlayer
            fetchVideoUrlAndPreparePlayer(exoPlayer, fileName)
        }
    }

    @OptIn(UnstableApi::class)
    private fun fetchVideoUrlAndPreparePlayer(exoPlayer: ExoPlayer, fileName: String) {

        Log.d("Initialize", "fileName $fileName")
        Toast.makeText(context, "Initialize. fileName: $fileName", Toast.LENGTH_SHORT).show()
        FirebaseFirestore.getInstance()
            .collection("3d_models")
            .document(fileName)
            .get()
            .addOnSuccessListener { document ->
                val url = document.getString("video")
                Log.d("video url", url.toString())
                if (url != null) {
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
                } else {
                    Log.e(TAG, "Video URL is null")
                    Toast.makeText(requireContext(), "Video URL is null", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "get failed with ", exception)
                Toast.makeText(requireContext(), "Failed to fetch video URL", Toast.LENGTH_SHORT).show()
            }
    }
    private fun releasePlayer() {
        player?.let { player ->
            playbackPosition = player.currentPosition
            mediaItemIndex = player.currentMediaItemIndex
            playWhenReady = player.playWhenReady
            player.removeListener(playbackStateListener)
            player.release()
        }
        player = null
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        WindowInsetsControllerCompat(requireActivity().window, someView).let { controller ->
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
}