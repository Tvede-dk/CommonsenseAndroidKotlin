package com.commonsense.android.kotlin.base.extensions.collections

/**
 * Created by Kasper Tvede on 11-07-2017.
 */


/**
 * maps a progression into an array of all the values.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun IntProgression.toIntArray(): IntArray = this.toList().toIntArray()

/**
 * The length of an IntProgression
 * its the number of times steps have to be taken to get to the end.
 * or simply put, the number of times it would run in a loop.
 */
inline val IntProgression.length
    get() = ((last + step) - first) / step //+ step due to "inclusive".

