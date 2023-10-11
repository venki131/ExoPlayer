package com.example.exovideoplayer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.exovideoplayer.R

var isGlobalMuted = false
class VideoRvAdapter(
    private val list: MutableList<String?> = mutableListOf(),
) : RecyclerView.Adapter<VideoViewHolder>() {

    private var currentPlaybackPos: Long = 0L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount() = 50//list.size

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        if (position == 3) {
            holder.showImage()
        }
        holder.bind(list[position], currentPlaybackPos)
        holder.bookmark.setOnClickListener {
            currentPlaybackPos = holder.getPlaybackPos()
            notifyItemChanged(position)
        }
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        holder.releasePlayer()
    }
}

