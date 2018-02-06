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

fun <T> List<T>.categorizeInto(@Size(min = 0) vararg filters: (T) -> Boolean): List<List<T>> {
    val result = filters.map { mutableListOf<T>() }
    this.forEach {
        filters.forEachIndexed { index, filterAccepts ->
            filterAccepts(it).onTrue { result[index].add(it) }
        }
    }
    return result
}

fun <T> List<T>.categorize(categorize: (T) -> String): Map<String, List<T>> {
    val result = sortedMapOf<String, MutableList<T>>()
    forEach {
        val key = categorize(it)
        if (result[key] == null) {
            result[key] = mutableListOf(it)
        } else {
            result[key]?.add(it)
        }
    }
    return result
}


fun <T> List<T>.repeat(repeatBy: Int): List<T> {
    val resultList = this.toMutableList()
    for (i in 0 until repeatBy) {
        resultList += this
    }
    return resultList
}

inline fun <reified T> List<T>.repeatToSize(size: Int): List<T> {
    val timesToRepeat = size / count()
    val missingItemsToCopy = size % count()
    val resultList = this.repeat(timesToRepeat - 1)
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
fun <E> List<E>.limitToSize(size: Int): List<E> = subList(0, minOf(size, this.size))


// invoke each with functionality.


//<editor-fold desc="Invoke each with ">
/**
 * Invokes each function with the given arguments
 */
fun <I1, O> Iterable<Function1<I1, O>>.invokeEachWith(element: I1) =
        forEach { it(element) }

fun <I1, I2, O> Iterable<Function2<I1, I2, O>>.invokeEachWith(
        firstElement: I1,
        secondElement: I2) =
        forEach { it(firstElement, secondElement) }


fun <I1, I2, I3, O>
        Iterable<Function3<I1, I2, I3, O>>.invokeEachWith(
        firstElement: I1,
        secondElement: I2,
        thirdElement: I3) =
        forEach {
            it(firstElement,
                    secondElement,
                    thirdElement)
        }

fun <I1, I2, I3, I4, O>
        Iterable<Function4<I1, I2, I3, I4, O>>.invokeEachWith(
        firstElement: I1,
        secondElement: I2,
        thirdElement: I3,
        forthElement: I4) =
        forEach {
            it(firstElement,
                    secondElement,
                    thirdElement,
                    forthElement)
        }


fun <I1, I2, I3, I4, I5, O>
        Iterable<Function5<I1, I2, I3, I4, I5, O>>.invokeEachWith(
        firstElement: I1,
        secondElement: I2,
        thirdElement: I3,
        forthElement: I4,
        fifthElement: I5) =
        forEach {
            it(firstElement,
                    secondElement,
                    thirdElement,
                    forthElement,
                    fifthElement)
        }

fun <I1, I2, I3, I4, I5, I6, O>
        Iterable<Function6<I1, I2, I3, I4, I5, I6, O>>.invokeEachWith(
        firstElement: I1,
        secondElement: I2,
        thirdElement: I3,
        forthElement: I4,
        fifthElement: I5,
        sixthElement: I6) =
        forEach {
            it(firstElement,
                    secondElement,
                    thirdElement,
                    forthElement,
                    fifthElement,
                    sixthElement)
        }
//</editor-fold>


fun <E> Iterable<E?>.forEachNotNull(action: FunctionUnit<E>) {
    forEach { it?.let(action) }
}