package com.commonsense.android.kotlin.base.extensions.collections

import android.util.SparseArray
import com.commonsense.android.kotlin.base.FunctionUnit
import com.commonsense.android.kotlin.base.MapFunction

/**
 * Created by Kasper Tvede on 08-10-2017.
 */


fun <K, V> Map<K, V>.forEachIndexed(action: (Map.Entry<K, V>, Int) -> Unit) {
    var i = 0
    forEach {
        action(it, i)
        i += 1
    }
}

/**
 * converts each element in the sparse array using the mapper
 */
fun <E, U> SparseArray<E>.map(mapper: MapFunction<E, U>): List<U> {
    return mutableListOf<U>().apply {
        this@map.forEach { add(mapper(it)) }
    }
}

fun <E> SparseArray<E>.forEach(action: FunctionUnit<E>) {
    for (i in 0 until size()) {
        action(valueAt(i))
    }
}

