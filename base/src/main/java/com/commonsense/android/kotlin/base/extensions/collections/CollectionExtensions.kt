package com.commonsense.android.kotlin.base.extensions.collections

import android.support.annotation.Size
import com.commonsense.android.kotlin.base.FunctionResult
import com.commonsense.android.kotlin.base.FunctionUnit

/**
 * Created by Kasper Tvede on 30-09-2016.
 */

@Suppress("NOTHING_TO_INLINE")
inline fun Collection<*>.isIndexValid(index: Int) =
        index >= 0 && index < count()


@Suppress("NOTHING_TO_INLINE")
inline fun Collection<*>.isIndexValidForInsert(index: Int) =
        index >= 0 && index <= count()


@Suppress("NOTHING_TO_INLINE")
inline fun <T> Collection<T>.getSafe(index: Int): T? =
        if (this.isIndexValid(index)) {
            elementAt(index)
        } else {
            null
        }

@Suppress("NOTHING_TO_INLINE")
inline fun <T> List<T>.categorizeInto(@Size(min = 0) vararg filters: (T) -> Boolean): List<List<T>> {
    val result = filters.map { mutableListOf<T>() }
    this.forEach {
        filters.forEachIndexed { index, filterAccepts ->
            filterAccepts(it).onTrue { result[index].add(it) }
        }
    }
    return result
}

inline fun <T> List<T>.categorize(
        crossinline categorizer: FunctionResult<T, String>): Map<String, List<T>> {
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


@Suppress("NOTHING_TO_INLINE")
inline fun <T> List<T>.repeate(repeateBy: Int): List<T> {
    val resultList = this.toMutableList()
    for (i in 0 until repeateBy) {
        resultList += this
    }
    return resultList
}

inline fun <reified T> List<T>.repeateToSize(size: Int): List<T> {
    val timesToRepeate = size / count()
    val missingItemsToCopy = size % count()
    val resultList = this.repeate(timesToRepeate - 1)
    return resultList + this.subList(0, missingItemsToCopy)
}


@Suppress("NOTHING_TO_INLINE")
inline fun Collection<*>.isRangeValid(intRange: IntRange): Boolean =
        (intRange.start >= 0 && intRange.endInclusive < size)

@Suppress("NOTHING_TO_INLINE")
inline fun <T> List<T>.subList(intRange: IntRange): List<T> =
        subList(intRange.start, intRange.endInclusive)

/**
 * Returns a limited view of this list, by limiting the size of it (if the list is shorter than the limit,
 * then the result will be the lists' com.commonsense.android.kotlin.base.extensions.getLength).
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <E> List<E>.limitToSize(size: Int): List<E> =
        subList(0, minOf(size, this.size))

/**
 * Invokes each function with the given argument
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <E> Iterable<FunctionUnit<E>>.invokeEachWith(element: E) =
        forEach { it(element) }


inline fun <E> Iterable<E?>.forEachNotNull(crossinline action: FunctionUnit<E>) {
    forEach { it?.let(action) }
}