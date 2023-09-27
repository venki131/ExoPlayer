package com.example.exovideoplayer.strikepricewidget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

class RulerItemDecoration(
    private val context: Context,
    private val gapBetweenLines: Float,
    private val lineHeight: Float,
    private val topGap: Float
) : RecyclerView.ItemDecoration() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = Color.GRAY // Set the color of the lines
        paint.strokeWidth = 2f // Set the line width
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val startX = parent.paddingLeft.toFloat()
        val endX = parent.width.toFloat()

        // Calculate the Y positions for the lines
        val startY = topGap
        val endY = startY + lineHeight

        // Calculate the number of lines needed
        val itemCount = parent.adapter?.itemCount ?: 0
        val lineCount = itemCount - 1

        // Draw the lines
        for (i in 0 until lineCount) {
            val x = startX + i * gapBetweenLines
            c.drawLine(x, startY, x, endY, paint)
        }
    }
}
