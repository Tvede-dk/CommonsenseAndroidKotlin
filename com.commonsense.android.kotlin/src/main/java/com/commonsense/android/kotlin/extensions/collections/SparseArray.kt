package com.commonsense.android.kotlin.extensions.collections

import android.support.annotation.IntRange
import android.util.SparseArray

/**
 * Created by kasper on 05/07/2017.
 */
data class SparseArrayEntry<out T>(@IntRange(from = 0) val key: Int, val value: T)

fun <T> SparseArray<T>.toList(@IntRange(from = 1) maxKeyValue: Int = Int.MAX_VALUE): List<SparseArrayEntry<T>> {
    return (0 until size())
            .map(this::keyAt)
            .takeWhile { it <= maxKeyValue }
            .mapNotNull { key -> get(key)?.let { SparseArrayEntry(key, it) } }
}