package com.example.exovideoplayer.strikepricewidget.model

import com.example.exovideoplayer.ui.fnochart.OptionType

data class BreakEvenChartInputData(
    val isSell: Boolean,
    val premium: Double,
    val breakEven: Double,
    val lotSize: Int,
    val optionType: OptionType,
    val optionName: String,
    val parentStockPrice: Double
)