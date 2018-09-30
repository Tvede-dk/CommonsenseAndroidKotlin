@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")
package com.commonsense.android.kotlin.base.extensions.collections

/**
 * Created by Kasper Tvede on 09-07-2017.
 */

/**
 * Toggles whenever a set contains the given item;
 * if the set contains the item it will be removed.
 * if it does not contain the item, the item will be inserted.
 */
inline fun <T> MutableSet<T>.toggleExistence(item: T) {
    setExistence(item, !contains(item))
}

/**
 * like toggle, except you control the action by the "shouldExists";
 * if that is true, then the element is added, if false the element is removed.
 */
inline fun <T> MutableSet<T>.setExistence(item: T, shouldExists: Boolean) {
    if (shouldExists) {
        add(item)
    } else {
        remove(item)
    }
}