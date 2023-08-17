package com.example.exovideoplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class VideoRvAdapter(
    private val list: MutableList<String> = mutableListOf(),
) : RecyclerView.Adapter<VideoViewHolder>() {

    private val snapHelper = PagerSnapHelper()

    init {
        snapHelper.attachToRecyclerView(null)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        snapHelper.attachToRecyclerView(recyclerView)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(list[position])
        holder.onViewAttachedToWindow()
        holder.itemView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                holder.onViewAttachedToWindow()
            }

            override fun onViewDetachedFromWindow(v: View) {
                holder.onViewDetachedFromWindow()
            }
        })
    }
}

