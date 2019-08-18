@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions.collections

import com.commonsense.android.kotlin.base.*

/**
 * Created by Kasper Tvede on 09-07-2017.
 */

inline fun <T> MutableList<T>.findAndRemove(crossinline foundAction: FunctionBoolean<T>) {
    val index = this.indexOfFirst(foundAction)
    isIndexValid(index).onTrue { removeAt(index) }
}


inline fun <T> MutableList<T>.findAndRemoveAll(crossinline findAction: FunctionBoolean<T>): List<T> {
    val collection = this.filter(findAction)
    removeAll(collection)
    return collection
}


fun <T> MutableList<T>.replace(item: T, @androidx.annotation.IntRange(from = 0) position: Int) {
    if (isIndexValid(position)) {
        this.add(position, item)
        this.removeAt(position + 1) //the +1 : we just moved all content before the original position.
    }
}

/**
 * Clears the collection and add's the given collection
 */
fun <E> MutableCollection<E>.set(collection: Collection<E>) {
    clear()
    addAll(collection)
}

/**
 * Clears the collection and add's the given element
 */
fun <E> MutableCollection<E>.set(item: E) {
    clear()
    add(item)
}

/**
 * returns true iff all could be removed
 */
fun <T> MutableList<T>.removeAll(intRange: kotlin.ranges.IntRange): Boolean {
    if (intRange.first >= size || intRange.last >= size) {
        return false
    }
    intRange.forEach { _ -> this.removeAt(intRange.first) }
    return true
}


fun <T> MutableList<T>.removeAtOr(index: Int, default: T?): T? = if (isIndexValid(index)) {
    removeAt(index)
} else {
    default
}
