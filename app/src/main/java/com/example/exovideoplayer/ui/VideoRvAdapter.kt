package com.example.exovideoplayer.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.exovideoplayer.R

var isGlobalMuted = false
class VideoRvAdapter(
    private val context: Context,
    private val list: MutableList<String?> = mutableListOf(),
) : RecyclerView.Adapter<VideoViewHolder>() {

    private var currentPlaybackPos: Long = 0L
    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        if (holder.adapterPosition == 3) {
            holder.showImage()
        } else {
            holder.hideImage()
        }
        /*holder.bind(list[position], currentPlaybackPos)
        holder.bookmark.setOnClickListener {
            currentPlaybackPos = holder.getPlaybackPos(videoPlayer)
            notifyItemChanged(position)
        }*/
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        holder.releasePlayer()
        Toast.makeText(context, "recycled for ${holder.position}", Toast.LENGTH_SHORT).show()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }
}

