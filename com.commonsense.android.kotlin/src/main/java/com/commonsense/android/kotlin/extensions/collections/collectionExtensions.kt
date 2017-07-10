package com.commonsense.android.kotlin.extensions.collections

import android.support.annotation.Size
import onTrue

/**
 * Created by Kasper Tvede on 30-09-2016.
 */
fun <T> Collection<T>.isIndexValid(index: Int) = index >= 0 && index < count()

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

inline fun <T> Collection<T>.foreachUntil(action: (item: T) -> Boolean) {
    forEach {
        if (action(it)) {
            return@forEach //break all iteration.
        }
    }
}


/**
 * Returns a limited view of this list, by limiting the size of it (if the list is shorter than the limit,
 * then the result will be the lists' length).
 */
fun <E> List<E>.limitToSize(size: Int): List<E> {
    return subList(0, Math.min(size, this.size))
}