package com.example.exovideoplayer

import android.view.MotionEvent
import android.view.View
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener

class CustomChartGestureListener(private val markerView: CustomMarkerView) :
    OnChartGestureListener {
    override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
        // Display the marker dot when the chart gesture starts
    }

    override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
        // Handle any actions when the chart gesture ends
    }

    override fun onChartLongPressed(me: MotionEvent?) {

    }

    override fun onChartDoubleTapped(me: MotionEvent?) {

    }

    override fun onChartSingleTapped(me: MotionEvent?) {

    }

    override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {
        // Handle chart fling gestures
    }

    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {

    }

    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {

    }

}