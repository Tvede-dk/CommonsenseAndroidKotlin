package com.commonsense.android.kotlin.base.extensions.collections

import android.util.SparseIntArray

/**
 * Created by Kasper Tvede on 09-07-2017.
 */


/**
 * sets the content to the given list of pairs (unwraps the pair into (first  -> second)
 */
fun SparseIntArray.set(input: List<Pair<Int, Int>>) {
    clear()
    input.forEach { put(it.first, it.second) }
}

/**
 * sets the content to the given map (unwraps the map into (first  -> second)
 */
fun SparseIntArray.set(input: Map<Int, Int>) {
    clear()
    input.forEach { put(it.key, it.value) }
}

