package com.commonsense.android.kotlin.test

import android.net.Uri
import android.support.annotation.IntRange
import org.junit.Assert
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * Created by Kasper Tvede on 18-07-2017.
 */
fun Boolean.assert(value: Boolean, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun <T : Number>T.assert(value: T, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun String.assert(value: String, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun Char.assert(value: Char, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun Byte.assert(value: Byte, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun String.assertContains(value: String,
                          ignoreCase: Boolean = false,
                          message: String = "Could not find \"$value\", in  \r\n\"$this\"") {
    Assert.assertTrue(message, this.contains(value, ignoreCase = ignoreCase))
}

fun String.assertContainsNot(value: String,
                             ignoreCase: Boolean = false,
                             message: String = "") {
    Assert.assertFalse("$message \n Reason: Could find \"$value\", in  \r\n\"$this\"", this.contains(value, ignoreCase = ignoreCase))
}


fun Double.assert(value: Double, delta: Double = 0.1, message: String = "") {
    Assert.assertEquals(message, value, this, delta)
}

fun Float.assert(value: Float, delta: Float = 0.1f, message: String = "") {
    Assert.assertEquals(message, value, this, delta)
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


fun <U : Comparable<U>> U.assertLargerOrEqualTo(i: U, optMessage: String = "") {
    Assert.assertTrue("$this should be larger or equal to $i, but it is not.\n$optMessage", this >= i)
}

fun <U : Comparable<U>> U.assertLargerThan(i: U, optMessage: String = "") {
    Assert.assertTrue("$this should be larger than $i, but it is not.\n$optMessage", this > i)
}


fun kotlin.ranges.IntRange.assert(otherRange: kotlin.ranges.IntRange, message: String = "") {
    Assert.assertEquals(message, otherRange, this)
}

inline fun <reified T : Exception> assertThrows(
        message: String = "should throw",
        messageWrongException: String = "wrong exception type",
        crossinline action: () -> Unit) {

    try {
        action()
        failTest("Expected an exception of type ${T::class.java.simpleName} but got no exceptions\r$message")
    } catch (exception: Exception) {
        if (exception is T) {
            //all is good / expected.
        } else {
            failTest("Expected an exception of type \"${T::class.java.simpleName}\" " +
                    "but got exception of type \"${exception::class.java.simpleName}\" instead." +
                    "\r$messageWrongException")
        }

    }
}

fun <T> Any.assertAs(otherValue: T, message: String = "") {
    @Suppress("UNCHECKED_CAST") //this is expected
    // we are just making life easier for testing, if it throws, then its "all right" for a test.
    Assert.assertEquals(message, this as? T, otherValue)
}

fun failTest(message: String = "") {
    Assert.fail(message)
}

inline fun testCallbackWithSemaphore(@IntRange(from = 0) startPermits: Int = 0,
                                     @IntRange(from = 0) startAcquire: Int = startPermits + 1,
                                     @IntRange(from = 0) timeoutTime: Int = 50,
                                     timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
                                     shouldAcquire: Boolean = true,
                                     errorMessage: String = "",
                                     callback: (Semaphore) -> Unit) {
    val sem = Semaphore(startPermits)
    callback(sem)
    sem.tryAcquire(startAcquire, timeoutTime.toLong(), timeoutUnit).assert(shouldAcquire, errorMessage)
}
