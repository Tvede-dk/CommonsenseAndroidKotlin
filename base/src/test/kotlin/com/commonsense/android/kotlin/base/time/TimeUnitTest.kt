package com.commonsense.android.kotlin.base.time

import com.commonsense.android.kotlin.base.time.TimeUnit.*
import com.commonsense.android.kotlin.test.*
import org.junit.*
import org.junit.jupiter.api.Test

class TimeUnitTest {

    @Test
    fun getPrefix() {
        NanoSeconds(0).prefix.assert("ns")
        MilliSeconds(0).prefix.assert("ms")
        Seconds(0).prefix.assert("s")
        Minutes(0).prefix.assert("m")
        Hours(0).prefix.assert("h")
        Days(0).prefix.assert("d")
    }

    @Test
    fun toMilliSeconds() {
        NanoSeconds(0).toMilliSeconds().value.assert(0)
        MilliSeconds(0).toMilliSeconds().value.assert(0)
        Seconds(0).toMilliSeconds().value.assert(0)
        Minutes(0).toMilliSeconds().value.assert(0)
        Hours(0).toMilliSeconds().value.assert(0)
        Days(0).toMilliSeconds().value.assert(0)


        NanoSeconds(1).toMilliSeconds().value
                .assert(0,
                        "1 nanosecond is too small" +
                                " to become a meaningful milliseconds (whole number)")
        MilliSeconds(1).toMilliSeconds().value.assert(1)
        Seconds(1).toMilliSeconds().value.assert(1000)
        Minutes(1).toMilliSeconds().value.assert(1000 * 60)
        Hours(1).toMilliSeconds().value.assert(1000 * 60 * 60)
        Days(1).toMilliSeconds().value.assert(1000 * 60 * 60 * 24)
        NanoSeconds(1_000_000).toMilliSeconds().value.assert(1)

    }

    @Test
    fun testToString() {
        NanoSeconds(0).toString().assert("0 ns")
        MilliSeconds(0).toString().assert("0 ms")
        Seconds(0).toString().assert("0 s")
        Minutes(0).toString().assert("0 m")
        Hours(0).toString().assert("0 h")
        Days(0).toString().assert("0 d")
    }

    @Test
    fun getValue() {
        NanoSeconds(0).value.assert(0)
        MilliSeconds(0).value.assert(0)
        Seconds(0).value.assert(0)
        Minutes(0).value.assert(0)
        Hours(0).value.assert(0)
        Days(0).value.assert(0)
    }

}