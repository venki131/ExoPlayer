package com.example.exovideoplayer

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VideoPlayScrollListener(
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return

        // Calculate the height of the RecyclerView
        val recyclerViewHeight = recyclerView.height

        // Get the first fully visible item position
        val firstCompletelyVisibleItemPosition =
            layoutManager.findFirstCompletelyVisibleItemPosition()

        // Attach the player to the top-most item if it's scrolled at least 50% off the screen
        recyclerView.adapter?.let { rvAdapter ->
            for (i in 0 until rvAdapter.itemCount) {
                val viewHolder =
                    recyclerView.findViewHolderForAdapterPosition(i) as? VideoViewHolder
                val attachPlayer = shouldAttachPlayer(
                    i,
                    firstCompletelyVisibleItemPosition,
                    recyclerViewHeight,
                    viewHolder
                )
                viewHolder?.apply {
                    if (attachPlayer) {
                        onViewAttachedToWindow(lifecycleOwner)
                    } else {
                        onViewDetachedFromWindow(lifecycleOwner)
                    }
                }
            }
        }
    }

    private fun shouldAttachPlayer(
        itemPosition: Int,
        firstVisiblePosition: Int,
        recyclerViewHeight: Int,
        viewHolder: RecyclerView.ViewHolder?
    ): Boolean {
        viewHolder ?: return false

        val view = viewHolder.itemView
        val itemTop = view.top
        val itemBottom = view.bottom
        val itemHeight = view.height

        // Calculate the percentage of the item that is above the screen
        val percentageAboveScreen = (recyclerViewHeight - itemTop).toFloat() / itemHeight

        // Calculate the percentage of the item that is below the screen
        val percentageBelowScreen = (itemBottom - recyclerViewHeight).toFloat() / itemHeight

        // Check if the item is the top-most item and is scrolled at least 50% off the screen
        return itemPosition == firstVisiblePosition && (percentageAboveScreen >= 0.5f || percentageBelowScreen >= 0.5f)
    }
}




