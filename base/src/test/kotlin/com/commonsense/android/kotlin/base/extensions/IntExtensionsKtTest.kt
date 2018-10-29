package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.test.*
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

    @Test
    fun isEven() {
        //some examples
        0.isEven.assertTrue("")
        2.isEven.assertTrue("")
        4.isEven.assertTrue("")
        6.isEven.assertTrue("")
        (-0).isEven.assertTrue("")
        (-2).isEven.assertTrue("")
        (-4).isEven.assertTrue("")
        (-6).isEven.assertTrue("")


        (-5).isEven.assertFalse("")
        (-1).isEven.assertFalse("")
        (1).isEven.assertFalse("")
        (203).isEven.assertFalse("")
    }

    @Test
    fun isOdd() {
        //some examples
        0.isOdd.assertFalse("")
        2.isOdd.assertFalse("")
        4.isOdd.assertFalse("")
        6.isOdd.assertFalse("")
        (-0).isOdd.assertFalse("")
        (-2).isOdd.assertFalse("")
        (-4).isOdd.assertFalse("")
        (-6).isOdd.assertFalse("")


        (-5).isOdd.assertTrue("")
        (-1).isOdd.assertTrue("")
        (1).isOdd.assertTrue("")
        (3).isOdd.assertTrue("")
        (5).isOdd.assertTrue("")
        (17).isOdd.assertTrue("")
        (203).isOdd.assertTrue("")
    }

    @Ignore
    @Test
    fun compareToRange() {
    }

}