package com.commonsense.android.kotlin.base.extensions.collections

import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.test.*
import com.commonsense.android.kotlin.test.benchmark.*
import org.junit.*
import org.junit.jupiter.api.Test

/**
 * Created by Kasper Tvede on 11-04-2018.
 * Purpose:
 *
 */
class ByteArrayExtensionsKtTest {

    @Test
    fun toHexString() {
        val array = byteArrayOf(0x3A, 0x69, 0x0f)
        array.toHexString(appendHexPrefix = true).assert("0x3A690F")
        array.toHexString(appendHexPrefix = true, shouldBeUppercase = true).assert("0x3A690F")
        array.toHexString(shouldBeUppercase = false).assert("3a690f")
        array.toHexString(appendHexPrefix = true, shouldBeUppercase = false).assert("0x3a690f")
    }

    /**
     * a regression test.
     */
    @Ignore
    @Test
    fun benchmarkToHexString() {

        val array = mutableListOf<Byte>()
        2048.forEach {
            array.add(it.toByte())
        }
        val bytearray = array.toByteArray()

        microBench(limitMsPrInvocation = 500, forceGcBetweenRuns = true, totalTimeoutInSeconds = 60) {
            bytearray.toHexString()
        }
    }
}