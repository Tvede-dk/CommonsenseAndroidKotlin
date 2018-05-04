package com.commonsense.android.kotlin.base.extensions

/**
 * Created by Kasper Tvede on 17-04-2018.
 * Purpose:
 *
 */

/**
 * Gets this long negative, if it is already negative, returns that.
 */
val Long.negative: Long
    get() = Math.min(this, -this)




/**
 *  if this int is not 0 => returns true. false otherwise
 */
val Long.isNotZero: Boolean
    get() {
        return !isZero
    }

/**
 *  if this int is 0 => returns true. false otherwise
 */
val Long.isZero: Boolean
    get() {
        return this == 0L
    }

