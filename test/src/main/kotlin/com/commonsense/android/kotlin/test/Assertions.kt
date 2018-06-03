package com.commonsense.android.kotlin.test

import android.net.Uri
import android.support.annotation.IntRange
import org.junit.Assert
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * Created by Kasper Tvede on 18-07-2017.
 */
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

fun <T> T?.assertNotNullAndEquals(other: T?, message: String = "value was $this, expected $other") {
    this.assertNotNull(message)
    Assert.assertEquals(message, other, this)
}


fun <U : Comparable<U>> U.assertLargerOrEqualTo(i: U, optMessage: String = "") {
    Assert.assertTrue("$this should be larger or equal to $i, but it is not.\n$optMessage", this >= i)
}

fun <U : Comparable<U>> U.assertLargerThan(i: U, optMessage: String = "") {
    Assert.assertTrue("$this should be larger than $i, but it is not.\n$optMessage", this > i)
}


fun <T : Comparable<T>> T.assertNotEquals(other: T, message: String = "") {
    Assert.assertNotEquals(message, other, this)
}


fun <T> Any.assertAs(otherValue: T, message: String = "") {
    @Suppress("UNCHECKED_CAST") //this is expected
    // we are just making life easier for testing, if it throws, then its "all right" for a test.
    Assert.assertEquals(message, this as? T, otherValue)
}

fun failTest(message: String = "") {
    Assert.fail(message)
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
