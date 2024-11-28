package com.example.specequipmentapp.util

object NativeUtils {
    init {
        System.loadLibrary("native-lib")
    }

    external fun calculateTotalPrice(quantities: IntArray, prices: DoubleArray): Double
}
