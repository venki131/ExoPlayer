package com.example.exovideoplayer.strikepricewidget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.example.exovideoplayer.R
import com.example.exovideoplayer.topx
private var textValue: String = ""
private var hammerPosition: Float = 0f
class HammerView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paintText = Paint(Paint.ANTI_ALIAS_FLAG)
    private var density: Float = context.resources.displayMetrics.density
    private var lineHeight = 32 * density
    private var textSizePointerText =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            14f,
            context.resources.displayMetrics
        )
    private val rectangle = RectF()
    private val rectangle2 = RectF()
    private var rectangleWidth = 80 * density
    private var rectangleHeight = 22 * density
    private val rectangleRadius = 4 * density
    private var path = Path()
    private var topGap = 32 * density
    private var gapBetweenViews = 12 * density
    private var textBoxWidthPadding = 10.5f * density
    private var textBoxHeightPadding = 8 * density
    private val textPosition = PointF()


    init {
        paint.apply {
            color = ContextCompat.getColor(context, R.color.purple_text)
            strokeWidth = 2f * density
            textAlign = Paint.Align.CENTER
        }

        paintText.apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = textSizePointerText
        }
    }

    fun setHammerPosition(position: Float) {
        // Update the position of the hammer
        hammerPosition = position
    }

    fun setTextValue(value: String) {
        // Update the text value displayed on the hammer
        textValue = value
    }

    override fun onDraw(canvas: Canvas) {


        val middleOfScreen = width / 2.0f
        val textX = middleOfScreen
        val textY =
            (rectangleHeight / 2f) + (textSizePointerText / 2f) - (paintText.descent() / 2f)

        textPosition.set(textX, textY)

        // Calculate the text length dynamically based on the content
        val textLength = paintText.measureText(
            textValue
        )

        val textSize = paintText.textSize
        val rectangleWidth = textLength + textBoxWidthPadding * 2 // Adjust padding as needed
        val rectangleHeight = textSize * 0.5f + textBoxHeightPadding * 2 // Adjust padding as needed

        canvas.apply {
            val centerX = width / 2.0f // Center of the canvas
            // Calculate the left and right positions of rectangles
            val leftRectX = centerX - (rectangleWidth / 2f)
            val rightRectX = centerX + (rectangleWidth / 2f)

            // Adjust the hammer position based on its width
            val hammerX = hammerPosition - (rectangleWidth / 2f)

            rectangle.set(
                leftRectX,
                0f,
                rightRectX,
                rectangleHeight
            )
            path.addRoundRect(rectangle, rectangleRadius, rectangleRadius, Path.Direction.CW)

            path.moveTo(centerX - (2.topx / 2f), rectangleHeight)

            val leftRect2X = centerX - 2.topx
            val rightRect2X = centerX + 2.topx

            rectangle2.set(
                leftRect2X,
                gapBetweenViews,
                rightRect2X,
                topGap + lineHeight
            )
            path.addRoundRect(rectangle2, rectangleRadius, rectangleRadius, Path.Direction.CW)
            drawPath(path, paint)

            drawText(
                "₹ $textValue",
                textPosition.x,
                textPosition.y,
                paintText
            )
        }
        invalidate() // Request a redraw
    }
}
