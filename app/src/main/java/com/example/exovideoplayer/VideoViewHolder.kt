package com.example.exovideoplayer

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView

class VideoViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    private val player: VideoPlayer = itemView.findViewById(R.id.video_player)
    private val lifecycle: Lifecycle?
        get() = itemView.findViewTreeLifecycleOwner()?.lifecycle

    fun bind(videoUrl: String) {
        lifecycle?.let {
            player.attachLifecycle(it)
        }
        player.initializePlayer(videoUrl)
    }

    fun onViewAttachedToWindow() {
        player.startPlayback()
    }

    fun onViewDetachedFromWindow() {
        player.pausePlayback()
        lifecycle?.let {
            player.detachLifecycle(it)
        }
    }
}