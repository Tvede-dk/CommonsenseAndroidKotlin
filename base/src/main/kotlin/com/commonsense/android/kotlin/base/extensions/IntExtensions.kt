@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.base.algorithms.*
import kotlin.math.*

/**
 * Created by Kasper Tvede on 13-09-2017.
 */


/**
 * Gets this int negative, if it is already negative, returns that.
 */
inline val Int.negative: Int
    get() = minOf(this, -this)


/**
 *  if this int is not 0 => returns true. false otherwise
 */
inline val Int.isNotZero: Boolean
    get() = !isZero

/**
 *  if this int is 0 => returns true. false otherwise
 */
inline val Int.isZero: Boolean
    get() = this == 0

/**
 *
 */
inline val Int.isEven: Boolean
    get() = abs(this % 2) == 0
/**
 *
 */
inline val Int.isOdd: Boolean
    get() = !isEven

/**
 * tells if we are in a range, or above / below it (equal => in range).
 * @receiver Int
 * @param from Int from this value (inclusive)
 * @param to Int to this value (inclusive)
 * @return Comparing
 */
inline fun Int.compareToRange(from: Int, to: Int): Comparing {
    //make it a bit more "defined" behavior.
    if (from > to) {
        return Comparing.LessThan
    }
    return when {
        this in (from..to) -> Comparing.Equal
        this > to -> Comparing.LargerThan
        else -> Comparing.LessThan
    }
}

