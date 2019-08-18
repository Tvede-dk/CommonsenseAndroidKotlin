@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions

/**
 * Created by Kasper Tvede on 18-04-2018.
 * Purpose:
 *
 */

/**
 * Gets this int negative, if it is already negative, returns that.
 */
inline val Double.negative: Double
    get() = minOf(this, -this)



