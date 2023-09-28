package com.example.exovideoplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

class VideoPagingAdapter :
    PagingDataAdapter<String, VideoViewHolder>(StringDiffCallback()) {

    private var currentPlaybackPos: Long = 0L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = getItem(position)
        /*if (item == null) {
            holder.clear() // Clear the view if the item is null (used for placeholders).
        } else {*/
            holder.bind(item, currentPlaybackPos)
            holder.bookmark.setOnClickListener {
                currentPlaybackPos = holder.getPlaybackPos()
                notifyItemChanged(position)
            }
        //}
    }

    // Custom DiffUtil callback to compare items.
    class StringDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
