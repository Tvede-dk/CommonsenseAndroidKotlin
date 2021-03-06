@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.extensions

import android.os.*

/**
 * This function call can involve synchronization, so be aware, could be slow.
 */
inline fun isInMainUIThread(): Boolean {
    return if (isApiOverOrEqualTo(23)) {
        Looper.getMainLooper().isCurrentThread
    } else {
        Thread.currentThread() === Looper.getMainLooper().thread
    }
}
