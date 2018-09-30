@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions.collections

import com.commonsense.android.kotlin.base.algorithms.*

/**
 * Created by Kasper Tvede on 11-07-2017.
 */


/**
 * maps a progression into an array of all the values.
 */
inline fun IntProgression.toIntArray(): IntArray = this.toList().toIntArray()

/**
 * The length of an IntProgression
 * its the number of times steps have to be taken to get to the end.
 * or simply put, the number of times it would run in a loop.
 */
inline val IntProgression.length
    get() = ((last + step) - first) / step //+ step due to "inclusive".


inline fun IntArray.previousValueOr(index: Int, orElse: Int): Int {
    if (index <= 0 || index >= size) {
        return orElse
    }
    return get(index - 1)
}

inline fun IntArray.binarySearch(crossinline comparere: Function2<Int, Int, Comparing>): Int? {
    var start = 0
    var end = size
    while (start < end) {
        val mid = start + (end - start) / 2
        val item = get(mid)
        val compResult = comparere(item, mid)
        when (compResult) {
            Comparing.LargerThan -> start = mid + 1
            Comparing.LessThan -> end = mid
            Comparing.Equal -> return mid
        }
    }
    return null
}
