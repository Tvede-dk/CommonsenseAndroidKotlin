package com.commonsense.android.kotlin.extensions.collections

/**
 * Created by Kasper Tvede on 09-07-2017.
 */

fun <T> MutableSet<T>.toggleExistance(item: T) {
    if (contains(item)) {
        remove(item)
    } else {
        add(item)
    }
}