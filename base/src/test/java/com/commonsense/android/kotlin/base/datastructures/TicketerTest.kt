package com.commonsense.android.kotlin.base.datastructures

import com.commonsense.android.kotlin.test.*
import org.junit.Test


/**
 * Created by Kasper Tvede on 20-05-2018.
 * Purpose:
 */
internal class TicketerTest {

    @Test
    fun getIdForCategory() {
        val ticker = Ticketer(0)
        val testValue = ticker.getIdForCategory("test")
        val test2Value = ticker.getIdForCategory("test2")
        testValue.assertNotEquals(test2Value)
        testValue.assertLargerOrEqualTo(0, "we are not allowed to go below 0")
        val testValueSecond = ticker.getIdForCategory("test")
        testValueSecond.assert(testValue, "drawing the same category should yield the same" +
                "value")

    }
}