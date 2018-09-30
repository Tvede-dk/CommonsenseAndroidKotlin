@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.logging

import com.commonsense.android.kotlin.base.extensions.*

/**
 * Created by Kasper Tvede on 03-06-2018.
 * Purpose:
 * A simple Logger that prints the content rather than for example use the android Logger
 */

object PrintLogger {
    fun printLog(tag: String, message: String, throwable: Throwable?) {
        println("$tag:\r\n$message\r\n${throwable?.stackTraceToString()}")

    }

    fun addToAllLoggers() {
        L.warningLoggers.add(::printLog)
        L.errorLoggers.add(::printLog)
        L.debugLoggers.add(::printLog)
    }

}
