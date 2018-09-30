@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.algorithms


inline fun <T : Comparable<T>> List<T>.binarySearch(crossinline comparere: Function2<T, Int, Comparing>): Int? {
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


enum class Comparing {
    LargerThan,
    LessThan,
    Equal
}
