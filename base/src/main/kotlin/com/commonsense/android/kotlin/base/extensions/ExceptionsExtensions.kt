@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions

import java.io.*

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
            it.toString()
        }


