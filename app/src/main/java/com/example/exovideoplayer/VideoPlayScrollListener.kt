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

        // Get the first fully visible item position
        val firstCompletelyVisibleItemPosition =
            layoutManager.findFirstCompletelyVisibleItemPosition()

        // Attach the player to the first item and partially visible items
        recyclerView.adapter?.let { rvAdapter ->
            for (i in 0 until rvAdapter.itemCount) {
                val viewHolder =
                    recyclerView.findViewHolderForAdapterPosition(i) as? VideoViewHolder
                val attachPlayer =
                    i == firstCompletelyVisibleItemPosition || isPartiallyVisible(viewHolder)
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

    private fun isPartiallyVisible(viewHolder: RecyclerView.ViewHolder?): Boolean {
        viewHolder ?: return false

        val view = viewHolder.itemView
        val itemTop = view.top
        val itemBottom = view.bottom
        val itemHeight = view.height
        val percentageVisible = (itemHeight - (itemBottom - itemTop)) / itemHeight.toFloat()

        return percentageVisible > 0.6f // Adjust the threshold as needed
    }
}




