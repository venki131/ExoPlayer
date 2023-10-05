package com.example.exovideoplayer.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

class ConstraintLayoutWithDisableSupport @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val ignoredViews = mutableSetOf<View>()
    var disabled = false
        set(value) {
            field = value
            requestLayout()
        }

    fun addIgnoredView(view: View) {
        ignoredViews.add(view)
    }

    fun removeIgnoredView(view: View) {
        ignoredViews.remove(view)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (disabled) {
            // Draw non-ignored views with the grayscale filter
            val saveCount = canvas?.saveLayer(null, getGrayScalePaint())

            super.dispatchDraw(canvas)

            saveCount?.let { canvas.restoreToCount(it) }

            // Draw ignored views without the grayscale filter on a separate canvas
            ignoredViews.forEach { ignoredView ->
                val saveCountIgnored = canvas?.save()
                canvas?.translate(ignoredView.left.toFloat(), ignoredView.top.toFloat())
                ignoredView.draw(canvas)
                saveCountIgnored?.let { canvas.restoreToCount(it) }
            }


        } else {
            super.dispatchDraw(canvas)
        }
    }

    override fun isEnabled(): Boolean {
        return super.isEnabled() && !disabled
    }

    private fun getGrayScalePaint(): Paint {
        val grayscalePaint = Paint()
        val cm = ColorMatrix()
        cm.set(
            floatArrayOf(
                GREY_SCALE_NUMBER, GREY_SCALE_NUMBER, GREY_SCALE_NUMBER, 0f, 0f,
                GREY_SCALE_NUMBER, GREY_SCALE_NUMBER, GREY_SCALE_NUMBER, 0f, 0f,
                GREY_SCALE_NUMBER, GREY_SCALE_NUMBER, GREY_SCALE_NUMBER, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        grayscalePaint.colorFilter = ColorMatrixColorFilter(cm)
        return grayscalePaint
    }

    private companion object {
        private const val GREY_SCALE_NUMBER = 0.33f
    }
}






