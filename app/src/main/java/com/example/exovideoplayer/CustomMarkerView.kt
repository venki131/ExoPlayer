package com.example.exovideoplayer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.StyleSpan
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.math.RoundingMode

class CustomMarkerView(
    context: Context,
    layoutResource: Int,
    private val inputData: BreakEvenChartInputData,
) : MarkerView(context, layoutResource) {

    private val TAG = CustomMarkerView::class.java.simpleName

    private val dotRadius = 12f // Adjust the dot size as needed
    private val dotPaint = Paint()
    private val textPaint = TextPaint()
    private var markerView: TextView? = null
    private var offset: MPPointF? = null

    private var horizontalMargin: Float

    init {
        dotPaint.isAntiAlias = true
        dotPaint.style = Paint.Style.FILL

        textPaint.color = Color.BLACK
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.isAntiAlias = true
        textPaint.textSize = 20f
        markerView = findViewById(R.id.marker_view)

        // Convert dp to pixels
        horizontalMargin = 20.dpToPx()
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        // Customize the content of the marker here
        if (e != null) {
            // Set the content of the marker view based on the data point (e)
            val positive = (chartView.data.dataSets[0] as LineDataSet).values.filter {
                it == e
            }
            val negative = (chartView.data.dataSets[1] as LineDataSet).values.filter {
                it == e
            }
            val maxLoss = (chartView.data.dataSets[3] as LineDataSet).values.filter {
                it == e
            }
            val breakEven = (chartView.data.dataSets[2] as LineDataSet).values.filter {
                it == e
            }

            val valueText = when {
                positive.isNotEmpty() -> (positive[0].data as BEChartData).itemValue.round(2)
                    .toString()

                negative.isNotEmpty() -> (negative[0].data as BEChartData).itemValue.round(2)
                    .toString()

                maxLoss.isNotEmpty() -> (maxLoss[0].data as BEChartData).itemValue.round(2)
                    .toString()

                breakEven.isNotEmpty() -> (breakEven[0].data as BEChartData).itemValue.round(2)
                    .toString()

                else -> "0"
            }

            Log.d(TAG, "plotted value text = $valueText")
            val percentageChange = (((valueText.toFloat().minus(inputData.parentStockPrice)).div(inputData.parentStockPrice)) * 100).round(2)

            // Create a SpannableStringBuilder to format the text
            val formattedValueText = SpannableStringBuilder(valueText)

            // Apply the bold style to the valueText
            val boldStyle = StyleSpan(Typeface.BOLD)

            formattedValueText.setSpan(
                boldStyle,
                0,
                valueText.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )

            markerView?.apply {
                val formattedText = SpannableStringBuilder(
                    context.getString(
                        R.string.formatted_marker_text,
                        inputData.optionName,
                        formattedValueText,
                        percentageChange.toString()
                    )
                )

                // Apply bold style to the formattedValueText
                val valueTextStart = formattedText.indexOf(formattedValueText.toString())
                if (valueTextStart != -1) {
                    val valueTextEnd = valueTextStart + formattedValueText.length
                    formattedText.setSpan(
                        StyleSpan(Typeface.BOLD),
                        valueTextStart,
                        valueTextEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                text = formattedText
                setTextColor(Color.BLACK)
                visibility = VISIBLE
            }

        } else {
            hideMarkerLine() // Hide the marker view if there is no entry
        }
        super.refreshContent(e, highlight)
    }

    private fun hideMarkerLine() {
        markerView?.visibility = GONE
    }


    override fun getOffset(): MPPointF {
        if (offset == null) {
            offset = MPPointF((-(width) / 2).toFloat(), (-height).toFloat())
        }
        return offset as MPPointF
    }

    override fun draw(canvas: Canvas?, posX: Float, posY: Float) {
        Log.d(TAG, "Draw method called")
        // Calculate the center position (centerY) of the chart
        val centerY = chartView.height / 2f
        Log.d(TAG, "X = $posX Y = $posY centre = $centerY")

        // Determine whether the marker is in the positive or negative direction
        val isPositive = posY.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toFloat() < centerY
        val isNegative = posY.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toFloat() > centerY

        // Set the dot color based on the direction
        dotPaint.color = when {
            isPositive -> ContextCompat.getColor(context, R.color.green)
            isNegative -> Color.RED
            else -> Color.BLACK // Use black for the center position
        }

        // Draw the circular dot at the touch position
        canvas?.drawCircle(posX, posY, dotRadius, dotPaint)

        val text = markerView?.text
        // Convert the SpannableStringBuilder to a CharSequence
        val formattedText = (markerView?.text as? SpannableStringBuilder)?.toString() ?: text

        val textWidth =
            textPaint.measureText(formattedText.toString()) // Calculate the width of the text

        // Calculate the available space on the screen (chart width)
        val screenWidth = chartView.width.toFloat()

        // Adjust the posX to keep the text within the screen bounds
        val adjustedPosX = if (posX + textWidth > screenWidth - horizontalMargin) {
            screenWidth - textWidth - horizontalMargin // Move the text back inside the screen
        } else {
            posX // Keep the original posX if it fits within the screen
        }

        val adjustedPosY = 0f // Adjust the Y position to be at the top of the chart

        canvas?.let {
            // Create a StaticLayout from the SpannableString
            val textLayout = StaticLayout(
                formattedText,
                textPaint,
                it.width,
                Layout.Alignment.ALIGN_NORMAL,
                1f,
                0f,
                false
            )
            it.translate(adjustedPosX, adjustedPosY)
            textLayout.draw(it)
        }
        invalidate()
    }
}

