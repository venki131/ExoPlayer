package com.example.exovideoplayer

//import com.assetgro.stockgro.databinding.LayoutVideoPlayerBinding
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.exovideoplayer.databinding.LayoutVideoPlayerBinding
import java.util.Locale
import java.util.concurrent.TimeUnit

class VideoPlayer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), SeekBar.OnSeekBarChangeListener, LifecycleObserver {

    private var _context: Context? = null

    private lateinit var binding: LayoutVideoPlayerBinding

    var onVideoPlayerAction: OnVideoPlayerAction? = null

    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: Player? = null

    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L
    private var mediaVolume = 0f
    private var customSeekBar: SeekBar? = null
    private var volumeButton: ImageButton? = null
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

    private val handler = Handler()


    private val lifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            // Called when the containing component is started
            // resume video playback
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            // Called when the containing component is stopped
            // pause video playback
            pausePlayback()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            startPlayback()
        }
        // Implement other lifecycle events as needed
    }

    fun attachLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
    }

    fun detachLifecycle(lifecycle: Lifecycle) {
        lifecycle.removeObserver(lifecycleObserver)
    }

    init {
        init(context)
        keepScreenOn = true //keep this in-order to avoid screen lock when playing the video
    }
    private fun init(context: Context) {
        _context = context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = LayoutVideoPlayerBinding.inflate(
            inflater,
            this,
            true
        )

        customSeekBar = binding.videoView.findViewById(R.id.custom_seek_bar)
        volumeButton = binding.videoView.findViewById(R.id.volume_button)
        playPauseButton = binding.videoView.findViewById(R.id.play_pause_button)
        fastForward = binding.videoView.findViewById(R.id.fast_forward)
        fastRewind = binding.videoView.findViewById(R.id.fast_rewind)
        timeDurationText = binding.videoView.findViewById(R.id.time_duration)
        screenDimension = binding.videoView.findViewById(R.id.fullscreen)
        /*binding.videoView.findViewById<ImageButton>(R.id.settings).setOnClickListener {
            showSettingsDialog()
        }*/
        fastForwardAnimation = AnimationUtils.loadAnimation(context, R.anim.fast_foward_animation)
        fastRewindAnimation = AnimationUtils.loadAnimation(context, R.anim.fast_rewind_animation)
        playPauseAnimation = AnimationUtils.loadAnimation(context, R.anim.play_pause_animation)
        customSeekBar?.setOnSeekBarChangeListener(this)
    }

    fun initializePlayer(videoUrl: String, isFullScreen: Boolean = false) {
        setFullScreenView(isFullScreen)

        player = ExoPlayer.Builder(context)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer
                // Update the track selection parameters to only pick standard definition tracks
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                    .buildUpon()
                    .setMaxVideoSizeSd()
                    .build()

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
                    this.isFullScreen = !this.isFullScreen
                    onVideoPlayerAction?.onFullScreenClicked()

                }
                exoPlayer.prepare()
            }
    }

    private fun setFullScreenView(isFullScreen: Boolean) {
        this.isFullScreen = isFullScreen
        val drawable = if (isFullScreen)
            R.drawable.ic_fullscreen_exit
        else R.drawable.ic_fullscreen
        screenDimension?.setImageDrawable(context.getDrawable(drawable))
    }

    /*private fun showSettingsDialog() {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.layout_video_settings_dialog, null)
//        val qualityOption = dialogView.findViewById<TextView>(R.id.qualityOption)
        val speedOption = dialogView.findViewById<TextView>(R.id.speedOption)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Disabling option to select Quality of video for now

        *//* qualityOption.setOnClickListener {
             // Handle quality option click
             showQualityDialogOptions()
             dialog.dismiss()
         }*//*

        speedOption.setOnClickListener {
            // Handle speed option click
            showSpeedOptionsDialog()
            dialog.dismiss()
        }

        dialog.show()
    }*/

    fun releasePlayer() {
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

    fun startPlayback() {
        player?.play()
    }

    fun pausePlayback() {
        player?.pause()
    }

    private fun updateVolumeButtonState() {
        val muteIcon = if (isMuted) {
            R.drawable.ic_volume_off
        } else {
            R.drawable.ic_volume_up
        }
        volumeButton?.setImageResource(muteIcon)
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: Unit = when (playbackState) {
                ExoPlayer.STATE_IDLE -> {}
                ExoPlayer.STATE_BUFFERING -> {
                    updateProgress()
                }

                ExoPlayer.STATE_READY -> {
                    binding.videoView.useController = true
                }

                ExoPlayer.STATE_ENDED -> {}
                else -> {
                    binding.videoView.useController = false
                }
            }
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
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

    private fun updatePlayPauseButtonState(exoPlayer: ExoPlayer) {
        val playPauseIcon = if (exoPlayer.isPlaying) {
            R.drawable.ic_pause_circle
        } else {
            R.drawable.play_circle
        }
        playPauseButton?.setImageResource(playPauseIcon)
    }

    private val updateProgressAction = object : Runnable {
        override fun run() {
            updateProgress()
            handler.postDelayed(this, 1000) // Update every second
        }
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

    /*private fun showSpeedOptionsDialog() {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.layout_video_quality_selection_dialog, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.titleTextView)
        val playbackListView = dialogView.findViewById<ListView>(R.id.qualityListView)

        titleTextView.text = context.getString(R.string.playback_speed)

        val adapter = CustomSelectionAdapter(context, playbackSpeedOptions)
        playbackListView.adapter = adapter

        val dialog = AlertDialog.Builder(context)
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
    }*/

    private val qualityOptions = arrayOf("144p", "240p", "360p", "480p", "720p", "1080p")
    /*private fun showQualityDialogOptions() {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.layout_video_quality_selection_dialog, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.titleTextView)
        val qualityListView = dialogView.findViewById<ListView>(R.id.qualityListView)

        titleTextView.text = context.getString(R.string.quality)

        val adapter = CustomSelectionAdapter(context, qualityOptions)
        qualityListView.adapter = adapter

        val dialog = AlertDialog.Builder(context)
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
    }*/

    private fun updatePlayerMediaItem() {
        /* val currentMediaItem = player?.currentMediaItem
         val currentPosition = player?.currentPosition ?: 0
         val newMediaItem = MediaItem.fromUri(videoUrl)
         player?.setMediaItems(listOf(newMediaItem), mediaItemIndex, playbackPosition)
         player?.seekTo(currentPosition)
         player?.prepare()
         player?.play()*/
    }

    private fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private val qualityUrls = arrayOf(
        "url_for_144p",
        "url_for_240p",
        "url_for_360p",
        "url_for_480p",
        "url_for_720p",
        "url_for_1080p"
    )

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

    interface OnVideoPlayerAction {
        fun onFullScreenClicked()
    }

    inner class CustomSelectionAdapter(
        context: Context,
        options: Array<String>
    ) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, options) {

        private var selectedPosition = -1

        fun setSelectedPosition(position: Int) {
            selectedPosition = position
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val itemView = super.getView(position, convertView, parent)
            val textView = itemView.findViewById<TextView>(android.R.id.text1)

            if (position == selectedPosition) {
                textView.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_check_circle,
                    0
                )
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            return itemView
        }
    }
}