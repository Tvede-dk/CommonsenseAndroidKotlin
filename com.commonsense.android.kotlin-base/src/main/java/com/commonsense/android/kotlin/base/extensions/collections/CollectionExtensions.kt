package com.commonsense.android.kotlin.base.extensions.collections

import android.support.annotation.Size
import com.commonsense.android.kotlin.base.FunctionUnit

/**
 * Created by Kasper Tvede on 30-09-2016.
 */

fun Collection<*>.isIndexValid(index: Int) = index >= 0 && index < count()

fun Collection<*>.isIndexValidForInsert(index: Int) = index >= 0 && index <= count()

fun <T> Collection<T>.getSafe(index: Int): T? = if (this.isIndexValid(index)) {
    elementAt(index)
} else {
    null
}

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


fun Collection<*>.isRangeValid(intRange: IntRange): Boolean =
        (intRange.start >= 0 && intRange.endInclusive < size)

fun <T> List<T>.subList(intRange: IntRange): List<T> =
        subList(intRange.start, intRange.endInclusive)

/**
 * Returns a limited view of this list, by limiting the size of it (if the list is shorter than the limit,
 * then the result will be the lists' com.commonsense.android.kotlin.base.extensions.getLength).
 */
fun <E> List<E>.limitToSize(size: Int): List<E>
        = subList(0, minOf(size, this.size))

/**
 * Invokes each function with the given argument
 */
fun <E> Iterable<FunctionUnit<E>>.invokeEachWith(element: E) = forEach { it(element) }


fun <E> Iterable<E?>.forEachNotNull(action: FunctionUnit<E>) {
    forEach { it?.let(action) }
}