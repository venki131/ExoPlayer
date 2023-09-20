package com.example.exovideoplayer

import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

class VideoViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    private val TAG = VideoViewHolder::class.java.simpleName
    private val player: VideoPlayer = itemView.findViewById(R.id.video_player)
    private var isPlayerInitialized = false
    fun bind(videoUrl: String) {
        if (isPlayerInitialized.not()) {
            player.initializePlayer(videoUrl)
            isPlayerInitialized = true
            Log.d(TAG, "Player initialized for URL: $videoUrl")
        }

        val isFirstItem = position == 0

        if (isFirstItem) {
            player.startPlayback()
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
}