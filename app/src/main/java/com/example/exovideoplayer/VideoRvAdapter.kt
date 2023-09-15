package com.example.exovideoplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class VideoRvAdapter(
    private val list: MutableList<String> = mutableListOf(),
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<VideoViewHolder>() {

    private val snapHelper = PagerSnapHelper()
    init {
        snapHelper.attachToRecyclerView(null)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        snapHelper.attachToRecyclerView(recyclerView)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        // Check if the view is newly attached or being recycled
        if (holder.itemView.isAttachedToWindow) {
            holder.onViewAttachedToWindow(lifecycleOwner)
        }
        holder.itemView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                holder.onViewAttachedToWindow(lifecycleOwner)
            }

            override fun onViewDetachedFromWindow(v: View) {
                holder.onViewDetachedFromWindow(lifecycleOwner)
            }
        })
        holder.bind(list[position])
    }
}

