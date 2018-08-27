package com.commonsense.android.kotlin.system.logging

import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.test.*
import org.junit.*

/**
 * Created by Kasper Tvede on 17-01-2018.
 */
class LTest : BaseRoboElectricTest() {
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
        testLoggingPassThough(
                { L.isDebugLoggingAllowed = it },
                { L.debugLoggers.set(it) },
                L::debug,
                "Debugtag",
                "messageDebug",
                RuntimeException("someDebug")
        )
        testLoggingPassThough(
                { L.isWarningLoggingAllowed = it },
                { L.warningLoggers.set(it) },
                L::warning,
                "tagwarning\r\n",
                "wmessage",
                RuntimeException("warning ")
        )
        testLoggingPassThough(
                { L.isErrorLoggingAllowed = it },
                { L.errorLoggers.set(it) },
                L::error,
                "tagError",
                "EmessageRror",
                RuntimeException("warningtoError")
        )
        testLoggingPassThough(
                { L.isProductionLoggingAllowed = it },
                { L.productionLoggers.set(it) },
                L::logProd,
                "prod",
                "very usuable message",
                IllegalAccessError("prod not allowed on test")
        )
    }

    @Ignore
    @Test
    fun testViewLogging() {

    }

    private inline fun testLoggingPassThough(
            controlLoggingMethod: FunctionUnit<Boolean>,
            setLoggerMethod: FunctionUnit<LoggingFunctionType<Unit>>,
            loggerMethod: LoggingFunctionType<Unit>,
            tagToUse: String,
            messageToUse: String,
            exceptionToUse: Throwable) {
        var outerTag = ""
        var outerMessage = ""
        var outerThrowable: Throwable? = null
        setLoggerMethod { tag: String,
                          message: String,
                          stackTrace: Throwable? ->
            outerTag = tag
            outerMessage = message
            outerThrowable = stackTrace
        }
        controlLoggingMethod(false)
        loggerMethod(tagToUse, messageToUse, exceptionToUse)
        outerTag.assert("", "not allowed to run logger at this time")
        outerMessage.assert("", "not allowed to run logger at this time")
        outerThrowable.assertNull("not allowed to run logger at this time")

        controlLoggingMethod(true)
        loggerMethod(tagToUse, messageToUse, exceptionToUse)
        outerTag.assert(tagToUse, "supplied tag should get passed")
        outerMessage.assert(messageToUse, "supplied message should get passed")
        outerThrowable.assertNotNullAndEquals(exceptionToUse, "supplied exception should get passed")

    }

    @Ignore
    @Test
    fun isLoggingAllowed() {
    }

    @Ignore
    @Test
    fun isErrorLoggingAllowed() {
    }

    @Ignore
    @Test
    fun setErrorLoggingAllowed() {
    }

    @Ignore
    @Test
    fun isWarningLoggingAllowed() {
    }

    @Ignore
    @Test
    fun setWarningLoggingAllowed() {
    }

    @Ignore
    @Test
    fun isDebugLoggingAllowed() {
    }

    @Ignore
    @Test
    fun setDebugLoggingAllowed() {
    }

    @Ignore
    @Test
    fun isProductionLoggingAllowed() {
    }

    @Ignore
    @Test
    fun setProductionLoggingAllowed() {
    }

    @Ignore
    @Test
    fun getProductionLoggers() {
    }

    @Ignore
    @Test
    fun setProductionLoggers() {
    }

    @Ignore
    @Test
    fun getDebugLoggers() {
    }

    @Ignore
    @Test
    fun setDebugLoggers() {
    }

    @Ignore
    @Test
    fun getWarningLoggers() {
    }

    @Ignore
    @Test
    fun setWarningLoggers() {
    }

    @Ignore
    @Test
    fun getErrorLoggers() {
    }

    @Ignore
    @Test
    fun setErrorLoggers() {
    }

    @Ignore
    @Test
    fun error() {
    }

    @Ignore
    @Test
    fun error1() {
    }

    @Ignore
    @Test
    fun error2() {
    }

    @Ignore
    @Test
    fun warning() {
    }

    @Ignore
    @Test
    fun warning1() {
    }

    @Ignore
    @Test
    fun warning2() {
    }

    @Ignore
    @Test
    fun debug() {
    }

    @Ignore
    @Test
    fun debug1() {
    }

    @Ignore
    @Test
    fun debug2() {
    }

    @Ignore
    @Test
    fun logProd() {
    }

    @Ignore
    @Test
    fun logProd1() {
    }

    @Ignore
    @Test
    fun logProd2() {
    }


}