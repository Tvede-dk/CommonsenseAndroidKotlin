@file:Suppress("NOTHING_TO_INLINE")

package com.commonsense.android.kotlin.base.extensions.collections

import android.util.SparseIntArray
import com.commonsense.android.kotlin.base.algorithms.Comparing

/**
 * Performs a binary search on the given SparseIntArray, using the given comparing function.
 * @receiver SparseIntArray
 * @param compare (key: Int, value: Int, Index: Int) -> Comparing
 * @return Pair<Int,Int>? null if no matching entry was found, or the key, value pair
 */
inline fun SparseIntArray.binarySearch(
        crossinline compare: (key: Int, value: Int, Index: Int) -> Comparing
): Pair<Int, Int>? {
    var start = 0
    var end = size()
    while (start < end) {
        val mid = start + (end - start) / 2
        val pair = keyValueAt(mid)
                ?: throw Exception("should never happen, mid = $mid, start = $start, end = $end \n" +
                        " pretty SparseIntArray = ${toPrettyString()}")
        when (compare(pair.first, pair.second, mid)) {
            Comparing.LargerThan -> start = mid + 1
            Comparing.LessThan -> end = mid
            Comparing.Equal -> return pair
        }
    }
    return null
}


/**
 * Retrieves both the key and value for a given index, if valid.
 * @receiver SparseIntArray
 * @param index Int the index to lookup
 * @return Pair<Int, Int>? null if the index is outside of range. otherwise the key and value
 */
inline fun SparseIntArray.keyValueAt(index: Int): Pair<Int, Int>? = when (isIndexValid(index)) {
    true -> Pair(keyAt(index), valueAt(index))
    else -> null
}

/**
 * Tells if the given index is within this SparseIntArray size. (NOT KEY VALUES)
 * @receiver SparseIntArray
 * @param index Int the raw index to validate.
 * @return Boolean true if it is a valid index , false otherwise
 */
inline fun SparseIntArray.isIndexValid(index: Int): Boolean = index >= 0 && index < size()

//TODO improve me. maybe.
fun SparseIntArray.toPrettyString(): String {
    return toString()
}

/**
 *
 * @receiver SparseIntArray
 * @param index Int
 * @param defaultValue Int
 * @return Int
 */
inline fun SparseIntArray.previousValueOr(index: Int, defaultValue: Int): Int {
    val indexToConsider = index - 1
    return if (isIndexValid(indexToConsider)) {
        valueAt(indexToConsider)
    } else {
        defaultValue
    }
}