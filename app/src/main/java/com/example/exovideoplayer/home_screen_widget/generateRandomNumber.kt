package com.example.exovideoplayer.home_screen_widget

fun generateRandomNumber(): Int {
    val random = java.util.Random()
    return random.nextInt(10) + 1
}

fun main() {
    val randomNumber = generateRandomNumber()
    println("Random Number between 1 and 10: $randomNumber")
}
