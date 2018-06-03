package com.commonsense.android.kotlin.base.extensions

import java.io.PrintWriter
import java.io.StringWriter

/**
 * Created by Kasper Tvede
 *
 */


/**
 *  Converts a throwable to a string with the stacktrace
 */
fun Throwable.stackTraceToString(): String =
        StringWriter().use {
            printStackTrace(PrintWriter(it))
            return it.toString()
        }


