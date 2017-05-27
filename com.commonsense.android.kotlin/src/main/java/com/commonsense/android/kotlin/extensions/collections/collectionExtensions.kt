package com.commonsense.android.kotlin.extensions.collections

import android.util.SparseIntArray
import onTrue

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
    return if (isIndexValid(index)) {
        removeAt(index)
    } else {
        default
    }
}

fun <T> Collection<T>.isIndexValid(index: Int) = index >= 0 && index < count()

inline fun <T> MutableList<T>.findAndRemove(crossinline foundAction: (T) -> Boolean) {
    val index = this.indexOfFirst(foundAction)
    isIndexValid(index).onTrue { removeAt(index) }
}


inline fun <T> MutableList<T>.findAndRemoveAll(crossinline findAction: (T) -> Boolean): List<T> {
    val collection = this.filter(findAction)
    removeAll(collection)
    return collection
}