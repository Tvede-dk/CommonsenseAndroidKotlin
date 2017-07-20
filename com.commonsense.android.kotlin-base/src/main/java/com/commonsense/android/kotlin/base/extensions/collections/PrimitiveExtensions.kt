package com.commonsense.android.kotlin.base.extensions.collections

import com.commonsense.android.kotlin.base.extensions.isNotNull
import com.commonsense.android.kotlin.base.extensions.isNull

/**
 * Created by Kasper Tvede on 06-12-2016.
 */

/**
 * performs the action if the boolean is true.
 */
inline fun Boolean.onTrue(crossinline action: () -> Unit): Boolean {
    if (this) {
        action()
    }
    return this
}


fun <T> Boolean.map(ifTrue: T, ifFalse: T): T {
    return if (this) {
        ifTrue
    } else {
        ifFalse
    }
}


inline fun Any?.ifNull(crossinline action: () -> Unit) {
    this.isNull.ifTrue(action)
}

inline fun Any?.ifNotNull(crossinline action: () -> Unit) {
    this.isNotNull.ifTrue(action)
}

/**
 * Makes a more "elegant" sentence for some expressions, same as "com.commonsense.android.kotlin.base.extensions.collections.onTrue"
 */
inline fun Boolean.ifTrue(crossinline action: () -> Unit): Boolean = onTrue(action)

/**
 * performs the action if the boolean is false.
 */
inline fun Boolean.onFalse(crossinline action: () -> Unit): Boolean {
    if (!this) {
        action()
    }
    return this
}


/**
 * Makes a more "elegant" sentence for some expressions, same as "com.commonsense.android.kotlin.base.extensions.collections.onTrue"
 */
inline fun Boolean.ifFalse(crossinline action: () -> Unit): Boolean = onFalse(action)

inline fun <reified T : kotlin.Enum<T>> valueOfOrUnsafe(type: String?, orValue: T?): T? =
        java.lang.Enum.valueOf(T::class.java, type) ?: orValue

inline fun <reified T : kotlin.Enum<T>> enumFromOr(type: String?, orValue: T): T =
        java.lang.Enum.valueOf(T::class.java, type) ?: orValue

inline fun <reified T : kotlin.Enum<T>> enumFromOrNull(type: String?): T? =
        java.lang.Enum.valueOf(T::class.java, type) ?: null


//TODO test regarding steps.
val IntRange.length
    get() = (last - start) + 1 //+1 since start is inclusive.

val IntRange.largest
    get() = maxOf(last, start)