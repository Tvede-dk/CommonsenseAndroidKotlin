package com.commonsense.android.kotlin.system.logging

import android.content.ComponentCallbacks
import android.util.Log
import android.view.View
import com.commonsense.android.kotlin.base.extensions.collections.onTrue

/**
 * Created by Kasper Tvede on 19-02-2017.
 */
object L {
    var isLoggingAllowed = true
    fun error(tag: String, msg: String, exception: Throwable? = null) {
        isLoggingAllowed.onTrue { Log.e(tag, msg, exception) }
    }

    fun debug(tag: String, msg: String, exception: Throwable? = null) {
        isLoggingAllowed.onTrue { Log.d(tag, msg, exception) }
    }

    fun warning(tag: String, message: String, exception: Throwable? = null) {
        isLoggingAllowed.onTrue { Log.w(tag, message, exception) }
    }
}

fun ComponentCallbacks.logError(message: String, exception: Throwable? = null) {
    L.error(this.javaClass.simpleName, message, exception)
}

fun ComponentCallbacks.logWarning(message: String, exception: Throwable? = null) {
    L.warning(this.javaClass.simpleName, message, exception)
}

fun ComponentCallbacks.logDebug(message: String, exception: Throwable? = null) {
    L.debug(this.javaClass.simpleName, message, exception)
}

fun View.logDebug(message: String, exception: Throwable? = null) {
    L.debug(this.javaClass.simpleName, message, exception)
}

fun View.logWarning(message: String, exception: Throwable? = null) {
    L.warning(this.javaClass.simpleName, message, exception)
}

fun View.logError(message: String, exception: Throwable? = null) {
    L.error(this.javaClass.simpleName, message, exception)
}