package com.commonsense.android.kotlin.system.logging


/**
 * Created by Kasper Tvede on 18-07-2017.
 */

inline fun tryAndLog(logTitle: String, crossinline action: () -> Unit) {
    try {
        action()
    } catch (exception: Exception) {
        L.error(logTitle, "", exception)
    }
}

suspend fun tryAndLogSuspend(logTitle: String, action: suspend () -> Unit) {
    try {
        action()
    } catch (exception: Exception) {
        L.error(logTitle, "", exception)
    }
}
