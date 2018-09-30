package com.commonsense.android.kotlin.base.extensions.collections

import com.commonsense.android.kotlin.test.*
import org.junit.*
import org.junit.jupiter.api.Test

/**
 * Created by Kasper Tvede on 20-05-2018.
 * Purpose:
 *
 */
class ArrayExtensionsKtTest {

    @Test
    fun testIntProgressionToArray() {

        IntProgression.fromClosedRange(0,9,1).toIntArray().apply {
            size.assert(10)
            first().assert(0)
            last().assert(9)
        }

        IntProgression.fromClosedRange(0,9,2).toIntArray().apply {
            size.assert(5)
            first().assert(0)
            last().assert(8)
        }

        IntProgression.fromClosedRange(0,10,2).toIntArray().apply {
            size.assert(6)
            first().assert(0)
            last().assert(10)
        }
    }

    @Test
    fun testIntProgressionLength() {
        IntProgression.fromClosedRange(0, 9, 1).length.assert(10)
        IntProgression.fromClosedRange(0, 9, 4).length.assert(3)
        IntProgression.fromClosedRange(0, 9, 10).length.assert(1)
    }


    @Test
    fun toIntArray() {
    }

    @Ignore
    @Test
    fun getLength() {
    }

    @Ignore
    @Test
    fun previousValueOr() {
    }

    @Ignore
    @Test
    fun binarySearch() {
    }
}