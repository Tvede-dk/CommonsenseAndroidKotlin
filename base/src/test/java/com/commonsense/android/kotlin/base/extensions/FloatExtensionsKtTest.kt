package com.commonsense.android.base.extensions

import com.commonsense.android.kotlin.base.extensions.isZero
import com.commonsense.android.kotlin.base.extensions.negative
import com.commonsense.android.kotlin.test.assert
import org.junit.Test

/**
 * Created by Kasper Tvede on 18-04-2018.
 * Purpose:
 *
 */

class FloatExtensionsKtTest {

    @Test
    fun testNegative() {
        2f.negative.assert(-2f)
        (-2f).negative.assert(-2f)
        0f.negative.assert(0f)
    }

    @Test
    fun testIsZero() {
        2f.isZero().assert(false)
        0f.isZero().assert(true)
        0.1f.isZero(0.2f).assert(true)
    }

}