package com.example.exovideoplayer

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

class VideoPlayScrollListener(
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        // Attach the player to the top-most item if it's scrolled at least 50% off the screen
        recyclerView.adapter?.let { rvAdapter ->
            for (i in 0 until rvAdapter.itemCount) {
                val viewHolder =
                    recyclerView.findViewHolderForAdapterPosition(i) as? VideoViewHolder
                viewHolder?.let {
                    isVideoPlayerCompletelyVisible(it, viewHolder.itemView)
                }.apply {
                    if (this == true) {
                        viewHolder?.onViewAttachedToWindow(lifecycleOwner)
                    } else {
                        viewHolder?.onViewDetachedFromWindow(lifecycleOwner)
                    }
                }
            }
        }
    }
    // Check if the VideoPlayer view is completely visible within its parent
    private fun isVideoPlayerCompletelyVisible(videoViewHolder: VideoViewHolder, parent: View): Boolean {
        val videoPlayerView = videoViewHolder.player // Adjust this to your actual VideoPlayer view
        val rect = Rect()
        parent.getLocalVisibleRect(rect)
        // Check if the visible portion of the VideoPlayer view covers the entire view's height (or width)
        return videoPlayerView.getLocalVisibleRect(rect) && rect.bottom - rect.top >= videoPlayerView.height
    }
}




