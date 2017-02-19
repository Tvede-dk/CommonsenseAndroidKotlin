package com.commonsense.android.kotlin.android.logging

import android.util.Log
import onTrue

/**
 * Created by Kasper Tvede on 19-02-2017.
 */
object L {
    val isLoggingAllowed = true
    fun error(tag: String, msg: String, exception: Throwable? = null) {
        isLoggingAllowed.onTrue { Log.e(tag, msg, exception) }
    }
}