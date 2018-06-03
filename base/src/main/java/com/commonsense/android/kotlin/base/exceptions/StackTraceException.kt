package com.commonsense.android.kotlin.base.exceptions

/**
 * Created by Kasper Tvede on 03-06-2018.
 * Purpose:
 * An exception that returns the callers stacktrace; this is intended for some kind of error
 * where the developer who sees the error need to know the stack trace (eventually)
 */
class StackTraceException : Exception {

    private val callingStackTrace: Array<StackTraceElement>

    constructor() : super() {
        //drops this constructor call
        callingStackTrace = Thread.currentThread().stackTrace.drop(1).toTypedArray()
    }

    override fun getLocalizedMessage(): String {
        return "Stacktrace"
    }

    override fun getStackTrace(): Array<StackTraceElement> {
        return callingStackTrace
    }
}