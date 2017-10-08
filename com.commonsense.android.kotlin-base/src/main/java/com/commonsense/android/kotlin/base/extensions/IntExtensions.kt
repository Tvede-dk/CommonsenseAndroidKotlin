package com.commonsense.android.kotlin.base.extensions

/**
 * Created by Kasper Tvede on 13-09-2017.
 */

/**
 *  if this int is not 0 => returns true. false otherwise
 */
val Int.isNotZero: Boolean
    get() {
        return !isZero
    }

/**
 *  if this int is 0 => returns true. false otherwise
 */
val Int.isZero: Boolean
    get() {
        return this == 0
    }
