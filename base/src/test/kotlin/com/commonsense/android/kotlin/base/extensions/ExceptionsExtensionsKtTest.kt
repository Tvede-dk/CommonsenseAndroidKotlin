package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.test.*
import org.junit.jupiter.api.*

/**
 * Created by Kasper Tvede on 03-06-2018.
 * Purpose:
 */
internal class ExceptionsExtensionsKtTest {

    @Test
    fun stackTraceToString() {
        try {
            throw RuntimeException("some magic description")
        } catch (exception: Exception) {
            val stackTrace = exception.stackTraceToString()
            stackTrace.assertContains(this::class.java.simpleName)
            stackTrace.assertContains(this::stackTraceToString.name)
        }
    }
}