package com.example.exovideoplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class VideoRvAdapter(
    private val list: MutableList<String> = mutableListOf(),
) : RecyclerView.Adapter<VideoViewHolder>() {

    private var currentPlaybackPos: Long = 0L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(list[position], currentPlaybackPos)
        holder.bookmark.setOnClickListener {
            currentPlaybackPos = holder.getPlaybackPos()
            notifyItemChanged(position)
        }
    }
}

