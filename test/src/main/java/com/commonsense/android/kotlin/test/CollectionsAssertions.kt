package com.commonsense.android.kotlin.test

import junit.framework.Assert

/**
 * Created by Kasper Tvede on 21-05-2018.
 * Purpose:
 *
 */
fun <T> Array<T>.assertSize(size: Int, message: String = "") {
    this.size.assert(size, message)
}

fun <T> Collection<T>.assertSize(size: Int, message: String = "") {
    this.size.assert(size, message)
}

fun <T> Collection<T>.assertEmpty(message: String = "") {
    assertSize(0, message)
}


fun List<*>.assertEmpty(message: String = "") {
    assertSize(0, message)
}

fun List<*>.assertSize(size: Int, message: String = "") {
    Assert.assertEquals(message, this.size, size)
}