package com.commonsense.android.kotlin.extensions.collections

import android.util.SparseArray

/**
 * Created by kasper on 05/07/2017.
 */

fun <T> SparseArray<T>.toList(length: Int = this.size()): List<T> {
    val list = mutableListOf<T>()
    for (index in 0 until minOf(length, size())) {
        get(index)?.let(list::add)
    }
    return list
}