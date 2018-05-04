package com.commonsense.android.kotlin.system.extensions

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build
import android.support.annotation.RequiresApi
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.EmptyFunctionResult
import com.commonsense.android.kotlin.base.EmptyReceiver

/**
 * Created by Kasper Tvede on 30-05-2017.
 */

@Suppress("NOTHING_TO_INLINE")
inline fun isApiEqualOrGreater(level: Int): Boolean {
    return Build.VERSION.SDK_INT >= level
}

@Suppress("NOTHING_TO_INLINE")
inline fun isApiGreaterThan(level: Int): Boolean {
    return Build.VERSION.SDK_INT > level
}

@Suppress("NOTHING_TO_INLINE")
inline fun isApiLowerThan(level: Int): Boolean {
    return Build.VERSION.SDK_INT < level
}

/**
 * The api level of this device.
 * This however is incompatible with lint......
 */
@Suppress("NOTHING_TO_INLINE")
inline val apiLevel: Int
    get () = Build.VERSION.SDK_INT

/**
 * General functions applying the given action iff the api level criteria is met
 * works with lint.
 */
inline fun ifApiOver(api: Int,
                     crossinline action: EmptyFunction) {
    if (Build.VERSION.SDK_INT > api) {
        action()
    }
}

inline fun ifApiLowerThan(api: Int,
                          crossinline action: EmptyFunction) {
    if (Build.VERSION.SDK_INT < api) {
        action()
    }
}

inline fun <T> ifApiIsEqualOrGreater(api: Int,
                                     crossinline action: EmptyFunctionResult<T>): T? {
    if (Build.VERSION.SDK_INT >= api) {
        return action()
    }
    return null
}

/**
 * Extensions on any.
 * Lint does not work with this.
 */
inline fun <T : Any> T.applyIfApiGreater(api: Int,
                                         crossinline action: EmptyReceiver<T>): T {
    if (Build.VERSION.SDK_INT > api) {
        action(this)
    }
    return this
}


inline fun <T> T.applyIfApiLowerThan(api: Int,
                                     crossinline action: EmptyReceiver<T>): T = apply {
    if (Build.VERSION.SDK_INT < api) {
        action(this)
    }
}

inline fun <T> T.applyIfApiEqualOrGreater(api: Int,
                                          crossinline action: EmptyReceiver<T>): T = apply {
    if (Build.VERSION.SDK_INT >= api) {
        action(this)
    }
}
