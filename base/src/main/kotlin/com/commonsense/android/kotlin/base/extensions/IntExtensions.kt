package com.commonsense.android.kotlin.base.extensions

import android.support.annotation.IntRange
import com.commonsense.android.kotlin.base.algorithms.Comparing

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
    get() {
        return !isZero
    }

/**
 *  if this int is 0 => returns true. false otherwise
 */
inline val Int.isZero: Boolean
    get() {
        return this == 0
    }


/**
 *
 * @receiver Int
 * @param from Int
 * @param to Int
 * @return Comparing
 */
@Suppress("NOTHING_TO_INLINE")
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