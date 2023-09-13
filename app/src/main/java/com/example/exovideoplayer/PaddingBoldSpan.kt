package com.example.exovideoplayer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.style.ReplacementSpan

class PaddingBoldSpan(
    private val padding: Float, // Padding in pixels
    private val textSize: Float // Text size in pixels
) : ReplacementSpan() {

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val width = paint.measureText(text, start, end)
        return (width + 2 * padding).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = textSize

        val width = paint.measureText(text, start, end)
        val xOffset = x + padding // Adjust the x-coordinate for padding
        val baseline = (y + bottom - paint.descent() - top) / 2
        canvas.drawText(text, start, end, xOffset, baseline.toFloat(), paint)
    }
}
