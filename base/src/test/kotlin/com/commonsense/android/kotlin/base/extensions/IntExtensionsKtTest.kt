package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.test.assert
import org.junit.jupiter.api.Test


/**
 * Created by Kasper Tvede on 17-04-2018.
 * Purpose:
 *
 */
class IntExtensionsKtTest {

    @Test
    fun testIsNotZero() {
        0.isNotZero.assert(false, "zero is \"not zero\"")
        1.isNotZero.assert(true)
        (-1).isNotZero.assert(true)
        Int.MAX_VALUE.isNotZero.assert(true)
        Int.MIN_VALUE.isNotZero.assert(true)
    }

    @Test
    fun testIsZero() {
        0.isZero.assert(true, "zero is 0")
        1.isZero.assert(false)
        (-1).isZero.assert(false)
        Int.MAX_VALUE.isZero.assert(false)
        Int.MIN_VALUE.isZero.assert(false)
    }

    @Test
    fun testNegative() {
        0.negative.assert(0)
        (-1).negative.assert(-1)
        1.negative.assert(-1)
    }

}