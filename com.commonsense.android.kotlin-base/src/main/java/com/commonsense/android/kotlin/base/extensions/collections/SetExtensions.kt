package com.commonsense.android.kotlin.base.extensions.collections

/**
 * Created by Kasper Tvede on 09-07-2017.
 */

/**
 * Toggles whenever a set contains the given item;
 * if the set contains the item it will be removed.
 * if it does not contain the item, the item will be inserted.
 */
fun <T> MutableSet<T>.toggleExistance(item: T) {
    if (contains(item)) {
        remove(item)
    } else {
        add(item)
    }
}