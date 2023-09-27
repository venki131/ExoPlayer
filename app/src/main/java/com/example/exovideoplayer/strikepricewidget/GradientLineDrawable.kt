package com.example.exovideoplayer.strikepricewidget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import kotlin.math.abs
class GradientLineDrawable(
    private val context: Context,
    private val itemCount: Int, // Total number of items in your RecyclerView
    private val lineHeight: Float, // Height of each line item
    private val lineLength: Float // Length of each line
) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Adjust these properties based on your design
    private val hammerWidth = 80f // Width of the hammer
    private val hammerHeight = 100f // Height of the hammer
    private val hammerMargin = 32f // Margin between the hammer and the line
    private val hammerColor = Color.RED // Color of the hammer

    override fun draw(canvas: Canvas) {
        val centerX = bounds.exactCenterX()
        val centerY = bounds.exactCenterY()

        val halfItemCount = itemCount / 2
        val lineSpacing = lineLength / halfItemCount
        var startX = centerX - lineLength / 2

        for (position in 0 until itemCount) {
            // Calculate the alpha factor for the gradient
            val alphaFactor = calculateAlphaFactor(position, halfItemCount)

            // Set the alpha value based on the alpha factor
            paint.alpha = (255 * alphaFactor).toInt()

            // Draw a line
            canvas.drawLine(startX, centerY, startX + lineLength, centerY, paint)

            // Draw a hammer rectangle on top of the line
            val hammerTop = centerY - hammerHeight / 2
            val hammerBottom = centerY + hammerHeight / 2
            val hammerLeft = startX + (lineLength - hammerWidth) / 2
            val hammerRight = startX + (lineLength + hammerWidth) / 2
            paint.color = hammerColor
            canvas.drawRect(hammerLeft, hammerTop - hammerMargin, hammerRight, hammerBottom - hammerMargin, paint)

            startX += lineSpacing
        }
    }

    // Calculate alpha factor based on the position and the total number of items
    private fun calculateAlphaFactor(position: Int, halfItemCount: Int): Float {
        val distanceFromCenter = abs(position - halfItemCount)
        return 1.0f - (distanceFromCenter / halfItemCount.toFloat())
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}




