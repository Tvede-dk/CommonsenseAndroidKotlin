@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.base.patterns

import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.base.extensions.collections.*

/**
 * Created by kasper on 23/05/2017.
 * TODO remove / refactor make this more useable / accessible ?
 */

class ObserverPattern<T> {

    private var listOfListeners = mutableListOf<FunctionUnit<T>>()

    fun addListener(callback: FunctionUnit<T>) = listOfListeners.add(callback)

    fun removeListener(callback: FunctionUnit<T>) = listOfListeners.remove(callback)

    fun clearListeners() = listOfListeners.clear()

    fun notify(item: T) = listOfListeners.invokeEachWith(item)

}