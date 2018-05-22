package com.commonsense.android.kotlin.base.extensions.collections

import android.support.annotation.Size
import com.commonsense.android.kotlin.base.Function1
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

@Size(min = 0)
inline fun <reified T> List<T>.repeateToSize(
        @android.support.annotation.IntRange(from = 0) size: Int): List<T> {
    if (isEmpty() || size <= 0) {
        return listOf()
    }
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


// invoke each with functionality.


//<editor-fold desc="Invoke each with ">
/**
 * Invokes each function with the given arguments
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <I1, O> Iterable<kotlin.Function1<I1, O>>.invokeEachWith(element: I1) =
        forEach { it(element) }


@Suppress("NOTHING_TO_INLINE")
inline fun <I1, I2, O> Iterable<Function2<I1, I2, O>>.invokeEachWith(
        firstElement: I1,
        secondElement: I2) =
        forEach { it(firstElement, secondElement) }


@Suppress("NOTHING_TO_INLINE")
inline fun <I1, I2, I3, O>
        Iterable<Function3<I1, I2, I3, O>>.invokeEachWith(
        firstElement: I1,
        secondElement: I2,
        thirdElement: I3) =
        forEach {
            it(firstElement,
                    secondElement,
                    thirdElement)
        }

@Suppress("NOTHING_TO_INLINE")
inline fun <I1, I2, I3, I4, O>
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


@Suppress("NOTHING_TO_INLINE")
inline fun <I1, I2, I3, I4, I5, O>
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

@Suppress("NOTHING_TO_INLINE")
inline fun <I1, I2, I3, I4, I5, I6, O>
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


@Suppress("NOTHING_TO_INLINE")
inline fun <E> Iterable<E?>.forEachNotNull(crossinline action: FunctionUnit<E>) {
    forEach { it?.let(action) }
}