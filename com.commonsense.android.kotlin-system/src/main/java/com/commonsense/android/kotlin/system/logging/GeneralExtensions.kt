package com.commonsense.android.kotlin.system.logging

import kotlin.reflect.KClass


/**
 * Created by Kasper Tvede on 18-07-2017.
 */

inline fun <T> tryAndLog(classAsTitle: KClass<*>, crossinline action: () -> T): T?
        = tryAndLog(classAsTitle.java.simpleName, action)


inline fun <T> tryAndLog(logTitle: String, crossinline action: () -> T): T? {
    return try {
        action()
    } catch (exception: Throwable) {
        L.error(logTitle, "", exception)
        null
    }
}

suspend fun <T> tryAndLogSuspend(logTitle: String, action: suspend () -> T): T? {
    return try {
        action()
    } catch (exception: Throwable) {
        L.error(logTitle, "", exception)
        null
    }
}
