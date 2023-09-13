package com.example.exovideoplayer

data class BreakEvenChartInputData(
    val isSell: Boolean,
    val premium: Double,
    val breakEven: Double,
    val lotSize: Int,
    val optionType: OptionType,
    val optionName: String,
    val parentStockPrice: Double
)