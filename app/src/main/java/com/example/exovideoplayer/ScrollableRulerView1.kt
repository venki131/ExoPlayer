package com.example.exovideoplayer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
/*import com.assetgro.stockgro.R
import com.assetgro.stockgro.utils.common.topx
import com.assetgro.stockgro.utils.log.Logger*/
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class ScrollableRulerView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var cachedBitmap: Bitmap? = null
    private var cacheValid = false

    companion object {
        const val TAG = "ScrollableRulerView"
    }

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paintSolid = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paintText = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paintLegend = Paint(Paint.ANTI_ALIAS_FLAG)

    private var rulerViewPressed: Boolean = false

    private var density: Float = context.resources.displayMetrics.density
    private var padding = 4 * density

    private var path = Path()
    private val rectangle = RectF()
    private val rectangle2 = RectF()
    private var rectangleWidth = 80 * density
    private var rectangleHeight = 22 * density
    private var lesserRectangleHeight = 11 * density
    private val rectangleRadius = 4 * density
    private var topGap = 32 * density
    private var gapBetweenViews = 12 * density
    private var lineHeight = 32 * density
    private var gapBetweenLines = 8 * density
    private val textPosition = PointF()

    private var debouncePeriod: Long = 500
    private var searchJob: Job? = null
    lateinit var coroutineScope: CoroutineScope
    private var isShadowEnabled = false

    var scrollableRulerFormatter: ScrollableRulerFormatter? = null


    var rulerStartValue = 0
        set(value) {
            field = value
        }

    var rulerEndValue = 100
        set(value) {
            field = value
            rulerLength =
                ((rulerEndValue - rulerStartValue) / rulerIncrementValue) * gapBetweenLines
            rulerValue = rulerStartValue + (rulerEndValue - rulerStartValue) / 2
            invalidateCache()
        }

    var rulerIncrementValue = 1
        set(value) {
            field = value
        }
    var rulerValue = rulerStartValue + (rulerEndValue - rulerStartValue) / 2


    val screenWidth = context.resources.displayMetrics.widthPixels
    var middleOfScreen = (screenWidth - 32) / 2.0f

    var initialXValue = 0F
    private var lastTouchX = 0f

    fun moveToIndex(index: Int) {
        val rulerIndexForComputing =
            if (index > rulerEndValue) rulerEndValue else if (index < rulerStartValue) rulerStartValue else index
        val computedRulerValue = rulerIndexForComputing * rulerIncrementValue
        val offsetX = middleOfScreen - (computedRulerValue * gapBetweenLines)

        initialXValue = offsetX
        lastTouchX = offsetX

        rulerValue = computedRulerValue


        listener?.let { callback ->
            searchJob?.cancel()
            searchJob = coroutineScope.launch {
                rulerValue.let {
                    delay(debouncePeriod)
                    /*Logger.d(
                        TAG,
                        "Value emitted after scroll: ${
                            scrollableRulerFormatter?.getMarkerValue(rulerValue)
                        }"
                    )*/
                    callback.onRulerValueChanged(rulerValue)
                }
            }
        }

        invalidateCache()
    }

    fun enableShadow(enable: Boolean = false) {
        isShadowEnabled = enable
        invalidate() // Invalidate the view to trigger a redraw with the updated shadow state
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Calculate the new rectangle dimensions based on the content and view's size
        val textSize = paintText.textSize // Get the text size used for the pointer text
        rectangleWidth = textSize * 3f + padding * 7.5f // Adjust as needed
        rectangleHeight = textSize * 0.7f + padding * 2 // Adjust as needed

        // Recalculate any positions or values that depend on the view size
        middleOfScreen = width / 2.0f
        val textX = middleOfScreen
        val textY =
            (rectangleHeight / 2f) + (textSizePointerText / 2f) - (paintText.descent() / 2f)
        if (rulerValue > rulerEndValue) {
            rulerValue = rulerEndValue
        }

        textPosition.set(textX, textY)
        // Invalidate the cache to trigger a redraw with the updated dimensions
        invalidateCache()
    }


    private var rulerLength =
        ((rulerEndValue - rulerStartValue) / rulerIncrementValue) * gapBetweenLines
    private var legendTextSize =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10f,
            context.resources.displayMetrics
        )
    private var textSizePointerText =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            14f,
            context.resources.displayMetrics
        )

    private var listener: OnRulerValueChangeListener? = null


    init {
        paint.apply {
            color = ContextCompat.getColor(context, R.color.purple_text)
            strokeWidth = 2f * density
            textAlign = Paint.Align.CENTER
            textSize = textSize
        }


        paintSolid.apply {
            color = ContextCompat.getColor(context, R.color.purple_text)
            strokeWidth = 2f * density
            textAlign = Paint.Align.CENTER
            textSize = textSize
        }


        paintText.apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = textSizePointerText
        }

        paintLegend.apply {
            color = Color.GRAY
            textAlign = Paint.Align.CENTER
            textSize = legendTextSize
        }
    }

    fun setOnRulerValueChangeListener(listener: OnRulerValueChangeListener) {
        this.listener = listener
    }

    override fun onDraw(canvas: Canvas) {
        if (!cacheValid) {
            // Create the bitmap if it's null or doesn't match the view dimensions
            if (cachedBitmap == null || cachedBitmap?.width != width || cachedBitmap?.height != height) {
                cachedBitmap?.recycle()
                cachedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            }

            // Draw content onto the cachedBitmap
            cachedBitmap?.let { bitmap ->
                val bitmapCanvas = Canvas(bitmap)
                drawContent(bitmapCanvas)
                cacheValid = true
            }
        }

        // Draw cachedBitmap onto the view's canvas
        cachedBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, 0f, 0f, null)
        }
    }

    private fun invalidateCache() {
        cachedBitmap?.recycle()
        cachedBitmap = null
        cacheValid = false
        invalidate()
    }

    private fun drawContent(canvas: Canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        var startX = initialXValue
        var count = rulerStartValue

        val layoutParams = layoutParams as? ViewGroup.MarginLayoutParams
        val leftMargin = 0//layoutParams?.leftMargin ?: 16
        val rightMargin = 0//layoutParams?.rightMargin ?: 16

        canvas.apply {

            if (isShadowEnabled) {
                displayShadow(rulerViewPressed)
            } else {
                displayShadow(false)
            }

            val centerX = (width - leftMargin - rightMargin) / 2.0f // Center of the canvas
            // Calculate the left and right positions of rectangles
            val leftRectX = centerX - (rectangleWidth / 2f) - leftMargin
            val rightRectX = centerX + (rectangleWidth / 2f) + rightMargin

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

            /**
             * disabling shadow for the cubic figure in-order to avoid the black patch on the rectangle
             */
            displayShadow() // Disable shadow
            /*path.moveTo(centerX - rectangleWidth / 6f, rectangleHeight)
            path.cubicTo(
                centerX - rectangleWidth / 6f, lesserRectangleHeight,
                centerX, lesserRectangleHeight,
                centerX, (lesserRectangleHeight + lineHeight) / 2f
            )
            path.moveTo(centerX + rectangleWidth / 6f, rectangleHeight)
            path.cubicTo(
                centerX + rectangleWidth / 6f, lesserRectangleHeight,
                centerX, lesserRectangleHeight,
                centerX, (lesserRectangleHeight + lineHeight) / 2f
            )*/
            //drawPath(path, paint)
            if (isShadowEnabled) {
                displayShadow(true) // Re-enable shadow
            }
            path.reset()

            drawText(
                "₹ ${scrollableRulerFormatter?.getMarkerValue(rulerValue) ?: rulerValue.toString()}",
                textPosition.x,
                textPosition.y,
                paintText
            )

            val distance = rulerEndValue - rulerStartValue
            val xAxisValueMarkerModulo = distance / 6

            while (count <= rulerEndValue) {
                val distanceFromCenter = abs(centerX - startX)
                val alphaFactor = (255 * (1 - distanceFromCenter / centerX)).toInt()

                paintSolid.alpha = alphaFactor

                if (count % xAxisValueMarkerModulo == 0) {
                    val text =
                        scrollableRulerFormatter?.getRulerValue(count) ?: count.toString()


                    drawText(
                        text,
                        startX,
                        topGap + lineHeight + padding + legendTextSize,
                        paintLegend
                    )
                }

                drawLine(startX, topGap, startX, topGap + lineHeight, paintSolid)
                startX += gapBetweenLines
                count += rulerIncrementValue
            }
        }
    }

    fun displayShadow(show: Boolean = false) {
        if (show) paint.setShadowLayer(padding, 0f, padding, Color.DKGRAY)
        else paint.clearShadowLayer()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                rulerViewPressed = true
                parent?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP -> {
                lastTouchX = event.x
                rulerViewPressed = false
                parent?.requestDisallowInterceptTouchEvent(false)
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastTouchX
                initialXValue += dx
                if (initialXValue > middleOfScreen) initialXValue = middleOfScreen
                if (initialXValue < 0) {

                    if (initialXValue.absoluteValue + middleOfScreen > rulerLength) {
                        initialXValue = -(rulerLength - middleOfScreen)
                    }


                    rulerValue =
                        rulerStartValue + (((initialXValue.absoluteValue + middleOfScreen) / gapBetweenLines) * rulerIncrementValue).toInt()

                    if (rulerValue > rulerEndValue) {
                        rulerValue = rulerEndValue
                    }

                } else {
                    rulerValue =
                        rulerStartValue + (((middleOfScreen - initialXValue) / gapBetweenLines) * rulerIncrementValue).toInt()

                    if (rulerValue > rulerEndValue) {
                        rulerValue = rulerEndValue
                    }
                }


                listener?.let { callback ->
                    searchJob?.cancel()
                    searchJob = coroutineScope.launch {
                        rulerValue.let {
                            delay(debouncePeriod)
                            /*Logger.d(
                                TAG,
                                "Value emitted after scroll: ${
                                    scrollableRulerFormatter?.getMarkerValue(rulerValue)
                                }"
                            )*/
                            callback.onRulerValueChanged(rulerValue)
                        }
                    }
                }

                invalidateCache()
                lastTouchX = event.x
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = (topGap + lineHeight + padding + legendTextSize + (padding * 3)).roundToInt()
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, height)
    }


}

/*
interface OnRulerValueChangeListener {
    fun onRulerValueChanged(rulerValue: Int)
}

interface ScrollableRulerFormatter {

    fun getRulerValue(index: Int): String
    fun getMarkerValue(index: Int): String

}*/
