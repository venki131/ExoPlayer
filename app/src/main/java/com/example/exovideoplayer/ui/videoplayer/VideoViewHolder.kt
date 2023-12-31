package com.example.exovideoplayer.ui.videoplayer

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
    fun bind(videoUrl: String? = null, playbackPos: Long = 0L) {
        if (videoUrl != null) {
            player.initializePlayer(videoUrl)
        }
    }

    fun onViewAttachedToWindow(lifecycleOwner: LifecycleOwner) {
        Log.d(TAG, "attached to window")
        player.attachLifecycle(lifecycleOwner.lifecycle)
    }

    fun onViewDetachedFromWindow(lifecycleOwner: LifecycleOwner) {
        Log.d(TAG, "detached from window")
        player.detachLifecycle(lifecycleOwner.lifecycle)
    }

    fun releasePlayer() {
        player.releasePlayer()
    }

    fun getPlaybackPos() = player.getPlaybackPosition()

    fun showImage() {
        gImg.visibility = View.VISIBLE
    }
    fun hideImage() {
        gImg.visibility = View.GONE
    }
}