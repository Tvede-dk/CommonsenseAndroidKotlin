package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.test.*
import org.junit.jupiter.api.*

/**
 * Created by Kasper Tvede on 17-04-2018.
 * Purpose:
 *
 */
class LongExtensionsKtTest {

    @Test
    fun getNegative() {
        0L.negative.assert(0)
        (1L).negative.assert(-1L)
        (-1L).negative.assert(-1L)
    }

    @Test
    fun isNotZero() {
        0L.isNotZero.assert(false, "zero is \"not zero\"")
        1L.isNotZero.assert(true)
        (-1L).isNotZero.assert(true)
        Long.MAX_VALUE.isNotZero.assert(true)
        Long.MIN_VALUE.isNotZero.assert(true)

    }

    @Test
    fun isZero() {
        0L.isZero.assert(true, "zero is 0")
        1L.isZero.assert(false)
        (-1L).isZero.assert(false)
        Long.MAX_VALUE.isZero.assert(false)
        Long.MIN_VALUE.isZero.assert(false)

    }
}