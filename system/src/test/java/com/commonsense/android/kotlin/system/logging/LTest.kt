package com.commonsense.android.kotlin.system.logging

import com.commonsense.android.kotlin.test.assert
import org.junit.Test

/**
 * Created by Kasper Tvede on 17-01-2018.
 */
class LTest {
    /**
     * Tests the logging controls (turning  them on off )
     */
    @Test
    fun testLoggingSwitches() {

        //manually turn all on.
        L.apply {
            isProductionLoggingAllowed = true
            isDebugLoggingAllowed = true
            isErrorLoggingAllowed = true
            isWarningLoggingAllowed = true
        }
        assertLoggingAllowedStates(
                true,
                true,
                true,
                true)

        //make sure all are turned on

        //turn all off.
        L.isLoggingAllowed(false)
        //make sure all are turned off.
        assertLoggingAllowedStates(
                false,
                false,
                false,
                false)


        L.isLoggingAllowed(true)
        assertLoggingAllowedStates(
                true,
                true,
                true,
                true)
        //make sure all are turned on.

        L.isLoggingAllowed(false)
        assertLoggingAllowedStates(
                false,
                false,
                false,
                false)
        L.isProductionLoggingAllowed = true
        assertLoggingAllowedStates(
                true,
                false,
                false,
                false)

        L.isWarningLoggingAllowed = true

        L.isProductionLoggingAllowed = false

        assertLoggingAllowedStates(
                false,
                false,
                true,
                false)
        //make sure they are not dependent on each other.


    }

    /**
     * Asserts the state on the L. properties.
     */
    private fun assertLoggingAllowedStates(assertProd: Boolean,
                                           assertError: Boolean,
                                           assertWarning: Boolean,
                                           assertDebug: Boolean,
                                           optionalMessage: String = "") {
        L.apply {
            isProductionLoggingAllowed.assert(assertProd, optionalMessage)
            isErrorLoggingAllowed.assert(assertError, optionalMessage)
            isWarningLoggingAllowed.assert(assertWarning, optionalMessage)
            isDebugLoggingAllowed.assert(assertDebug, optionalMessage)
        }
    }

    /**
     * testing the state is one important thing,
     * but testing whenever the state is obeyed is another thing
     */
    @Test
    fun testLoggingContentControl() {
        L.isLoggingAllowed(false)
        L.warningLoggers.clear()

    }

    private fun removeAllLoggers() = L.apply{
        warningLoggers.clear()
        debugLoggers.clear()
        errorLoggers.clear()
    }

}