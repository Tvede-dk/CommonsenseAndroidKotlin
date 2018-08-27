package com.commonsense.android.kotlin.base.extensions.collections

import com.commonsense.android.kotlin.test.assert
import com.commonsense.android.kotlin.test.failTest
import org.junit.*
import org.junit.jupiter.api.Test


/**
 * Created by Kasper Tvede on 21-05-2018.
 * Purpose:
 */
internal class PrimitiveExtensionsKtTest {

    @Test
    fun onTrue() {
        false.onTrue { failTest("should not call on false") }.assert(false, "value should be returned")
        var counter = 0
        true.onTrue { counter += 1 }.assert(true, "value should be returned")
        counter.assert(1, "should have executed the onTrue content")
    }


    @Test
    fun onFalse() {
        true.onFalse { failTest("should not call on false") }.assert(true, "value should be returned")
        var counter = 0
        false.onFalse { counter += 1 }.assert(false, "value should be returned")
        counter.assert(1, "should have executed the onFalse content")
    }


    @Test
    fun ifTrue() {
        false.ifTrue { failTest("should not call on false") }.assert(false, "value should be returned")
        var counter = 0
        true.ifTrue { counter += 1 }.assert(true, "value should be returned")
        counter.assert(1, "should have executed the onTrue content")
    }

    @Test
    fun ifFalse() {
        true.ifFalse { failTest("should not call on false") }.assert(true, "value should be returned")
        var counter = 0
        false.ifFalse { counter += 1 }.assert(false, "value should be returned")
        counter.assert(1, "should have executed the onFalse content")
    }


    @Test
    fun map() {
        false.map(1, -1).assert(-1)
        true.map("a", "b").assert("a")
    }

    @Test
    fun mapLazy() {
        true.mapLazy({
            42
        }, {
            failTest("should have lazy evaluation")
            20
        }).assert(42)

        false.mapLazy({
            failTest("should have lazy evaluation")
            20
        }, {
            42
        }).assert(42)

    }

    @Test
    fun ifNull() {
        val optString: String? = ""
        optString.ifNull { failTest("should not be called on non null") }

        var counter = 0
        val optInt: Int? = null
        optInt.ifNull { counter += 1 }
        counter.assert(1, "should have executed branch.")
    }

    @Test
    fun ifNotNull() {
        val optString: String? = ""
        var counter = 0
        optString.ifNotNull { counter += 1 }
        counter.assert(1, "should have executed branch.")

        val optInt: Int? = null
        optInt.ifNotNull { failTest("should not be called on null") }
    }


    @Test
    fun getLength() {
        val range = 0 until 20
        range.length.assert(20)

        val weirdRange = 20 until 21
        weirdRange.length.assert(1)

        val wrongRange = 20 until 20
        wrongRange.length.assert(0)

    }

    @Test
    fun getLargest() {
        val range = 0 until 20
        range.largest.assert(19, "largest element is 20 - 1 = 19(due to until )")

        val weirdRange = 20 until 21
        weirdRange.largest.assert(20)

        val wrongRange = 20 until 20
        wrongRange.largest.assert(20)

    }


}