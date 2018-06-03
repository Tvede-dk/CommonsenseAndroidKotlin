package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.test.assertContains
import org.junit.jupiter.api.Test

/**
 * Created by Kasper Tvede on 03-06-2018.
 * Purpose:
 */
internal class ExceptionsExtensionsKtTest {

    @Test
    fun toReadableString() {
        try {
            throw RuntimeException("some magic description")
        } catch (exception: Exception) {
            val stackTrace = exception.stackTraceToString()
            stackTrace.assertContains(this::class.java.simpleName)
            stackTrace.assertContains(this::toReadableString.name)
        }
    }
}