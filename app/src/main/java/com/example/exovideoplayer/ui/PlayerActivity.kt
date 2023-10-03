package com.example.exovideoplayer.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.example.exovideoplayer.CustomSelectionAdapter
import com.example.exovideoplayer.R
import com.example.exovideoplayer.databinding.ActivityPlayerBinding
import com.example.exovideoplayer.domain.repository.SamplePagingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * A fullscreen activity to play audio or video streams.
 */

private const val TAG = "PlayerActivity"
@AndroidEntryPoint
class PlayerActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    private val model: SamplePagingViewModel by viewModels()

    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: Player? = null

    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L
    private var mediaVolume = 0f
    private var customSeekBar: SeekBar? = null
    private var volumeButton: ImageButton? = null
    private var thumbanail: ImageView? = null
    private var playPauseButton: ImageButton? = null
    private var fastForward: ImageButton? = null
    private var fastRewind: ImageButton? = null
    private var isMuted: Boolean = false
    private var timeDurationText: TextView? = null
    private var screenDimension: ImageButton? = null
    private var isFullScreen: Boolean = true
    private lateinit var fastForwardAnimation: Animation
    private lateinit var fastRewindAnimation: Animation
    private lateinit var playPauseAnimation: Animation
    private var currentQualityIndex = 0
    private var selectedQuality = 1
    private val videoUrl = "https://storage.googleapis.com/stockgro-feed-assets-dev/post_media/9fcc25e4-f616-4a40-96cb-94b6398c230e.mp4"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        customSeekBar = viewBinding.videoView.findViewById(R.id.custom_seek_bar)
        volumeButton = viewBinding.videoView.findViewById(R.id.volume_button)
        playPauseButton = viewBinding.videoView.findViewById(R.id.play_pause_button)
        fastForward = viewBinding.videoView.findViewById(R.id.fast_forward)
        fastRewind = viewBinding.videoView.findViewById(R.id.fast_rewind)
        timeDurationText = viewBinding.videoView.findViewById(R.id.time_duration)
        screenDimension = viewBinding.videoView.findViewById(R.id.fullscreen)
        viewBinding.videoView.findViewById<ImageButton>(R.id.settings).setOnClickListener {
            showSettingsDialog()
        }
        thumbanail = viewBinding.thumbnailImage
        //setVideoThumbnail()
        fastForwardAnimation = AnimationUtils.loadAnimation(this, R.anim.fast_foward_animation)
        fastRewindAnimation = AnimationUtils.loadAnimation(this, R.anim.fast_rewind_animation)
        playPauseAnimation = AnimationUtils.loadAnimation(this, R.anim.play_pause_animation)
        customSeekBar?.setOnSeekBarChangeListener(this)
        initRvAdapter()
    }

    public override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            //initializePlayer(videoUrl)
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Build.VERSION.SDK_INT <= 23 || player == null) {
            //initializePlayer(videoUrl)
        }
    }

    private var recyclerView: RecyclerView? = null
    private val lifecycleOwner: LifecycleOwner = this
    private fun initRvAdapter() {
        recyclerView = viewBinding.videoRecyclerview
        val videoList = mutableListOf(
            getString(R.string.media_url_mp4),
            null,
            getString(R.string.media_url_mp3),
            getString(R.string.media_url_mp4),
            getString(R.string.media_url_mp3),
        )

        //val adapter = VideoRvAdapter(videoList)
        val adapter = VideoPagingAdapter()
        /*lifecycleScope.launch {
            adapter.submitData(
                PagingData.from(
                    listOf(
                        getString(R.string.media_url_mp4),
                        "",
                        getString(R.string.media_url_mp3),
                        getString(R.string.media_url_mp4),
                        getString(R.string.media_url_mp3)
                    )
                )
            )
        }*/

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.pageListFlow.collectLatest {
                    adapter.submitData(it)
                }
            }
        }

        lifecycleScope.launch {
            model.updatePageList(videoList[0]!!)
        }

        recyclerView?.adapter = adapter
        val videoPlayScrollListener = VideoPlayScrollListener(lifecycleOwner)
        recyclerView?.addOnScrollListener(videoPlayScrollListener)
        viewBinding.addToFavourites.setOnClickListener {
            adapter.notifyDataSetChanged()
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

    @SuppressLint("UnsafeOptInUsageError")
    private fun setVideoThumbnail() {
        //this is with surface view, needs to do some work on this
        //val textureView = viewBinding.videoView.videoSurfaceView as TextureView
        //val bitmap = textureView.bitmap
        //thumbanail?.setImageBitmap(bitmap)
        val thumbnailImage = BitmapFactory.decodeResource(this.resources, R.drawable.google_logo)
        thumbanail?.setImageBitmap(thumbnailImage)
        thumbanail?.visibility = View.VISIBLE

        /* Testing code below
        val thumb = ThumbnailUtils.createVideoThumbnail(
            "file path/url",
            MediaStore.Images.Thumbnails.MINI_KIND
        )
        val bitmapDrawable = BitmapDrawable(thumbnailImage)
        viewBinding.videoView.setBackgroundDrawable(bitmapDrawable)
        */
    }

    private fun initializePlayer(videoUrl: String = getString(R.string.media_url_dash)) {
        // ExoPlayer implements the Player interface
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .build()
            .also { exoPlayer ->
                viewBinding.videoView.player = exoPlayer
                // Update the track selection parameters to only pick standard definition tracks
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                    .buildUpon()
                    .setMaxVideoSizeSd()
                    .build()

                //for dash URL streaming
                /*val mediaItem = MediaItem.Builder()
                    .setUri(videoUrl)
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .build()*/
                val mediaItem = MediaItem.fromUri(videoUrl)
                exoPlayer.setMediaItems(listOf(mediaItem), mediaItemIndex, playbackPosition)
                exoPlayer.playWhenReady = playWhenReady
                handler.post(updateProgressAction)
                exoPlayer.addListener(playbackStateListener)
                volumeButton?.setOnClickListener {
                    isMuted = !isMuted
                    exoPlayer.volume = if (isMuted) 0f else 1f
                    updateVolumeButtonState()
                }
                playPauseButton?.setOnClickListener {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                        handler.removeCallbacks(updateProgressAction)
                    } else {
                        exoPlayer.play()
                        handler.post(updateProgressAction)
                    }
                    it.startAnimation(playPauseAnimation)
                    updatePlayPauseButtonState(exoPlayer)
                }
                fastForward?.setOnClickListener {
                    val currentPosition = exoPlayer.currentPosition
                    it.startAnimation(fastForwardAnimation)
                    exoPlayer.seekTo(currentPosition + 10000) // Fast forward 10 seconds
                }

                fastRewind?.setOnClickListener {
                    val currentPosition = exoPlayer.currentPosition
                    it?.startAnimation(fastRewindAnimation)
                    exoPlayer.seekTo(currentPosition - 10000) // Rewind 10 seconds
                }
                screenDimension?.setOnClickListener {
                    isFullScreen = !isFullScreen
                    toggleFullscreen(isFullScreen)
                }
                exoPlayer.prepare()
            }
    }

    private fun releasePlayer() {
        player?.let { player ->
            playbackPosition = player.currentPosition
            mediaItemIndex = player.currentMediaItemIndex
            playWhenReady = player.playWhenReady
            player.removeListener(playbackStateListener)
            handler.removeCallbacks(updateProgressAction)
            player.release()
        }
        player = null
    }
    private fun updateVolumeButtonState() {
        val muteIcon = if (isMuted) {
            R.drawable.ic_volume_off
        } else {
            R.drawable.ic_volume_up
        }
        volumeButton?.setImageResource(muteIcon)
    }
    private fun toggleFullscreen(isFullscreen: Boolean) {
        if (isFullscreen) {
            val layoutParams = viewBinding.videoView.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            viewBinding.videoView.layoutParams = layoutParams
            screenDimension?.setImageResource(R.drawable.ic_fullscreen_exit)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            val layoutParams = viewBinding.videoView.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            viewBinding.videoView.layoutParams = layoutParams
            screenDimension?.setImageResource(R.drawable.ic_fullscreen)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun updatePlayPauseButtonState(exoPlayer: ExoPlayer) {
        val playPauseIcon = if (exoPlayer.isPlaying) {
            R.drawable.ic_pause_circle
        } else {
            R.drawable.play_circle
        }
        playPauseButton?.setImageResource(playPauseIcon)
    }

    private fun updateProgress() {
        player?.let {
            val currentPosition = it.currentPosition
            val totalDuration = it.duration
            val progressPercentage = (currentPosition * 100 / totalDuration).toInt()
            customSeekBar?.progress = progressPercentage
            val formattedProgress = formatDuration(currentPosition)
            val formattedTotalDuration = formatDuration(totalDuration)
            val progressText = "$formattedProgress / $formattedTotalDuration"
            timeDurationText?.text = progressText
        }
    }

    private val handler = Handler()

    private val updateProgressAction = object : Runnable {
        override fun run() {
            updateProgress()
            handler.postDelayed(this, 1000) // Update every second
        }
    }

    private fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }


    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, viewBinding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        val qualityOption = dialogView.findViewById<TextView>(R.id.qualityOption)
        val speedOption = dialogView.findViewById<TextView>(R.id.speedOption)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        qualityOption.setOnClickListener {
            // Handle quality option click
            showQualityDialogOptions()
            dialog.dismiss()
        }

        speedOption.setOnClickListener {
            // Handle speed option click
            showSpeedOptionsDialog()
            dialog.dismiss()
        }

        dialog.show()
    }

    private var selectedSpeedPosition: Int = 2 // Default selection
    private val playbackSpeedOptions = arrayOf("-2x", "-1.5x", "1x", "1.5x", "2x")
    private val playbackSpeedValues = mutableMapOf(
        "-2x" to 0.5f,
        "-1.5x" to 0.75f,
        "1x" to 1.0f,
        "1.5x" to 1.5f,
        "2x" to 2.0f
    )

    private fun showSpeedOptionsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_quality_selection, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.titleTextView)
        val playbackListView = dialogView.findViewById<ListView>(R.id.qualityListView)

        titleTextView.text = getString(R.string.playback_speed)

        val adapter = CustomSelectionAdapter(this, playbackSpeedOptions)
        playbackListView.adapter = adapter

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        playbackListView.setOnItemClickListener { _, _, position, _ ->
            // Handle playback speed selection here
            val selectedSpeedOption = playbackSpeedOptions[position]
            val selectedSpeed = playbackSpeedValues[selectedSpeedOption]

            selectedSpeed?.let {
                player?.setPlaybackSpeed(selectedSpeed)
                selectedSpeedPosition = position
                dialog.dismiss()
            }
        }

        adapter.setSelectedPosition(selectedSpeedPosition)
        adapter.notifyDataSetChanged()

        dialog.show()
    }

    private val qualityOptions = arrayOf("144p", "240p", "360p", "480p", "720p", "1080p")
    private fun showQualityDialogOptions() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_quality_selection, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.titleTextView)
        val qualityListView = dialogView.findViewById<ListView>(R.id.qualityListView)

        titleTextView.text = getString(R.string.quality)

        val adapter = CustomSelectionAdapter(this, qualityOptions)
        qualityListView.adapter = adapter

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        qualityListView.setOnItemClickListener { _, _, position, _ ->
            // Handle quality selection here
            selectedQuality = position
            dialog.dismiss()
            updatePlayerMediaItem()
        }

        adapter.setSelectedPosition(selectedQuality)
        adapter.notifyDataSetChanged()

        dialog.show()
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: Unit = when (playbackState) {
                ExoPlayer.STATE_IDLE -> {}
                ExoPlayer.STATE_BUFFERING -> {
                    updateProgress()
                }

                ExoPlayer.STATE_READY -> {
                    thumbanail?.visibility = View.GONE
                    viewBinding.videoView.useController = true
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
                ExoPlayer.STATE_ENDED -> {}
                else -> { viewBinding.videoView.useController = false }
            }
            Log.d(TAG, "changed state to $stateString")
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
    }

    private val qualityUrls = arrayOf(
        "url_for_144p",
        "url_for_240p",
        "url_for_360p",
        "url_for_480p",
        "url_for_720p",
        "url_for_1080p"
    )

    private fun updatePlayerMediaItem() {
        val currentMediaItem = player?.currentMediaItem
        val currentPosition = player?.currentPosition ?: 0
        val newMediaItem = MediaItem.Builder()
            .setUri(getString(R.string.media_url_dash)/*qualityUrls[selectedQuality]*/)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()

        player?.setMediaItems(listOf(newMediaItem), mediaItemIndex, playbackPosition)
        player?.seekTo(currentPosition)
        player?.prepare()
        player?.play()
    }

    override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            player?.let {
                val totalDuration = it.duration
                val newPosition = (totalDuration * progress) / 100
                it.seekTo(newPosition)
                updateProgress()
            }
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {}

    override fun onStopTrackingTouch(p0: SeekBar?) {}
}