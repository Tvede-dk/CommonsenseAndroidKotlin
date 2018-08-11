package com.commonsense.android.kotlin.system.logging

import com.commonsense.android.kotlin.test.*
import org.junit.jupiter.api.*

/**
 * Created by Kasper Tvede on 03-06-2018.
 * quite difficult to test the "print" method of kotlin..
 * so we test the add method.
 */
internal class PrintLoggerTest {

    @Test
    fun addToAllLoggers() {
        val prevSizeErrorSize = L.errorLoggers.size
        val prevSizeWarningSize = L.warningLoggers.size
        val prevSizeDebugSize = L.debugLoggers.size
        val prevSizeProductionSize = L.productionLoggers.size
        PrintLogger.addToAllLoggers()
        L.debugLoggers.size.assert(prevSizeDebugSize + 1, "should have added debugger logger")
        L.warningLoggers.size.assert(prevSizeWarningSize + 1, "should have added warning logger")
        L.errorLoggers.size.assert(prevSizeErrorSize + 1, "should have added error logger")
        L.productionLoggers.size.assert(prevSizeProductionSize, "should NOT have added production logger")

    }
}