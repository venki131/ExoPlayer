package com.example.exovideoplayer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class ScrollableRulerRecyclerView(context: Context, attrs: AttributeSet?): RecyclerView(context, attrs) {

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
            //invalidateCache()
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

    fun enableShadow(enable: Boolean = false) {
        isShadowEnabled = enable
        invalidate() // Invalidate the view to trigger a redraw with the updated shadow state
    }
}