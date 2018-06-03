package com.commonsense.android.kotlin.test

import org.junit.Assert

/**
 * Created by Kasper Tvede on 21-05-2018.
 * Purpose:
 *
 */

fun Double.assert(value: Double, delta: Double = 0.1, message: String = "") {
    Assert.assertEquals(message, value, this, delta)
}

fun Float.assert(value: Float, delta: Float = 0.1f, message: String = "") {
    Assert.assertEquals(message, value, this, delta)
}


fun <T : Number> T.assert(value: T, message: String = "") {
    Assert.assertEquals(message, value, this)
}


fun Char.assert(value: Char, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun Byte.assert(value: Byte, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun kotlin.ranges.IntRange.assert(otherRange: kotlin.ranges.IntRange, message: String = "") {
    Assert.assertEquals(message, otherRange, this)
}