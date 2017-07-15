package com.commonsense.android.kotlin.testHelpers

/**
 * Created by Kasper Tvede on 15-07-2017.
 */


fun Boolean.assert(value: Boolean, message: String = "") {
    org.junit.Assert.assertEquals(message, this, value)
}

fun Int.assert(value: Int, message: String = "") {
    org.junit.Assert.assertEquals(message, this, value)
}

fun String.assert(value: String, message: String = "") {
    org.junit.Assert.assertEquals(message, this, value)
}

fun Double.assert(value: Double, message: String = "") {
    org.junit.Assert.assertEquals(message, this, value)
}

fun Float.assert(value: Float, message: String = "") {
    org.junit.Assert.assertEquals(message, this, value)
}

fun List<*>.assertEmpty(message: String = "") {
    assertSize(0, message)
}

fun List<*>.assertSize(size: Int, message: String = "") {
    org.junit.Assert.assertEquals(message, size, this.size)
}

fun Any?.assertNotNull(message: String = "") {
    org.junit.Assert.assertNotNull(message, this)

}

inline fun <T> T?.assertNotNullApply(message: String = "", crossinline action: T.() -> Unit) {
    this.assertNotNull(message)
    this?.let(action)
}


