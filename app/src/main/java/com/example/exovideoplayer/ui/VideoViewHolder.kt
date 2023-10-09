package com.example.exovideoplayer.ui

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.exovideoplayer.R

class VideoViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    private val TAG = VideoViewHolder::class.java.simpleName
    val player: VideoPlayer = itemView.findViewById(R.id.video_player)
    val bookmark: ImageButton = itemView.findViewById(R.id.img1)
    val gImg: ImageView = itemView.findViewById(R.id.sample_image)
    private var isPlayerInitialized = false
    fun bind(videoUrl: String? = null, playbackPos: Long = 0L, videoPlayer: VideoPlayer) {
        if (videoUrl != null) {
            player.initializePlayer(videoUrl)
            isPlayerInitialized = true
            Log.d(TAG, "Player initialized for URL: $videoUrl")
        }

        val isFirstItem = position == 0
        if (videoUrl != null && isFirstItem) {
            player.startPlayback(playbackPos)
        }
    }

    fun onViewAttachedToWindow(lifecycleOwner: LifecycleOwner, videoPlayer: VideoPlayer) {
        Log.d(TAG, "attached to window")
        player.attachLifecycle(lifecycleOwner.lifecycle)
    }

    fun onViewDetachedFromWindow(lifecycleOwner: LifecycleOwner, videoPlayer: VideoPlayer) {
        Log.d(TAG, "detached from window")
        player.detachLifecycle(lifecycleOwner.lifecycle)
    }

    fun releasePlayer() {
        player.releasePlayer()
    }

    fun getPlaybackPos(videoPlayer: VideoPlayer) = player.getPlaybackPosition()

    fun showImage() {
        gImg.visibility = View.VISIBLE
    }
}