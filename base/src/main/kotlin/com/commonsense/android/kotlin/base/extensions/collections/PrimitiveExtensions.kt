@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")
package com.commonsense.android.kotlin.base.extensions.collections

import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.*

/**
 * Created by Kasper Tvede on 06-12-2016.
 */

/**
 * performs the action if the boolean is true.
 */
inline fun Boolean.onTrue(crossinline action: EmptyFunction): Boolean {
    if (this) {
        action()
    }
    return this
}

/**
 *  performs the action if the boolean is true.
 */
suspend inline fun Boolean.onTrueAsync(crossinline action: AsyncEmptyFunction): Boolean {
    if (this) {
        action()
    }
    return this
}


/**
 * Maps a boolean into a value.
 */
inline fun <T> Boolean.map(ifTrue: T, ifFalse: T): T = if (this) {
    ifTrue
} else {
    ifFalse
}

/**
 * Maps lazily the given parameters.
 * since its inline, then the code would be as if you wrote the "if else statement"
 * and then only did the computation iff that branch was chosen.
 *
 */
inline fun <T> Boolean.mapLazy(crossinline ifTrue: EmptyFunctionResult<T>,
                               crossinline ifFalse: EmptyFunctionResult<T>): T =
        if (this) {
            ifTrue()
        } else {
            ifFalse()
        }

/**
 * Maps lazily the given parameters.
 * since its inline, then the code would be as if you wrote the "if else statement"
 * and then only did the computation iff that branch was chosen.
 * Works with suspend functions
 */
suspend inline fun <T> Boolean.mapLazyAsync(crossinline ifTrue: AsyncEmptyFunctionResult<T>,
                                            crossinline ifFalse: EmptyFunctionResult<T>): T =
        if (this) {
            ifTrue()
        } else {
            ifFalse()
        }


/**
 * performs the given action if we are null
 */
inline fun Any?.ifNull(crossinline action: EmptyFunction) {
    this.isNull.ifTrue(action)
}

/**
 * performs the given action, if we are not null
 */
inline fun <T> T?.ifNotNull(crossinline action: FunctionUnit<T>) {
    this?.let { action(it) }
}

/**
 * Makes a more "elegant" sentence for some expressions, same as "com.commonsense.android.kotlin.com.commonsense.android.kotlin.base.onTrue"
 */
inline fun Boolean.ifTrue(crossinline action: EmptyFunction): Boolean = onTrue(action)

/**
 * Makes a more "elegant" sentence for some expressions, same as "com.commonsense.android.kotlin.com.commonsense.android.kotlin.base.onTrue"
 */
suspend inline fun Boolean.ifTrueAsync(crossinline action: AsyncEmptyFunction): Boolean =
        onTrueAsync(action)

/**
 * performs the action if the boolean is false.
 */
inline fun Boolean.onFalse(crossinline action: EmptyFunction): Boolean {
    if (!this) {
        action()
    }
    return this
}


/**
 * Makes a more "elegant" sentence for some expressions, same as "com.commonsense.android.kotlin.com.commonsense.android.kotlin.base.onTrue"
 */
inline fun Boolean.ifFalse(crossinline action: EmptyFunction): Boolean =
        onFalse(action)

inline fun <reified T : kotlin.Enum<T>> enumFromOrNull(name: String?): T? {
    return enumValues<T>().find { it.name == name }
}

inline fun <reified T : kotlin.Enum<T>> enumFromOr(name: String?, orElse: T): T {
    return enumFromOrNull<T>(name) ?: orElse
}

inline val IntRange.length: Int
    get() = (last - start) + 1 //+1 since start is inclusive.

inline val IntRange.largest: Int
    get() = maxOf(last, start)