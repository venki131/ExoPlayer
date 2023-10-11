package com.example.exovideoplayer

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.exovideoplayer.ThemeManager.isDarkModeEnabled
import com.example.exovideoplayer.ThemeManager.saveThemePreference
import com.example.exovideoplayer.databinding.ActivityMainBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil


class MainActivity : AppCompatActivity() {

    private var output: BreakEvenChartOutputData? = null
    private val TAG = MainActivity::class.java.simpleName
    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val list = mutableListOf<BreakEvenChartInputData>()

    val inputData = BreakEvenChartInputData(
        isSell = true,
        premium = 12.2,
        breakEven = 1012.2,
        lotSize = 700,
        optionType = OptionType.CALL,
        optionName = "ICICIBANK",
        parentStockPrice = 990.5
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Apply the initial theme
        if (isDarkModeEnabled(this)) {
            ThemeManager.applyDarkTheme(this)
            viewBinding.customSwitch.isChecked = true
        } else {
            ThemeManager.applyLightTheme(this)
            viewBinding.customSwitch.isChecked = false
        }

        setContentView(viewBinding.root)
        viewBinding.customSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveThemePreference(this, isChecked)

            // Recreate the activity to apply the new theme
            recreate()

        }
        addItems()
        showScrollWidget()
        output = computeBreakEvenChartOutputData(inputData)
        drawLineChart()
    }


    private fun showScrollWidget() {
        viewBinding.showScrollWidgetBtn.setOnClickListener {
            viewBinding.scrollableRulerSmoothScroller.visibility = View.VISIBLE
            viewBinding.smootText.visibility = View.VISIBLE

            viewBinding.scrollableRulerSnapper.visibility = View.GONE
            viewBinding.snapText.visibility = View.GONE

            viewBinding.scrollableRuler.visibility = View.GONE
            viewBinding.recentText.visibility = View.GONE

            viewBinding.scrollableRulerStopOnlineScroller.visibility = View.GONE
            viewBinding.stopOnlineText.visibility = View.GONE
            scrollViewSmoothScroller()
        }

        viewBinding.showScrollWidgetSnapperBtn.setOnClickListener {
            viewBinding.scrollableRulerSnapper.visibility = View.VISIBLE
            viewBinding.snapText.visibility = View.VISIBLE

            viewBinding.scrollableRuler.visibility = View.GONE
            viewBinding.recentText.visibility = View.GONE

            viewBinding.scrollableRulerSmoothScroller.visibility = View.GONE
            viewBinding.smootText.visibility = View.GONE

            viewBinding.scrollableRulerStopOnlineScroller.visibility = View.GONE
            viewBinding.stopOnlineText.visibility = View.GONE
            scrollViewSnapper()
        }

        viewBinding.showScrollWidgetRecentBtn.setOnClickListener {
            viewBinding.scrollableRuler.visibility = View.VISIBLE
            viewBinding.recentText.visibility = View.VISIBLE

            viewBinding.scrollableRulerSnapper.visibility = View.GONE
            viewBinding.snapText.visibility = View.GONE

            viewBinding.scrollableRulerSmoothScroller.visibility = View.GONE
            viewBinding.smootText.visibility = View.GONE

            viewBinding.scrollableRulerStopOnlineScroller.visibility = View.GONE
            viewBinding.stopOnlineText.visibility = View.GONE
            scrollView()
        }

        viewBinding.showScrollWidgetStopOnlineBtn.setOnClickListener {
            viewBinding.scrollableRuler.visibility = View.GONE
            viewBinding.recentText.visibility = View.GONE

            viewBinding.scrollableRulerSnapper.visibility = View.GONE
            viewBinding.snapText.visibility = View.GONE

            viewBinding.scrollableRulerSmoothScroller.visibility = View.GONE
            viewBinding.smootText.visibility = View.GONE

            viewBinding.scrollableRulerStopOnlineScroller.visibility = View.VISIBLE
            viewBinding.stopOnlineText.visibility = View.VISIBLE

            scrollViewStopOnLineScroller()
        }
    }

    private fun addItems() {
        list.add(
            BreakEvenChartInputData(
                isSell = false,
                premium = 9.75,
                breakEven = 970.25,
                lotSize = 700,
                optionType = OptionType.PUT,
                optionName = "ICICIBANK",
                parentStockPrice = 990.5
            )
        )
        list.add(
            BreakEvenChartInputData(
                isSell = true,
                premium = 12.2,
                breakEven = 1012.2,
                lotSize = 700,
                optionType = OptionType.CALL,
                optionName = "ICICIBANK",
                parentStockPrice = 990.5
            )
        )
        list.add(
            BreakEvenChartInputData(
                isSell = false,
                premium = 94.0,
                breakEven = 20094.0,
                lotSize = 50,
                optionType = OptionType.CALL,
                optionName = "Nifty 50",
                parentStockPrice = 20070.0
            )
        )

        list.add(
            BreakEvenChartInputData(
                isSell = false,
                premium = 2.95,
                breakEven = 1603.0,
                lotSize = 400,
                optionType = OptionType.CALL,
                optionName = "Infosys",
                parentStockPrice = 1498.35
            )
        )
    }

    private fun scrollView() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val scrollRuler = viewBinding.scrollableRuler
                scrollRuler.rulerStartValue = 0
                scrollRuler.rulerEndValue = 100
                scrollRuler.moveToIndex(20)
                scrollRuler.enableShadow(false)
            }
        }
    }

    private fun scrollViewSnapper() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val scrollRuler = viewBinding.scrollableRulerSnapper
                scrollRuler.rulerStartValue = 0
                scrollRuler.rulerEndValue = 100
                scrollRuler.moveToIndex(20)
                scrollRuler.enableShadow(false)
            }
        }
    }

    private fun scrollViewSmoothScroller() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val scrollRuler = viewBinding.scrollableRulerSmoothScroller
                scrollRuler.rulerStartValue = 0
                scrollRuler.rulerEndValue = 100
                scrollRuler.moveToIndex(20)
                scrollRuler.enableShadow(false)
            }
        }
    }

    private fun scrollViewStopOnLineScroller() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val scrollRuler = viewBinding.scrollableRulerStopOnlineScroller
                scrollRuler.rulerStartValue = 0
                scrollRuler.rulerEndValue = 100
                scrollRuler.moveToIndex(20)
                scrollRuler.enableShadow(false)
            }
        }
    }

    private fun drawLineChart() {
        val listData = list.random()
        val customMarkerView = CustomMarkerView(
            context = this,
            layoutResource = R.layout.custom_marker_view,
            inputData = listData
        )
        customMarkerView.chartView = viewBinding.historyChart
        val breakEvenChartOutputData = computeBreakEvenChartOutputData(listData)//output!!
        val lineChartData =
            convertBreakEvenDataToLineChartData(breakEvenChartOutputData = breakEvenChartOutputData)
        viewBinding.historyChart.apply {
            visibility = View.VISIBLE
            setTouchEnabled(true)
            setDrawMarkers(true)
            configureBreakEvenChart(breakEvenChartOutputData.maxY)
            data = lineChartData
            marker = customMarkerView
            isDragEnabled = true
            isHighlightPerDragEnabled = true
            isHighlightPerTapEnabled = true
            touchscreenBlocksFocus = false
            //animateY(10, Easing.EaseInOutQuad)
            animateXY(100, 100, Easing.EaseInOutQuad)
            setScaleEnabled(false)
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false
            description.apply {
                text = ""
                textSize = 12f
                isEnabled = false // make it true to show the description
            }
        }
        customMarkerView.hideHighlightLine()

        viewBinding.historyChart.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewBinding.parent.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    viewBinding.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
    }

    private fun resetChart() {
        lifecycleScope.launch {
            while (true) {
                drawLineChart()
                delay(5000) // to mock the behaviour of reloading the chart after 5 sec
                viewBinding.historyChart.axisLeft.removeAllLimitLines() //remove this limit line
                viewBinding.historyChart.axisRight.removeAllLimitLines()
                viewBinding.historyChart.xAxis.removeAllLimitLines()
            }
        }
    }

    fun computeBreakEvenChartOutputData(breakEvenChartInputData: BreakEvenChartInputData): BreakEvenChartOutputData {

        if (breakEvenChartInputData.premium <= 0.0) {
            return BreakEvenChartOutputData(
                maxY = 0.0,
                positiveChartValues = listOf(),
                negativeChartValues = listOf(),
                isChartPlottable = false
            )
        }

        var maxY = 0.0
        val positiveChartValues = arrayListOf<BEChartData>()
        val negativeChartValues = arrayListOf<BEChartData>()

        val maxLoss = breakEvenChartInputData.premium * breakEvenChartInputData.lotSize

        val percentageDifferance =
            breakEvenChartInputData.premium * 100 / breakEvenChartInputData.breakEven
        val roundedPercentageValue = percentageDifferance.round(2)

        var maxLoopNumber = 499
        var fractionNumber = 0.01

        //Below can be deprecated
        if (roundedPercentageValue < 1 && roundedPercentageValue > 0) {
            fractionNumber = roundedPercentageValue * 10 / 500
        } else if (roundedPercentageValue > 5) {
            maxLoopNumber = (roundedPercentageValue * 10).toInt() + 99
            fractionNumber = 0.1
        }

        if (breakEvenChartInputData.optionType == OptionType.CALL) {
            if (breakEvenChartInputData.isSell) {
                for (i in (0..maxLoopNumber)) {
                    val value = breakEvenChartInputData.breakEven * (100 - i * fractionNumber) / 100
                    val plValue =
                        breakEvenChartInputData.lotSize * (value - breakEvenChartInputData.breakEven)

                    val computedValue =
                        breakEvenChartInputData.breakEven * (100 + i * fractionNumber) / 100
                    negativeChartValues.add(
                        BEChartData(
                            xValue = i,
                            yValue = plValue,
                            itemValue = computedValue
                        )
                    )
                    if (i == maxLoopNumber) {
                        maxY = -plValue.round(2)
                    }
                }


                for (i in (1..maxLoopNumber).reversed()) {
                    val value = breakEvenChartInputData.breakEven * (100 + i * fractionNumber) / 100
                    val plValue =
                        breakEvenChartInputData.lotSize * (value - breakEvenChartInputData.breakEven)

                    val computedValue =
                        breakEvenChartInputData.breakEven * (100 - i * fractionNumber) / 100
                    if (plValue < maxLoss) {
                        positiveChartValues.add(
                            BEChartData(
                                xValue = -i,
                                yValue = plValue,
                                itemValue = computedValue
                            )
                        )
                    } else {
                        positiveChartValues.add(
                            BEChartData(
                                xValue = -i,
                                yValue = maxLoss,
                                itemValue = computedValue
                            )
                        )
                    }
                }
            } else {
                for (i in 0..maxLoopNumber) {
                    val value = breakEvenChartInputData.breakEven * (100 + i * fractionNumber) / 100
                    val plValue =
                        breakEvenChartInputData.lotSize * (value - breakEvenChartInputData.breakEven)
                    positiveChartValues.add(
                        BEChartData(
                            xValue = i,
                            yValue = plValue,
                            itemValue = value
                        )
                    )
                    if (i == maxLoopNumber) {
                        maxY = plValue.round(2)
                    }
                }


                for (i in (1..maxLoopNumber).reversed()) {
                    val value = breakEvenChartInputData.breakEven * (100 - i * fractionNumber) / 100
                    val plValue =
                        breakEvenChartInputData.lotSize * (value - breakEvenChartInputData.breakEven)
                    if (plValue > -maxLoss) {
                        negativeChartValues.add(
                            BEChartData(
                                xValue = -i,
                                yValue = plValue,
                                itemValue = value
                            )
                        )
                    } else {
                        negativeChartValues.add(
                            BEChartData(
                                xValue = -i,
                                yValue = -maxLoss,
                                itemValue = value
                            )
                        )
                    }
                }
            }
        } else {
            if (breakEvenChartInputData.isSell) {
                for (i in (0..maxLoopNumber).reversed()) {
                    val value = breakEvenChartInputData.breakEven * (100 - i * fractionNumber) / 100
                    val plValue =
                        breakEvenChartInputData.lotSize * (value - breakEvenChartInputData.breakEven)
                    negativeChartValues.add(
                        BEChartData(
                            xValue = -i,
                            yValue = plValue,
                            itemValue = value
                        )
                    )
                    if (i == maxLoopNumber) {
                        maxY = -plValue.round(2)
                    }
                }


                for (i in (1..maxLoopNumber)) {
                    val value = breakEvenChartInputData.breakEven * (100 + i * fractionNumber) / 100
                    val plValue =
                        breakEvenChartInputData.lotSize * (value - breakEvenChartInputData.breakEven)
                    if (plValue < maxLoss) {
                        positiveChartValues.add(
                            BEChartData(
                                xValue = i,
                                yValue = plValue,
                                itemValue = value
                            )
                        )
                    } else {
                        positiveChartValues.add(
                            BEChartData(
                                xValue = i,
                                yValue = maxLoss,
                                itemValue = value
                            )
                        )
                    }
                }
            } else {
                for (i in (0..maxLoopNumber).reversed()) {
                    val value = breakEvenChartInputData.breakEven * (100 + i * fractionNumber) / 100
                    val plValue =
                        breakEvenChartInputData.lotSize * (value - breakEvenChartInputData.breakEven)

                    val computedValue =
                        breakEvenChartInputData.breakEven * (100 - i * fractionNumber) / 100
                    positiveChartValues.add(
                        BEChartData(
                            xValue = -i,
                            yValue = plValue,
                            itemValue = computedValue
                        )
                    )
                    if (i == maxLoopNumber) {
                        maxY = plValue.round(2)
                    }
                }


                for (i in (1..maxLoopNumber)) {
                    val value = breakEvenChartInputData.breakEven * (100 - i * fractionNumber) / 100
                    val plValue =
                        breakEvenChartInputData.lotSize * (value - breakEvenChartInputData.breakEven)

                    val computedValue =
                        breakEvenChartInputData.breakEven * (100 + i * fractionNumber) / 100
                    if (plValue > -maxLoss) {
                        negativeChartValues.add(
                            BEChartData(
                                xValue = i,
                                yValue = plValue,
                                itemValue = computedValue
                            )
                        )
                    } else {
                        negativeChartValues.add(
                            BEChartData(
                                xValue = i,
                                yValue = -maxLoss,
                                itemValue = computedValue
                            )
                        )
                    }
                }
            }

            negativeChartValues.forEach {
                Log.w(
                    TAG,
                    "negativeChartValues  (${it.xValue}  ,  ${it.yValue})  for ${it.itemValue}"
                )
            }
            positiveChartValues.forEach {
                Log.d(
                    TAG,
                    "positiveChartValues  (${it.xValue}  ,  ${it.yValue})  for ${it.itemValue}"
                )
            }

        }


        val breakEvenMarkerPointValue = BEChartData(
            xValue = 0,
            yValue = 0.0,
            itemValue = breakEvenChartInputData.breakEven
        )

        val maxLossMarkerPointValue = when (breakEvenChartInputData.optionType) {
            OptionType.CALL -> {
                val xValues = if (breakEvenChartInputData.isSell) {
                    negativeChartValues.lastOrNull()
                } else {
                    negativeChartValues.filter { it.yValue == -maxLoss }.maxByOrNull { it.xValue }
                } ?: BEChartData(0, 0.0, maxLoss)

                BEChartData(
                    xValue = xValues.xValue,
                    yValue = xValues.yValue,
                    itemValue = xValues.itemValue
                )
            }

            OptionType.PUT -> {
                val xValues = if (breakEvenChartInputData.isSell) {
                    negativeChartValues.firstOrNull()
                } else {
                    negativeChartValues.filter { it.yValue == -maxLoss }.minByOrNull { it.xValue }
                } ?: BEChartData(0, 0.0, maxLoss)

                BEChartData(
                    xValue = xValues.xValue,
                    yValue = xValues.yValue,
                    itemValue = xValues.itemValue
                )
            }
        }

        val maxProfitMarkerPointValue = when (breakEvenChartInputData.optionType) {
            OptionType.CALL -> {
                val xValues = if (breakEvenChartInputData.isSell) {
                    positiveChartValues.filter { it.yValue == maxLoss }
                } else {
                    listOf(positiveChartValues.lastOrNull())
                }

                val maxValue = xValues.maxByOrNull { it?.xValue ?: 0 } ?: BEChartData(0, 0.0, 0.0)

                BEChartData(xValue = maxValue.xValue, yValue = maxValue.yValue, itemValue = maxY)
            }

            OptionType.PUT -> {
                val xValues = if (breakEvenChartInputData.isSell) {
                    positiveChartValues.filter { it.yValue == maxLoss }
                } else {
                    listOf(positiveChartValues.firstOrNull())
                }

                val minValue = xValues.minByOrNull { it?.xValue ?: 0 } ?: BEChartData(0, 0.0, 0.0)

                BEChartData(xValue = minValue.xValue, yValue = minValue.yValue, itemValue = maxY)
            }
        }

        return BreakEvenChartOutputData(
            maxY = maxY,
            positiveChartValues = positiveChartValues,
            negativeChartValues = negativeChartValues,
            breakEvenMarkerPointValue = breakEvenMarkerPointValue,
            maxLossMarkerPointValue = maxLossMarkerPointValue,
            maxProfitMarkerPointValue = maxProfitMarkerPointValue,
            isChartPlottable = true
        )
    }

    @SuppressLint("NewApi")
    fun convertBreakEvenDataToLineChartData(
        breakEvenChartOutputData: BreakEvenChartOutputData
    ): LineData {
        val positiveChartDataEntries = arrayListOf<Entry>()
        val negativeChartDataEntries = arrayListOf<Entry>()
        val breakEvenMarkerPointEntries = arrayListOf<Entry>()
        val maxLossMarkerPointEntries = arrayListOf<Entry>()
        val maxProfitMarkerPointEntries = arrayListOf<Entry>()

        breakEvenChartOutputData.negativeChartValues.forEach { data ->
            negativeChartDataEntries.add(
                Entry(
                    data.xValue.toFloat(),
                    data.yValue.round(2).toFloat(),
                    data
                )
            )
        }

        breakEvenChartOutputData.positiveChartValues.forEach { data ->
            positiveChartDataEntries.add(
                Entry(
                    data.xValue.toFloat(),
                    data.yValue.round(2).toFloat(),
                    data
                )
            )
        }

        breakEvenMarkerPointEntries.add(
            Entry(
                breakEvenChartOutputData.breakEvenMarkerPointValue?.xValue?.toFloat() ?: 0.0f,
                breakEvenChartOutputData.breakEvenMarkerPointValue?.yValue?.round(2)?.toFloat()
                    ?: 0.0f,
                breakEvenChartOutputData.breakEvenMarkerPointValue
            )
        )

        maxLossMarkerPointEntries.add(
            Entry(
                breakEvenChartOutputData.maxLossMarkerPointValue?.xValue?.toFloat() ?: 0.0f,
                breakEvenChartOutputData.maxLossMarkerPointValue?.yValue?.round(2)?.toFloat()
                    ?: 0.0f,
                breakEvenChartOutputData.maxLossMarkerPointValue
            )
        )

        maxProfitMarkerPointEntries.add(
            Entry(
                breakEvenChartOutputData.maxProfitMarkerPointValue?.xValue?.toFloat() ?: 0.0f,
                breakEvenChartOutputData.maxProfitMarkerPointValue?.yValue?.round(2)?.toFloat()
                    ?: 0.0f,
                breakEvenChartOutputData.maxProfitMarkerPointValue
            )
        )

        negativeChartDataEntries.forEach {
            Log.d(
                TAG,
                "negative (${it.x}  ,  ${it.y})"
            )
        }

        positiveChartDataEntries.forEach {
            Log.d(
                TAG,
                "positive (${it.x}  ,  ${it.y})"
            )
        }

        val dataSet1 = LineDataSet(positiveChartDataEntries, "Set-1")
        dataSet1.apply {

            color = Color.rgb(1, 177, 143)
            lineWidth = 2f
            setDrawValues(true)
            setDrawCircles(false)
            setDrawCircleHole(false)
            setCircleColor(Color.BLACK)
            circleRadius = 2f
            setDrawFilled(true)
            setDrawHorizontalHighlightIndicator(false)
            highLightColor = Color.rgb(0, 134, 54)
            highlightLineWidth = 2f
            //fillColor = Color.argb(51, 80, 170, 80)
            fillColor = Color.argb(10, 1, 177, 143)
        }

        val dataSet2 = LineDataSet(negativeChartDataEntries, "Set-2")
        dataSet2.apply {

            //color = Color.rgb(224, 22, 22)
            color = Color.rgb(235, 59, 59)
            lineWidth = 2f
            setDrawValues(true)
            setDrawCircles(false)
            setDrawCircleHole(false)
            setDrawFilled(true)
            setDrawHorizontalHighlightIndicator(false)
            //highLightColor = Color.rgb(224, 22, 22)
            highLightColor = Color.rgb(235, 59, 59)
            highlightLineWidth = 2f
            //fillColor = Color.rgb(253, 227, 228)
            fillColor = Color.argb(10, 235, 59, 59)
        }

        val dataSet3 = LineDataSet(breakEvenMarkerPointEntries, "Set-3")
        dataSet3.apply {
            setDrawCircles(true)
            setDrawCircleHole(false)
            circleRadius = 4f //change radius as needed
            lineWidth = 2f
            val dotColor = baseContext.getColor(R.color.md_theme_onSurface)
            setCircleColor(dotColor)
            highlightLineWidth = 2f
            highLightColor = Color.rgb(235, 59, 59)
            setDrawHorizontalHighlightIndicator(false)
        }

        val dataSet4 = LineDataSet(maxLossMarkerPointEntries, "Set-4")
        dataSet4.apply {
            setDrawCircles(true)
            setDrawCircleHole(false)
            circleRadius = 4f //change radius as needed
            lineWidth = 2f
            setCircleColor(Color.RED)
            highlightLineWidth = 2f
            highLightColor = Color.rgb(235, 59, 59)
            setDrawHorizontalHighlightIndicator(false)
        }

        val dataSet5 = LineDataSet(maxProfitMarkerPointEntries, "Set-5")
        dataSet5.apply {
            val circleColor = ContextCompat.getColor(baseContext, R.color.green)
            lineWidth = 2f
            setDrawCircles(true)
            setDrawCircleHole(false)
            circleRadius = 4f //change radius as needed
            setCircleColor(circleColor)
            highlightLineWidth = 2f
            highLightColor = Color.rgb(0, 134, 54)
            setDrawHorizontalHighlightIndicator(false)
        }

        val dataSets = arrayListOf<LineDataSet>()
        dataSets.add(dataSet1)
        dataSets.add(dataSet2)
        dataSets.add(dataSet3)
        dataSets.add(dataSet4)
        dataSets.add(dataSet5)
        return LineData(dataSets.toList())
    }

    @SuppressLint("NewApi")
    fun LineChart.configureBreakEvenChart(maxY: Double) {
        axisLeft.apply {
            axisMinimum = -maxY.toFloat()
            axisMaximum = ceil(maxY.toFloat())

            zeroLineColor = baseContext.getColor(R.color.md_theme_onSurface)
            zeroLineWidth = 1.5f
            setDrawZeroLine(true)
            setDrawMarkers(true)

            setDrawGridLines(false)
            setDrawAxisLine(false)
            legend.isEnabled = false
            setDrawLabels(true)
            setLabelCount(5, true)

            addLimitLine(LimitLine(maxY.toFloat()).apply {
                lineWidth = 1.5f
                lineColor = baseContext.getColor(R.color.limit_line_color)
                enableDashedLine(20.0f, 25.0f, 0.0f)
                labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
            }).also {
                setDrawTopYLabelEntry(false)
            }
            addLimitLine(LimitLine(-maxY.toFloat()).apply {
                lineWidth = 1.5f
                lineColor = baseContext.getColor(R.color.limit_line_color)
                enableDashedLine(20.0f, 25.0f, 0.0f)
            })

            // Customize the axis labels
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when {
                        value.toInt() == 0 -> "0"
                        value > 0 -> "+"
                        value.toDouble().round(2) == -maxY || value.toDouble()
                            .round(2) == maxY -> ""

                        value < 0 -> "-"
                        else -> "0"
                    }
                }
            }.apply {
                textColor = baseContext.getColor(R.color.md_theme_onSurface)
            }
        }

        axisRight.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setDrawLabels(false)
        }

        xAxis.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setDrawLabels(false)
        }
    }
}