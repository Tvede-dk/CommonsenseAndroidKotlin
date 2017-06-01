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

fun <T> Collection<T>.getSafe(index: Int): T? {
    return if (this.isIndexValid(index)) {
        this.elementAt(index)
    } else {
        null
    }
}


data class CategorizationResult<out T>(val categoryA: List<T>, val categoryB: List<T>)

fun <T> List<T>.categorizeInto(filterA: (T) -> Boolean, filterB: (T) -> Boolean): CategorizationResult<T> {
    val listA = mutableListOf<T>()
    val listB = mutableListOf<T>()
    forEach {
        if (filterA(it)) {
            listA.add(it)
        } else if (filterB(it)) {
            listB.add(it)
        }
    }
    return CategorizationResult(listA, listB)
}


fun <T> List<T>.repeate(repeateBy: Int): List<T> {
    val resultList = this.toMutableList()
    for (i in 0..repeateBy - 2) {
        resultList.addAll(this)
    }
    return resultList
}