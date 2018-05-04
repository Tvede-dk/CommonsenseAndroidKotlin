package com.commonsense.android.kotlin.base.extensions.collections

import com.commonsense.android.kotlin.base.AsyncEmptyFunction
import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.EmptyFunctionResult
import com.commonsense.android.kotlin.base.FunctionUnit
import com.commonsense.android.kotlin.base.extensions.isNull

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
suspend fun Boolean.onTrueAsync(action: AsyncEmptyFunction): Boolean {
    if (this) {
        action()
    }
    return this
}


/**
 * Maps a boolean into a value.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> Boolean.map(ifTrue: T, ifFalse: T): T = if (this) {
    ifTrue
} else {
    ifFalse
}

inline fun <T> Boolean.mapLazy(crossinline ifTrue: EmptyFunctionResult<T>,
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
suspend fun Boolean.ifTrueAsync(action: AsyncEmptyFunction): Boolean = onTrueAsync(action)

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
inline fun Boolean.ifFalse(crossinline action: EmptyFunction): Boolean = onFalse(action)

inline fun <reified T : kotlin.Enum<T>> enumFromOrNull(name: String?): T? {
    return enumValues<T>().find { it.name == name }
}

inline fun <reified T : kotlin.Enum<T>> enumFromOr(name: String?, orElse: T): T {
    return enumFromOrNull<T>(name) ?: orElse
}

val IntRange.length
    get() = (last - start) + 1 //+1 since start is inclusive.

val IntRange.largest
    get() = maxOf(last, start)