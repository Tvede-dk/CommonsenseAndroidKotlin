package com.commonsense.android.kotlin.extensions.collections

import android.support.annotation.IntRange
import android.util.SparseArray

/**
 * Created by kasper on 05/07/2017.
 */
data class SparseArrayEntry<T>(@IntRange(from = 0) val key: Int, val value: T)

fun <T> SparseArray<T>.toList(@IntRange(from = 1) maxKeyValue: Int = this.size()): List<SparseArrayEntry<T>> {
    val list = mutableListOf<SparseArrayEntry<T>>()
    for (index in 0 until size()) {
        val key = keyAt(index)
        if (key > maxKeyValue) {
            break
        }
        get(key)?.let { list.add(SparseArrayEntry(key, it)) }
    }
    return list
}