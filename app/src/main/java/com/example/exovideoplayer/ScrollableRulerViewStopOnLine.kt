package com.example.exovideoplayer

/*import com.assetgro.stockgro.R
import com.assetgro.stockgro.utils.common.topx
import com.assetgro.stockgro.utils.log.Logger*/
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.OverScroller
import android.widget.Scroller
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class ScrollableRulerViewStopOnLine(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var cachedBitmap: Bitmap? = null
    private var cacheValid = false
    private var isContentVisible = false

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
    private var textBoxWidthPadding = 10.5f * density
    private var textBoxHeightPadding = 8 * density

    private var path = Path()
    private val rectangle = RectF()
    private val rectangle2 = RectF()
    private var rectangleWidth = 80 * density
    private var rectangleHeight = 22 * density
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

    private var lastScrollX: Float = 0f
    private var snappedPosition: Float = 0f
    private val snapDuration: Int = 300 // Adjust the snap duration as needed


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

    /*fun moveToIndex(index: Int) {
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
                    callback.onRulerValueChanged(rulerValue)
                }
            }
        }
        isContentVisible = true
        invalidateCache()
    }*/

    fun moveToIndex(index: Int, dx:Int = 0) {
        val rulerIndexForComputing =
            if (index > rulerEndValue) rulerEndValue else if (index < rulerStartValue) rulerStartValue else index
        val computedRulerValue = rulerIndexForComputing * rulerIncrementValue
        val offsetX = middleOfScreen - (computedRulerValue * gapBetweenLines)
        val animator = ObjectAnimator.ofFloat(this, "initialXValue", initialXValue, offsetX)
        animator.duration = 300
        Log.d(TAG, "dx value = $dx")
        animator.interpolator = if (dx < 0) LinearInterpolator() else null
        animator.start()

        animator.addUpdateListener { animation ->
            val currentValue = animation.animatedValue as Float
            initialXValue = currentValue
            lastTouchX = currentValue
            rulerValue = computedRulerValue

            listener?.let { callback ->
                searchJob?.cancel()
                searchJob = coroutineScope.launch {
                    rulerValue.let {
                        delay(debouncePeriod)
                        callback.onRulerValueChanged(rulerValue)
                    }
                }
            }
            isContentVisible = true
            invalidateCache()
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                // Ensure that the final position is set
                initialXValue = offsetX
                lastTouchX = offsetX
            }

            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
            }
        })

        animator.start()
    }

    /*fun moveToIndex(index: Int, direction: ScrollDirection? = null) {
        val rulerIndexForComputing =
            if (index > rulerEndValue) rulerEndValue else if (index < rulerStartValue) rulerStartValue else index
        val computedRulerValue = rulerIndexForComputing * rulerIncrementValue

        // Calculate offsetX based on the scrolling direction
        val offsetX = when (direction) {
            ScrollDirection.LEFT -> middleOfScreen - (computedRulerValue * gapBetweenLines)
            ScrollDirection.RIGHT -> middleOfScreen + (computedRulerValue * gapBetweenLines)
            else -> middleOfScreen - (computedRulerValue * gapBetweenLines)
        }

        // Adjust the duration based on the absolute value of dx to make it smooth
        //val duration = Math.abs(dx) * 2 // You can experiment with different values

        val animator = ObjectAnimator.ofFloat(this, "initialXValue", initialXValue, offsetX)
        animator.duration = 300//duration.toLong()
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { animation ->
            val currentValue = animation.animatedValue as Float
            initialXValue = currentValue
            lastTouchX = currentValue
            rulerValue = computedRulerValue

            listener?.let { callback ->
                searchJob?.cancel()
                searchJob = coroutineScope.launch {
                    rulerValue.let {
                        delay(debouncePeriod)
                        callback.onRulerValueChanged(rulerValue)
                    }
                }
            }
            isContentVisible = true
            invalidateCache()
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                // Ensure that the final position is set
                initialXValue = offsetX
                lastTouchX = offsetX
            }
        })

        animator.start()
    }*/


    fun enableShadow(enable: Boolean = false) {
        isShadowEnabled = enable
        invalidate() // Invalidate the view to trigger a redraw with the updated shadow state
    }

    private val scroller = Scroller(context, LinearInterpolator())


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Calculate the new rectangle dimensions based on the content and view's size
        val textSize = paintText.textSize // Get the text size used for the pointer text
        rectangleWidth = textSize * 3f + padding * 7.5f // Adjust as needed
        rectangleHeight = textSize * 0.7f + padding * 2 // Adjust as needed
        rulerLength = ((rulerEndValue - rulerStartValue) / rulerIncrementValue) * gapBetweenLines

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

        // Calculate the text length dynamically based on the content
        val textLength = paintText.measureText(
            scrollableRulerFormatter?.getMarkerValue(rulerValue) ?: rulerValue.toString()
        )

        // Calculate the rectangle dimensions based on the text length and padding
        val textSize = paintText.textSize
        val rectangleWidth = textLength + textBoxWidthPadding * 2 // Adjust padding as needed
        val rectangleHeight = textSize * 0.5f + textBoxHeightPadding * 2 // Adjust padding as needed

        // Recalculate any positions or values that depend on the view size
        middleOfScreen = width / 2.0f
        val textX = middleOfScreen
        val textY =
            (rectangleHeight / 2f) + (textSizePointerText / 2f) - (paintText.descent() / 2f)
        if (rulerValue > rulerEndValue) {
            rulerValue = rulerEndValue
        }

        textPosition.set(textX, textY)


        canvas.apply {

            if (isShadowEnabled) {
                displayShadow(rulerViewPressed)
            } else {
                displayShadow(false)
            }

            val centerX = (width) / 2.0f // Center of the canvas
            // Calculate the left and right positions of rectangles
            val leftRectX = centerX - (rectangleWidth / 2f)
            val rightRectX = centerX + (rectangleWidth / 2f)

            if (isContentVisible) {
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
            }

            /**
             * disabling shadow for the cubic figure in-order to avoid the black patch on the rectangle
             */
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

    private fun displayShadow(show: Boolean = false) {
        if (show) paint.setShadowLayer(padding, 0f, padding, Color.DKGRAY)
        else paint.clearShadowLayer()
    }

    var previousRulerValue = -1

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //scroller.forceFinished(true) // Stop any ongoing scroll animations
                lastTouchX = event.x
                rulerViewPressed = true
                isContentVisible = true
                lastScrollX = event.x
                parent?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                lastTouchX = event.x
                rulerViewPressed = false
                invalidateCache()
                val snapPosition = calculateSnappedPosition()
                // Animate to the snapped position
                //smoothScrollToPosition(rulerValue)
                //moveToIndex(rulerValue)
                parent?.requestDisallowInterceptTouchEvent(false)
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = (event.x - lastScrollX).toInt()
                Log.d(TAG, "dx = $dx eventX = ${event.x}")
                if (dx != 0) {
                    lastScrollX = event.x
                }

                initialXValue += dx

                // Ensure initialXValue is within the valid range
                if (initialXValue > middleOfScreen) initialXValue = middleOfScreen
                if (initialXValue < 0) {
                    if (initialXValue.absoluteValue + middleOfScreen > rulerLength) {
                        initialXValue = -(rulerLength - middleOfScreen)
                    }
                }

                // Calculate the rulerValue based on the current initialXValue
                val newRulerValue = calculateRulerValue(initialXValue)

                Log.d(TAG, "newRulerValue = $newRulerValue previousRulerValue = $previousRulerValue")

                // Check if the rulerValue has changed
                if (newRulerValue != previousRulerValue) {
                    rulerValue = newRulerValue

                    // Call moveToIndex only if the rulerValue has changed
                    moveToIndex(rulerValue, dx)

                    // Update the previousRulerValue
                    previousRulerValue = rulerValue
                }
            }
        }
        return true
    }

    private fun smoothScrollToPosition(targetX: Int) {
        val startX = scrollX
        val distance = targetX - startX
        scroller.startScroll(startX, 0, distance, 0, snapDuration)
        postInvalidate()
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, 0)
            postInvalidate()
        }
    }

    private fun calculateSnappedPosition(): Float {
        val lineSpacing = gapBetweenLines
        val currentScrollX = scrollX
        snappedPosition = (currentScrollX + lineSpacing / 2) / lineSpacing
        return snappedPosition
    }


    // Calculate rulerValue based on initialXValue
    private fun calculateRulerValue(initialXValue: Float): Int {
        return if (initialXValue < 0) {
            rulerStartValue + (((initialXValue.absoluteValue + middleOfScreen) / gapBetweenLines) * rulerIncrementValue).toInt()
        } else {
            rulerStartValue + (((middleOfScreen - initialXValue) / gapBetweenLines) * rulerIncrementValue).toInt()
        }
    }

    // Smooth scroll to the snapped position using Scroller
    private fun scrollToSnappedPosition() {
        val snappedInitialXValue = calculateInitialXValue(rulerValue)
        val dx = snappedInitialXValue - initialXValue
        if (initialXValue <= snappedInitialXValue) {
            // Start a smooth scroll animation
            scroller.startScroll(
                initialXValue.toInt(),
                0,
                dx.toInt(),
                0,
                (snappedInitialXValue - initialXValue).toInt(),
            )
            postInvalidateOnAnimation()
        }
    }

    // Smooth scroll to the snapped position using Scroller
    /*private fun scrollToSnappedPosition() {
        val snappedInitialXValue = calculateInitialXValue(rulerValue)
        val dx = (snappedInitialXValue - initialXValue).toInt()
        val snapDuration = 200 // Adjust this value for the snap animation duration (milliseconds)
        val snapThreshold = gapBetweenLines / 2 // 50% threshold for snapping

        // Determine whether to snap to the next line or the previous line
        val snapToNextLine = if (dx > 0) {
            dx > snapThreshold
        } else {
            dx < -snapThreshold
        }

        // Check if the distance between lines is more than 50% of a line's gap
        val isMoreThanHalfWay = abs(dx % gapBetweenLines) > snapThreshold

        // Adjust the finalDx based on snap direction
        val finalDx = if (snapToNextLine) {
            if (dx > 0) gapBetweenLines else -gapBetweenLines
        } else {
            if (dx > 0) -gapBetweenLines else 0
        }

        Log.d(TAG, "snap next line = $snapToNextLine dx = $dx morethan50 = $isMoreThanHalfWay")

        // Start a smooth scroll animation
        scroller.startScroll(
            initialXValue.toInt(),
            0,
            finalDx.toInt(),
            0,
            snapDuration
        )
        postInvalidateOnAnimation()
    }*/


    private fun calculateInitialXValue(rulerValue: Int): Float {
        return (middleOfScreen - (rulerValue - rulerStartValue) * gapBetweenLines).apply {
            "%.1f".format(this).toFloat()
        }
    }

    // Override the computeScroll method to handle scroll updates
    /*override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            // Update the initialXValue during the scroll animation
            initialXValue = scroller.currX.toFloat()
            postInvalidateOnAnimation()
        }
    }*/


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = (topGap + lineHeight + padding + legendTextSize + (padding * 3)).roundToInt()
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, height)
    }


}

/*interface OnRulerValueChangeListener {
    fun onRulerValueChanged(rulerValue: Int)
}

interface ScrollableRulerFormatter {

    fun getRulerValue(index: Int): String
    fun getMarkerValue(index: Int): String

}*/
