@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.extensions.collections

import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.*

/**
 * Created by Kasper Tvede on 08-10-2017.
 */


inline fun <K, V> Map<K, V>.forEachIndexed(
        crossinline action: (Map.Entry<K, V>, Int) -> Unit) {
    var i = 0
    forEach {
        action(it, i)
        i += 1
    }
}

/**
 * inserts a new value for the given key if non exists, and or runs the useValue callback; if there is a value, it will be used for the useValue.
 */
inline fun <reified K, reified V> MutableMap<K, V>.createOrUse(
        key: K,
        creator: EmptyFunctionResult<V>,
        useValue: FunctionUnit<V>) {
    val valueByKey = get(key)
    val toUse = valueByKey ?: creator()
    if (valueByKey.isNull) {
        put(key, toUse)
    }
    useValue(toUse)
}