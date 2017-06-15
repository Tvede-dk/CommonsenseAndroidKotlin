package com.commonsense.android.kotlin.patterns

/**
 * Created by kasper on 23/05/2017.
 */
//TODO remove and use the default in stdlib ?
typealias callback<T> = (T) -> Unit
class ObserverPattern<T> {

    private var listOfListeners = mutableListOf<callback<T>>()

    fun addListener(callback: callback<T>) = listOfListeners.add(callback)

    fun removeListener(callback: callback<T>) = listOfListeners.remove(callback)

    fun clearListeners() = listOfListeners.clear()

    fun notify(item: T) = listOfListeners.forEach { it(item) }

}