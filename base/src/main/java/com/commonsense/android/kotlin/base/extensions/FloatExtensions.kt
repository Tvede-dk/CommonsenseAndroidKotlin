package com.commonsense.android.kotlin.base.extensions

/**
 * Created by Kasper Tvede on 26-08-2017.
 */

/**
 * Gets this float negative, if it is already negative, returns that.
 */
val Float.negative: Float
    get() = Math.min(this, -this)

fun Float.equals(otherFloat: Float, delta: Float): Boolean = this >= otherFloat - delta && this <= otherFloat + delta

fun Float.isZero(tolerance: Float = 0.1f): Boolean {
    return this < tolerance && this > tolerance.negative
}
