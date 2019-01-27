@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions.collections

import android.support.annotation.IntRange
import android.util.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.algorithms.*
import kotlin.Pair

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
inline fun <E, U> SparseArray<E>.map(crossinline mapper: MapFunction<E, U>): List<U> {
    return mutableListOf<U>().apply {
        this@map.forEach { add(mapper(it)) }
    }
}

/**
 *
 * @receiver SparseArray<E>
 * @param action FunctionUnit<E>
 */
inline fun <E> SparseArray<E>.forEach(crossinline action: FunctionUnit<E>) {
    for (i in 0 until size()) {
        action(valueAt(i))
    }
}
//
//inline fun <E> SparseArray<E>.binarySearch(crossinline comparere: Function2<E, Int, Comparing>): SparseArrayEntry<E>? {
//    var start = 0
//    var end = size()
//    while (start < end) {
//        val mid = start + (end - start) / 2
//        val key = keyAt(mid)
//        val item = valueAt(key)
//        val compResult = comparere(item, mid)
//        when (compResult) {
//            Comparing.LargerThan -> start = mid + 1
//            Comparing.LessThan -> end = mid
//            Comparing.Equal -> return SparseArrayEntry<E>(key, item)
//        }
//    }
//    return null
//}