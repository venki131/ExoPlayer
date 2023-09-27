package com.example.exovideoplayer.strikepricewidget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.exovideoplayer.R
import kotlin.math.abs

class RulerAdapter(
    private val context: Context,
    private val lineHeight: Float,
    private val lineLength: Float
) : RecyclerView.Adapter<RulerAdapter.ViewHolder>() {

    private var scrollListener: RulerScrollListener? = null
    private var currentScrollPosition: Int = 0 // Initialize with the default position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.ruler_line_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data and set marker text based on position
        val markerText = calculateMarkerText(position)

        // Calculate alpha based on position
        val alphaFactor = calculateAlpha(position)
        holder.line.alpha = alphaFactor / 255f

        // Set the custom Drawable as the background for your line view
        val gradientLineDrawable = GradientLineDrawable(context, itemCount, lineHeight = lineHeight, lineLength = lineLength)
        holder.line.background = gradientLineDrawable

        holder.line.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_text))

        // Calculate the scroll position based on your logic
        val calculatedPosition = calculateScrollPosition(position)

        // Update the scroll position
        updateScrollPosition(calculatedPosition)
    }

    // Update the current scroll position and notify the scroll listener
    private fun updateScrollPosition(position: Int) {
        if (currentScrollPosition != position) {
            currentScrollPosition = position
            scrollListener?.onRulerScrolled(currentScrollPosition)
        }
    }

    // Calculate the scroll position based on the adapter position
    private fun calculateScrollPosition(adapterPosition: Int): Int {
        // Your logic to calculate the scroll position goes here
        // For example, you can use the adapter position or other data
        return adapterPosition
    }

    override fun getItemCount(): Int {
        // Return the total number of lines in your ruler
        return calculateTotalLines()
    }

    // Define your ViewHolder class here
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val line: View = itemView.findViewById(R.id.line)
    }

    // Implement your logic to calculate marker text, highlighted position, and total lines
    private fun calculateMarkerText(position: Int): String {
        // Calculate and return the marker text for the given position
        return "20"
    }

    private fun calculateHighlightedPosition(): Int {
        // Calculate and return the position of the highlighted line
        return 20
    }

    private fun calculateTotalLines(): Int {
        // Calculate and return the total number of lines in your ruler
        return 56
    }

    // Add a method to calculate alpha based on position for the gradient effect
    private fun calculateAlpha(position: Int): Int {
        // Calculate and return the alpha value based on the position for the gradient effect
        val centerX = (itemCount - 1) / 2f
        val distanceFromCenter = abs(position - centerX)
        val maxAlpha = 255

        // Adjust the alpha calculation to create the gradient effect
        val alphaFactor = maxAlpha - (distanceFromCenter / centerX * maxAlpha).toInt()

        return alphaFactor.coerceIn(0, 255) // Ensure alpha is within the valid range
    }
    fun setRulerScrollListener(listener: RulerScrollListener?) {
        this.scrollListener = listener
    }

}

interface RulerScrollListener {
    fun onRulerScrolled(position: Int)
}

