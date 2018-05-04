package com.commonsense.android.base.patterns

import com.commonsense.android.kotlin.base.patterns.Expected
import com.commonsense.android.kotlin.test.*
import org.junit.Test

/**
 * Created by Kasper Tvede on 18-04-2018.
 * Purpose:
 *
 */
class ExpectedKtTest {

    @Test
    fun testSuccessFailed() {

        val testSuccess = Expected.success(42)
        testSuccess.isValid.assert(true)
        testSuccess.isError.assert(false)
        testSuccess.error.assertNull()
        testSuccess.value.assert(42)


        val testFailed = Expected.failed<Int>()
        testFailed.isValid.assert(false)
        testFailed.isError.assert(true)
        assertThrows<Exception> {
            testFailed.value.assertNull()
        }
        testFailed.error.assertNotNull()
    }
}