package com.commonsense.android.kotlin.test

import org.junit.Assert

/**
 * Created by Kasper Tvede on 18-07-2017.
 */

/**
 * Created by Kasper Tvede on 15-07-2017.
 */


fun Boolean.assert(value: Boolean, message: String = "") {
    org.junit.Assert.assertEquals(message, value, this)
}

fun Int.assert(value: Int, message: String = "") {
    org.junit.Assert.assertEquals(message, value, this)
}

fun String.assert(value: String, message: String = "") {
    org.junit.Assert.assertEquals(message, value, this)
}

fun Double.assert(value: Double, message: String = "") {
    org.junit.Assert.assertEquals(message, value, this)
}

fun Float.assert(value: Float, message: String = "") {
    org.junit.Assert.assertEquals(message, value, this)
}

fun List<*>.assertEmpty(message: String = "") {
    assertSize(0, message)
}

fun List<*>.assertSize(size: Int, message: String = "") {
    org.junit.Assert.assertEquals(message, this.size, size)
}

fun Any?.assertNotNull(message: String = "") {
    org.junit.Assert.assertNotNull(message, this)
}

fun Any?.assertNull(message: String = "") {
    org.junit.Assert.assertNull(message, this)
}

fun <T> T?.assertNotNullApply(message: String = "", action: T.() -> Unit) {
    this.assertNotNull(message)
    this?.let(action)
}


fun IntRange.assert(otherRange: IntRange, message: String = "") {
    Assert.assertEquals(message, otherRange, this)
}

