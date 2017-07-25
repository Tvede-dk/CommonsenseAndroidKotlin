package com.commonsense.android.kotlin.system.logging


/**
 * Created by Kasper Tvede on 18-07-2017.
 */

inline fun <T> tryAndLog(logTitle: String, crossinline action: () -> T): T? {
    return try {
        action()
    } catch (exception: Exception) {
        L.error(logTitle, "", exception)
        null
    }
}

suspend fun <T> tryAndLogSuspend(logTitle: String, action: suspend () -> T): T? {
    return try {
        action()
    } catch (exception: Exception) {
        L.error(logTitle, "", exception)
        null
    }
}
