package com.commonsense.android.kotlin.extensions.collections

import android.util.SparseIntArray

/**
 * Created by Kasper Tvede on 30-09-2016.
 */


fun SparseIntArray.clearAndSet(input: List<Pair<Int, Int>>) {
    clear()
    input.forEach { put(it.first, it.second) }
}

fun SparseIntArray.clearAndSet(input: Map<Int, Int>) {
    clear()
    input.forEach { put(it.key, it.value) }
}

fun <T> MutableList<T>.removeAtOr(index: Int, default: T?): T? {
    return this.ifElse(isIndexValid(index), { removeAt(index) }, { default })
}

fun <T> MutableList<T>.isIndexValid(index: Int) = index >= 0 && index < count()

inline fun <T> MutableList<T>.findAndRemove(crossinline foundAction: (T) -> Boolean) = this.elementAtOrNull(this.indexOfFirst(foundAction))


inline fun <T> MutableList<T>.findAndRemoveAll(crossinline findAction: (T) -> Boolean): List<T> {
    val collection = this.filter(findAction)
    removeAll(collection)
    return collection
}