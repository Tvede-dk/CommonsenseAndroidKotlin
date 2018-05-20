package com.commonsense.android.base.extensions

import com.commonsense.android.kotlin.base.extensions.negative
import com.commonsense.android.kotlin.test.assert
import org.junit.Test

/**
 * Created by Kasper Tvede on 18-04-2018.
 * Purpose:
 */
class DoubleExtensionsKtTest {

    @Test
    fun getNegative() {
        val positive: Double = 2.0
        positive.negative.assert(-2.0)
        val negative: Double = -2.0
        negative.negative.assert(-2.0)
    }

}