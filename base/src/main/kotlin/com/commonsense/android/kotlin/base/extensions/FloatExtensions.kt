@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions

/**
 * Created by Kasper Tvede on 26-08-2017.
 */
/**
 * Gets this float negative, if it is already negative, returns that.
 */
inline val Float.negative: Float
    get() = minOf(this, -this)

/**
 * Compares two floats with a delta, since floats are not precise.
 *
 */
inline fun Float.equals(otherFloat: Float, delta: Float): Boolean =
        this >= otherFloat - delta && this <= otherFloat + delta

/**
 * Tells if this float value is zero (or more correctly, close to, since floats are not precise)
 */
inline fun Float.isZero(tolerance: Float = 0.1f): Boolean {
    return this < tolerance && this > tolerance.negative
}
