package com.example.exovideoplayer

import android.content.res.Resources
import android.graphics.Rect
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VideoPlayScrollListener(
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.OnScrollListener() {
    private var lastVisibleViewHolder: VideoViewHolder? = null

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        // Find the first visible item position
        val firstVisiblePosition =
            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

        // Get the first visible ViewHolder
        val firstVisibleViewHolder =
            recyclerView.findViewHolderForAdapterPosition(firstVisiblePosition) as? VideoViewHolder

        // Check if the first visible ViewHolder has changed
        if (firstVisibleViewHolder != lastVisibleViewHolder) {
            // Detach the player from the last visible ViewHolder
            lastVisibleViewHolder?.onViewDetachedFromWindow(lifecycleOwner)

            // Attach the player to the new visible ViewHolder if its VideoPlayer is completely visible
            if (isVideoPlayerVisible(firstVisibleViewHolder?.player)) {
                firstVisibleViewHolder?.onViewAttachedToWindow(lifecycleOwner)
            }

            // Update the last visible ViewHolder
            lastVisibleViewHolder = firstVisibleViewHolder
        }
    }

    // Check if the VideoPlayer view is completely visible within its parent
    private fun isVideoPlayerVisible(videoPlayer: VideoPlayer?): Boolean {
        val rect = Rect()
        videoPlayer?.getGlobalVisibleRect(rect)

        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val videoPlayerHeight = videoPlayer?.height ?: 0 // Get the height of the VideoPlayer

        // Check if the VideoPlayer's height is completely visible within the screen's height
        return rect.top >= 0 && rect.bottom <= screenHeight && rect.height() >= videoPlayerHeight
    }
}






