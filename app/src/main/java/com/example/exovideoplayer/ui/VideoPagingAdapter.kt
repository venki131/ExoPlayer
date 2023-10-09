package com.example.exovideoplayer.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.exovideoplayer.R

class VideoPagingAdapter(private val videoPlayer: VideoPlayer) :
    PagingDataAdapter<String, VideoViewHolder>(StringDiffCallback()) {

    private var currentPlaybackPos: Long = 0L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        if (position == 3) {
            holder.showImage()
        }
        holder.bind(getItem(position), currentPlaybackPos, videoPlayer = videoPlayer)
        holder.bookmark.setOnClickListener {
            currentPlaybackPos = holder.getPlaybackPos(videoPlayer)
            notifyItemChanged(position)
        }
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

    override fun onViewRecycled(holder: VideoViewHolder) {
        holder.releasePlayer()
        Log.d("VideoPlayerAdapter", "player released for ${holder.adapterPosition}")
    }
}
