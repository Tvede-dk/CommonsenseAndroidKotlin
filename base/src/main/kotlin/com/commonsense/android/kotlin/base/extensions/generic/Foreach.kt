@file:Suppress("unused")

package com.commonsense.android.kotlin.base.extensions.generic

import com.commonsense.android.kotlin.base.extensions.isOdd


typealias Function2Unit<T, U> = (T, U) -> Unit
typealias Function2IndexedUnit<T, U> = (Int, T, U) -> Unit

/**
 *
 * @receiver GenericExtensions unnused.
 * @param length Int
 * @param getter Function1<Int, T>
 * @param action Function2Unit<T, T>
 */
inline fun <T> GenericExtensions.forEach2(length: Int, getter: Function1<Int, T>, action: Function2Unit<T, T>) {
    if (length.isOdd) {
        return
    }
    for (i in (0 until length step 2)) {
        val charA = getter(i)
        val charB = getter(i + 1)
        action(charA, charB)
    }
}

/**
 *
 * @receiver GenericExtensions unnused.
 * @param length Int
 * @param getter Function1<Int, T>
 * @param action Function2Unit<T, T>
 */
inline fun <T> GenericExtensions.forEach2Indexed(length: Int, getter: Function1<Int, T>, action: Function2IndexedUnit<T, T>) {
    if (length.isOdd) {
        return
    }
    for (i in (0 until length step 2)) {
        val charA = getter(i)
        val charB = getter(i + 1)
        action(i, charA, charB)
    }
}