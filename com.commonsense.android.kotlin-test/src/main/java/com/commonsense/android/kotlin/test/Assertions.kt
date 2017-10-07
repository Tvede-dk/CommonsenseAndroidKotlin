package com.commonsense.android.kotlin.test

import android.net.Uri
import android.support.annotation.IntRange
import org.junit.Assert
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

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

fun Double.assert(value: Double, delta: Double = 0.1, message: String = "") {
    Assert.assertEquals(message, value, this, delta)
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


fun kotlin.ranges.IntRange.assert(otherRange: kotlin.ranges.IntRange, message: String = "") {
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

fun <T> Any.assertAs(otherValue: T, message: String = "") {
    @Suppress("UNCHECKED_CAST") //this is exepcted, we are just making life easier for testing, if it throws, then its "all right" for a test.
    Assert.assertEquals(message, this as? T, otherValue)
}

fun failTest(message: String = "") {
    Assert.fail(message)
}

inline fun testCallbackWithSemaphore(@IntRange(from = 0) startPermits: Int = 0,
                                     @IntRange(from = 0) startAquire: Int = startPermits + 1,
                                     @IntRange(from = 0) timeoutTime: Int = 50,
                                     timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
                                     shouldAquire: Boolean = true,
                                     errorMessage: String = "",
                                     callback: (Semaphore) -> Unit) {
    val sem = Semaphore(startPermits)
    callback(sem)
    sem.tryAcquire(startAquire, timeoutTime.toLong(), timeoutUnit).assert(shouldAquire, errorMessage)
}