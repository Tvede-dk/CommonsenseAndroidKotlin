package com.commonsense.android.base.extensions

import com.commonsense.android.kotlin.base.extensions.isNotZero
import com.commonsense.android.kotlin.base.extensions.isZero
import com.commonsense.android.kotlin.base.extensions.negative
import com.commonsense.android.kotlin.test.assert
import org.junit.Test

/**
 * Created by Kasper Tvede on 17-04-2018.
 * Purpose:
 *
 */
class LongExtensionsKtTest {

    @Test
    fun testIsNotZero() {
        0L.isNotZero.assert(false, "zero is \"not zero\"")
        1L.isNotZero.assert(true)
        (-1L).isNotZero.assert(true)
        Long.MAX_VALUE.isNotZero.assert(true)
        Long.MIN_VALUE.isNotZero.assert(true)
    }

    @Test
    fun testIsZero() {
        0L.isZero.assert(true, "zero is 0")
        1L.isZero.assert(false)
        (-1L).isZero.assert(false)
        Long.MAX_VALUE.isZero.assert(false)
        Long.MIN_VALUE.isZero.assert(false)
    }

    @Test
    fun testNegative() {
        0L.negative.assert(0)
        (1L).negative.assert(-1L)
        (-1L).negative.assert(-1L)
    }
}