package com.commonsense.android.kotlin.base.patterns

import com.commonsense.android.kotlin.base.FunctionUnit
import com.commonsense.android.kotlin.base.extensions.collections.invokeEachWith

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