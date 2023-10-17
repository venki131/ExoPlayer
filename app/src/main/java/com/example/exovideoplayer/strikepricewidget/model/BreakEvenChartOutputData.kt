package com.example.exovideoplayer.strikepricewidget.model

import com.example.exovideoplayer.strikepricewidget.model.BEChartData

data class BreakEvenChartOutputData(
    val maxY: Double,
    val positiveChartValues: List<BEChartData>,
    val negativeChartValues: List<BEChartData>,
    val breakEvenMarkerPointValue: BEChartData? = null,
    val maxLossMarkerPointValue: BEChartData? = null,
    val maxProfitMarkerPointValue: BEChartData? = null,
    val isChartPlottable: Boolean
)