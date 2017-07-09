package com.commonsense.android.kotlin.extensions.collections

import android.util.SparseIntArray

/**
 * Created by Kasper Tvede on 09-07-2017.
 */


//SPARSE ARRAY
fun SparseIntArray.clearAndSet(input: List<Pair<Int, Int>>) {
    clear()
    input.forEach { put(it.first, it.second) }
}

fun SparseIntArray.clearAndSet(input: Map<Int, Int>) {
    clear()
    input.forEach { put(it.key, it.value) }
}
