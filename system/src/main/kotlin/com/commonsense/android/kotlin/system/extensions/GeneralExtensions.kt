@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.extensions

import android.os.*
import com.commonsense.android.kotlin.base.*

/**
 * Created by Kasper Tvede on 30-05-2017.
 */


inline fun isApiOverOrEqualTo(level: Int): Boolean {
    return Build.VERSION.SDK_INT >= level
}

inline fun isApiGreaterThan(level: Int): Boolean {
    return Build.VERSION.SDK_INT > level
}

inline fun isApiLowerThan(level: Int): Boolean {
    return Build.VERSION.SDK_INT < level
}

/**
 * The api level of this device.
 * This however is incompatible with lint......
 */
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
