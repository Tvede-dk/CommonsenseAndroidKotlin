package com.commonsense.android.base.patterns

import com.commonsense.android.kotlin.base.patterns.*
import com.commonsense.android.kotlin.test.*
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

/**
 * Created by Kasper Tvede on 18-04-2018.
 * Purpose:
 *
 */
class ExpectedTest {

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

    @Test
    fun testExtensions() {
        val success20 = expectedSucceded(20)
        success20.isValid.assert(true)
        success20.isError.assert(false)
        success20.error.assertNull()
        success20.value.assert(20)

        val failedText = "failed"
        val failedException = RuntimeException(failedText)
        val failedInt = expectedFailed<Int>(failedException)
        (failedInt.error == failedException).assert(true)
        failedInt.error.message.assertNotNullAndEquals(failedText)
        failedInt.isError.assert(true)
        failedInt.isValid.assert(false)


        var successCounter = 1
        success20.use { successCounter -= 1 }

        successCounter.assert(0,"should 'use' on success")

        var failedCounter = 1
        failedInt.use { failedCounter -= 1 }
        failedCounter.assert(1,"should not 'use' on success")

    }

    @Test
    fun testExtensionsAsync() = runBlocking {
        val success42 = expectedSucceded(42)
        var counter = 1
        success42.useAsync {
            counter -= 1
        }
        counter.assert(0, "should run async first")

        var failedCounter = 1
        val failed = expectedFailed<Int>()
        failed.useAsync {
            failedCounter -= 1
        }
        failedCounter.assert(1, "cannot use a failed result.")


    }
}