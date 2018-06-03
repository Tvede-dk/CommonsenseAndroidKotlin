package com.commonsense.android.kotlin.base.extensions

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


