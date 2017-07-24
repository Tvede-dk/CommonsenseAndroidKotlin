package com.commonsense.android.kotlin.test

import android.net.Uri
import org.junit.Assert

/**
 * Created by Kasper Tvede on 18-07-2017.
 */

/**
 * Created by Kasper Tvede on 15-07-2017.
 */


fun Boolean.assert(value: Boolean, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun Int.assert(value: Int, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun String.assert(value: String, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun Double.assert(value: Double, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun Float.assert(value: Float, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun Uri.assert(value: Uri, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun Uri.assert(value: String, message: String = "") {
    Assert.assertEquals(message, value, this.toString())
}


fun List<*>.assertEmpty(message: String = "") {
    assertSize(0, message)
}

fun List<*>.assertSize(size: Int, message: String = "") {
    Assert.assertEquals(message, this.size, size)
}

fun Any?.assertNotNull(message: String = "") {
    Assert.assertNotNull(message, this)
}

fun Any?.assertNull(message: String = "") {
    Assert.assertNull(message, this)
}

fun <T> T?.assertNotNullApply(message: String = "", action: T.() -> Unit) {
    this.assertNotNull(message)
    this?.let(action)
}

fun <T> T?.assertNotNullAndEquals(other: T?, message: String = "") {
    this.assertNotNull(message)
    Assert.assertEquals(message, other, this)
}


fun IntRange.assert(otherRange: IntRange, message: String = "") {
    Assert.assertEquals(message, otherRange, this)
}

inline fun assertThrows(message: String = "should throw", crossinline action: () -> Unit) {
    try {
        action()
        Assert.fail(message)
    } catch (exception: Exception) {
        //all is good.
    }
}

fun <T> Any.assertAs(otherValue: T) {
    Assert.assertEquals(this as? T, otherValue)
}