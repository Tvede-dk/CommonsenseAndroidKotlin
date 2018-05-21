package com.commonsense.android.kotlin.base.extensions.collections

import android.support.annotation.IntRange
import android.util.SparseArray
import android.util.SparseIntArray

/**
 * Created by Kasper Tvede on 09-07-2017.
 */


/**
 * sets the content to the given list of pairs (unwraps the pair into (first  -> second)
 */
@Suppress("NOTHING_TO_INLINE")
inline fun SparseIntArray.set(input: List<Pair<Int, Int>>) {
    clear()
    input.forEach { put(it.first, it.second) }
}

/**
 * sets the content to the given map (unwraps the map into (first  -> second)
 */
@Suppress("NOTHING_TO_INLINE")
inline fun SparseIntArray.set(input: Map<Int, Int>) {
    clear()
    input.forEach { put(it.key, it.value) }
}


/**
 * Describes a sparse array entry. which is a key value pair.
 */
data class SparseArrayEntry<out T>(@IntRange(from = 0) val key: Int, val value: T)

/**
 * Converts a spareArray to a list. it will convert all keys, unless a max key is specified.
 */
//@Suppress("NOTHING_TO_INLINE")
//TODO cannot inline as that causes a crash in the kotlin compiler.
fun <T> SparseArray<T>.toList(@IntRange(from = 1) maxKeyValue: Int = Int.MAX_VALUE): List<SparseArrayEntry<T>> {
    val mapped = (0 until size())
            .map(this::keyAt)
    return if (maxKeyValue < Int.MAX_VALUE) {
        mapped.takeWhile { it <= maxKeyValue }
    } else {
        mapped
    }.mapNotNull { key -> get(key)?.let { SparseArrayEntry(key, it) } }
}