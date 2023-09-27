package com.example.exovideoplayer.strikepricewidget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class CustomRulerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs), RulerScrollListener {

    private var recyclerView: RecyclerView
    private var density: Float = context.resources.displayMetrics.density
    private var lineHeight = 32 * density
    private var gapBetweenLines = 8 * density
    private val rulerAdapter: RulerAdapter
    private var hammerView: HammerView
    private val TAG = CustomRulerView::class.java.simpleName

    init {
        // Initialize the RecyclerView and set the adapter
        recyclerView = RecyclerView(context)
        recyclerView.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )

        // Set the layoutManager for your RecyclerView (e.g., LinearLayoutManager)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Initialize your RulerAdapter and set it as the adapter for the RecyclerView
        rulerAdapter = RulerAdapter(context, lineHeight, gapBetweenLines)
        recyclerView.adapter = rulerAdapter
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.isHorizontalScrollBarEnabled = false
        recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        rulerAdapter.setRulerScrollListener(this)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        // Add the RecyclerView to your CustomRulerView
        addView(recyclerView)

        // Initialize and add the HammerView (adjust layout params as needed)
        hammerView = HammerView(context, attrs)
        val hammerLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(hammerView, hammerLayoutParams)

        val snapPositionListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                // Calculate the snapped position based on your logic
                val snappedPosition = calculateSnappedPosition(layoutManager, recyclerView)

                // Update HammerView based on the snapped position
                hammerView.setHammerPosition(snappedPosition.toFloat())
            }
        }

        recyclerView.addOnScrollListener(snapPositionListener)
    }

    private fun calculateSnappedPosition(
        layoutManager: LinearLayoutManager,
        recyclerView: RecyclerView
    ): Int {
        val recyclerViewCenterX = recyclerView.width / 2
        val visibleItemCount = layoutManager.childCount

        var closestLinePosition = -1
        var closestDistance = Int.MAX_VALUE

        for (i in 0 until visibleItemCount) {
            val child = layoutManager.getChildAt(i)!!
            val childCenterX = (layoutManager.getDecoratedLeft(child) + layoutManager.getDecoratedRight(child)) / 2

            val distance = Math.abs(recyclerViewCenterX - childCenterX)
            if (distance < closestDistance) {
                closestDistance = distance
                closestLinePosition = layoutManager.getPosition(child)
            }
        }

        return closestLinePosition
    }


    override fun onRulerScrolled(position: Int) {
        Log.d(TAG, "position = $position")
        hammerView.setTextValue("$position")
        hammerView.setHammerPosition(20f)
    }
}
