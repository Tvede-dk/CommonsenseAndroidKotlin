package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.test.assert
import org.junit.*
import org.junit.jupiter.api.Test


/**
 * Created by Kasper Tvede on 18-04-2018.
 * Purpose:
 *
 */

class FloatExtensionsKtTest {

    @Test
    fun getNegative() {
        2f.negative.assert(-2f)
        (-2f).negative.assert(-2f)
        0f.negative.assert(0f)
    }


    @Ignore
    @Test
    fun equals() {
    }

    @Test
    fun isZero() {
        2f.isZero().assert(false)
        0f.isZero().assert(true)
        0.1f.isZero(0.2f).assert(true)
    }

}