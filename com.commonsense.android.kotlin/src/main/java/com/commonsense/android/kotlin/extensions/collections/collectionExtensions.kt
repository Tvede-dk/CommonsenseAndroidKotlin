package com.commonsense.android.kotlin.extensions.collections

import android.support.annotation.IntRange
import android.support.annotation.Size
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

@Size(min = 0)
fun <T> List<T>.categorizeInto(vararg filters: (T) -> Boolean): List<List<T>> {
    val result = filters.map { mutableListOf<T>() }
    this.forEach {
        filters.forEachIndexed { index, filterAccepts ->
            filterAccepts(it).onTrue { result[index].add(it) }
        }
    }
    return result
}

fun <T> List<T>.categorize(categorizer: (T) -> String): Map<String, List<T>> {
    val result = sortedMapOf<String, MutableList<T>>()
    forEach {
        val key = categorizer(it)
        if (result[key] == null) {
            result.put(key, mutableListOf(it))
        } else {
            result[key]?.add(it)
        }
    }
    return result
}


fun <T> List<T>.repeate(repeateBy: Int): List<T> {
    val resultList = this.toMutableList()
    for (i in 0..repeateBy - 2) {
        resultList += this
    }
    return resultList
}

inline fun <reified T> List<T>.repeateToSize(size: Int): List<T> {
    val timesToRepeate = size / count()
    val missingItemsToCopy = size % count()
    val resultList = this.repeate(timesToRepeate)
    return resultList + this.subList(0, missingItemsToCopy)
}


fun <T> MutableList<T>.replace(item: T, @IntRange(from = 0) position: Int) {
    if (isIndexValid(position)) {
        this.add(position, item)
        this.removeAt(position + 1) //the +1 : we just moved all content before the original position.
    }
}

fun <E> MutableCollection<E>.clearAndAddAll(collection: Collection<E>) {
    clear()
    addAll(collection)
}

fun <T> MutableSet<T>.toggleExistance(item: T) {
    if (contains(item)) {
        remove(item)
    } else {
        add(item)
    }
}