@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.base.algorithms.*

/**
 * Created by Kasper Tvede on 13-09-2017.
 */


/**
 * Gets this int negative, if it is already negative, returns that.
 */
inline val Int.negative: Int
    get() = Math.min(this, -this)


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
    get() = this % 2 == 0
/**
 *
 */
inline val Int.isOdd: Boolean
    get() = this % 2 == 1


/**
 *
 * @receiver Int
 * @param from Int
 * @param to Int
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

