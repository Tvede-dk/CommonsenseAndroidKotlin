package com.commonsense.android.kotlin.tools

import com.commonsense.android.kotlin.base.StringList

/**
 * Created by Kasper Tvede on 10-03-2018.
 * Purpose:
 *
 */


fun dumpStackTrace(): StringList = Thread.currentThread().dumpStackTrace()

fun Thread.dumpStackTrace(): StringList = stackTrace.dumpStackTrace()

fun Iterable<StackTraceElement>.dumpStackTrace(): StringList = map(StackTraceElement::dump)

fun Array<StackTraceElement>.dumpStackTrace(): StringList = map(StackTraceElement::dump)

fun StackTraceElement.dump(): String {
    return "$fileName:$methodName:$lineNumber"
}

fun dumpStackTraceForAllThreads(): Map<Thread, StringList> {
    return Thread.getAllStackTraces().mapValues { it.value.dumpStackTrace() }
}