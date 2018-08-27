package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.test.assert
import org.junit.*
import org.junit.jupiter.api.Test


/**
 * Created by Kasper Tvede on 17-04-2018.
 * Purpose:
 *
 */
class IntExtensionsKtTest {


    @Test
    fun getNegative() {
        0.negative.assert(0)
        (-1).negative.assert(-1)
        1.negative.assert(-1)
    }

    @Test
    fun isNotZero() {
        0.isNotZero.assert(false, "zero is \"not zero\"")
        1.isNotZero.assert(true)
        (-1).isNotZero.assert(true)
        Int.MAX_VALUE.isNotZero.assert(true)
        Int.MIN_VALUE.isNotZero.assert(true)
    }

    @Test
    fun isZero() {
        0.isZero.assert(true, "zero is 0")
        1.isZero.assert(false)
        (-1).isZero.assert(false)
        Int.MAX_VALUE.isZero.assert(false)
        Int.MIN_VALUE.isZero.assert(false)
    }

    @Ignore
    @Test
    fun isEven() {
    }

    @Ignore
    @Test
    fun isOdd() {
    }

    @Ignore
    @Test
    fun compareToRange() {
    }

}