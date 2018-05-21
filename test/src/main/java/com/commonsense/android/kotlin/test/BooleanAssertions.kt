package com.commonsense.android.kotlin.test

import org.junit.Assert

/**
 * Created by Kasper Tvede on 21-05-2018.
 * Purpose:
 *
 */

fun Boolean.assert(value: Boolean, message: String = "") {
    Assert.assertEquals(message, value, this)
}

fun Boolean.assertFalse(message: String = "") {
    assert(false, message)
}

fun Boolean.assertTrue(message: String = "") {
    assert(true, message)
}