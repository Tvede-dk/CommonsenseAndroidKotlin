@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions.collections

import androidx.annotation.IntRange
import android.util.*
import kotlin.Pair
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.algorithms.Comparing
import com.commonsense.android.kotlin.base.debug.prettyStringContent

typealias SparseArrayEntryMapper<T, O> = (key: Int, item: T) -> O

/**
 * sets the content to the given list of pairs (unwraps the pair into (first  -> second)
 */
inline fun SparseIntArray.set(input: List<Pair<Int, Int>>) {
    clear()
    input.forEach { put(it.first, it.second) }
}

/**
 * sets the content to the given map (unwraps the map into (first  -> second)
 */
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
 * @receiver SparseArray<T>
 * @param maxKeyValue Int
 * @return List<SparseArrayEntry<T>>
 */
inline fun <T> SparseArray<T>.toList(
        @IntRange(from = 1) maxKeyValue: Int = Int.MAX_VALUE): List<SparseArrayEntry<T>> {
    val mapped = (0 until size())
            .map(this::keyAt)
    return if (maxKeyValue < Int.MAX_VALUE) {
        mapped.takeWhile { it <= maxKeyValue }
    } else {
        mapped
    }.mapNotNull { key -> get(key)?.let { SparseArrayEntry(key, it) } }
}

/**
 * finds the element that firstly satisfy a given condition.
 * @receiver SparseArray<T>
 * @param condition Function2<Int,T, Boolean> (int is the key, T is the value)
 * @return T?
 */
inline fun <T> SparseArray<T>.findFirst(
        crossinline condition: SparseArrayEntryMapper<T, Boolean>): SparseArrayEntry<T>? {
    val size = size()
    for (i in 0 until size) {
        val key = keyAt(i)
        val item = get(key)
        //never found something that fulfilled the condition so return that
        if (condition(key, item)) {
            return SparseArrayEntry(key, item)
        }
    }
    //we never found anything that fulfilled the condition
    return null
}


/**
 * converts each element in the sparse array using the mapper
 */
inline fun <E, U> SparseArray<E>.map(mapper: MapFunction<E, U>): List<U> {
    return mutableListOf<U>().apply {
        this@map.forEach { add(mapper(it)) }
    }
}

/**
 * Iterates over all the values this sparse array
 * @receiver SparseArray<E>
 * @param action FunctionUnit<E>
 */
inline fun <E> SparseArray<E>.forEach(action: FunctionUnit<E>) {
    for (i in 0 until size()) {
        val value = valueAt(i)
        action(value)
    }
}

/**
 * Iterates over all the keys and values this sparse array
 * @receiver SparseArray<E>
 * @param action (key: Int, value: E) -> Unit
 */
inline fun <E> SparseArray<E>.forEachKeyValue(action: (key: Int, value: E) -> Unit) {
    for (i in 0 until size()) {
        val key = keyAt(i)
        val value = valueAt(i)
        action(key, value)
    }
}

/**
 * Iterates over all the keys and values with the index in this sparse array
 * @receiver SparseArray<E>
 * @param action (key: Int, value: E, index: Int) -> Unit
 */
inline fun <E> SparseArray<E>.forEachIndexed(action: (key: Int, value: E, index: Int) -> Unit) {
    val size = size()
    for (index in 0 until size) {
        val key = keyAt(index)
        val value = valueAt(index)
        action(key, value, index)
    }
}

/**
 * Computes a pretty string representation of this SparseArray.
 * @receiver SparseArray<E>
 */
inline fun <E> SparseArray<E>.toPrettyString(): String {
    val size = size()
    val content = mutableListOf("size: $size")
    for (i in 0 until size) {
        val key = keyAt(i)
        val element = this.get(key)
        content.add("$key = $element")
    }
    return "SparseArray state: " + content.prettyStringContent()
}

/**
 * Retrieves both the key and value for a given index, if valid.
 * @receiver SparseArray<E>
 * @param index Int the index to lookup
 * @return Pair<Int, E>? null if the index is outside of range. otherwise the key and value
 */
inline fun <E> SparseArray<E>.keyValueAt(index: Int): Pair<Int, E>? = when (isIndexValid(index)) {
    true -> Pair(keyAt(index), valueAt(index))
    else -> null
}

/**
 * Tells if the given index is within this SparseArray size. (NOT KEY VALUES)
 * @receiver SparseArray<E>
 * @param index Int the raw index to validate.
 * @return Boolean true if it is a valid index , false otherwise
 */
inline fun <E> SparseArray<E>.isIndexValid(index: Int): Boolean = index >= 0 && index < size()

/**
 * Performs a binary search on the given SparseArray, using the given comparing function.
 * @receiver SparseArray<E>
 * @param compare Function2<E, Int, Comparing> a comparison function.
 * @return SparseArrayEntry<E>? null if no matching entry was found, or the found entry
 */
inline fun <E> SparseArray<E>.binarySearch(crossinline compare: Function2<E, Int, Comparing>): SparseArrayEntry<E>? {
    var start = 0
    var end = size()
    while (start < end) {
        val mid = start + (end - start) / 2
        val (key, value) = keyValueAt(mid)
                ?: throw Exception("should never happen, mid = $mid, start = $start, end = $end \n" +
                        " pretty SparseArray = ${toPrettyString()}")
        when (compare(value, mid)) {
            Comparing.LargerThan -> start = mid + 1
            Comparing.LessThan -> end = mid
            Comparing.Equal -> return SparseArrayEntry(key, value)
        }
    }
    return null
}