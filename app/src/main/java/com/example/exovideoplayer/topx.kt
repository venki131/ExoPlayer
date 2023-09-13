package com.example.exovideoplayer

import android.content.res.Resources.getSystem

val Int.topx: Int get() = (this * getSystem().displayMetrics.density).toInt()

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

fun Int.dpToPx() = this * getSystem().displayMetrics.density

