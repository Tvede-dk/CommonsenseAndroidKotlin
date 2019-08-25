@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.datastructures

import android.util.*
import com.commonsense.android.kotlin.base.debug.prettyStringContent
import com.commonsense.android.kotlin.base.extensions.collections.toPrettyString

/**
 * Created by Kasper Tvede on 03-06-2017.
 */

interface TypeHashCodeLookup {
    fun getTypeValue(): Int
}


interface TypeHashCodeLookupRepresent<out U> : TypeHashCodeLookup {
    fun getCreatorFunction(): U
}

/**
 * A collection the handles a somewhat GC like collection of types and their representatives.
 */
class TypeRepresentative<in T : TypeHashCodeLookupRepresent<Rep>, out Rep : Any> {
    //maps a type to a rep function
    private val lookupRep: SparseArray<Rep> = SparseArray()

    //counts each type
    private val lookupCounter: SparseArray<Int> = SparseArray()

    fun add(item: T) {
        addType(item)
    }

    fun addType(item: T) {
        val type = item.getTypeValue()
        createIfMissing(type, item.getCreatorFunction())
        lookupCounter.put(type, lookupCounter[type] + 1)
    }

    fun addAll(vararg items: T) = items.forEach(this::add)

    fun addAll(items: Iterable<T>) = items.forEach(this::add)

    fun remove(item: T) {
        handleRemove(item)
    }

    fun clear() {
        lookupCounter.clear()
        lookupRep.clear()
    }

    fun getTypeRepresentative(item: T): Rep? {
        return lookupRep[item.getTypeValue()]
    }

    fun getTypeRepresentativeFromTypeValue(value: Int): Rep? {
        return lookupRep[value]
    }


    private fun createIfMissing(type: Int, represent: Rep) {
        performIfNotHaveType(type) {
            lookupRep.put(type, represent)
            lookupCounter.put(type, 0)
        }
    }

    private inline fun performIfNotHaveType(type: Int, crossinline function: () -> Unit) {
        if (lookupCounter.get(type, -1) == -1) {
            function()
        }
    }

    private fun handleRemove(itemRemoved: T) {
        val type = itemRemoved.getTypeValue()
        val counter = lookupCounter.get(type)
        if (counter <= 1) {
            lookupCounter.remove(type)
            lookupRep.remove(type)
        } else {
            lookupCounter.put(type, counter - 1)
        }
    }

    fun clearAndSet(items: Collection<T>) {
        clear()
        addAll(items)
    }

    fun removeAll(vararg toRemove: T) {
        toRemove.forEach(this::remove)
    }

    fun removeAll(items: Collection<T>) {
        items.forEach(this::remove)
    }

    override fun toString(): String {
        return toPrettyString()
    }

    fun toPrettyString(): String {
        return "Type lookup collection representative state:  " + listOf(
                lookupRep.toPrettyString(),
                lookupCounter.toPrettyString()
        ).prettyStringContent()

    }
}


//so the types are as
class TypeLookupCollectionRepresentative<T : TypeHashCodeLookupRepresent<Rep>, out Rep : Any> {

    //collection of the data
    private val data: MutableList<T> = mutableListOf()

    private val lookup = TypeRepresentative<T, Rep>()

    val size
        get() = data.size

    fun add(item: T) {
        lookup.add(item)
        data.add(item)
    }

    fun addAll(vararg items: T) = items.forEach(this::add)

    fun addAll(items: Iterable<T>) = items.forEach(this::add)

    fun add(item: T, at: Int) = ifSafeIndex(at) {
        lookup.add(item)
        data.add(at, item)
    }

    private inline fun ifSafeIndex(at: Int, crossinline function: () -> Unit) {
        if (isIndexValid(at)) {
            function()
        }
    }

    fun addAll(items: Iterable<T>, startPosition: Int) {
        items.forEachIndexed { index: Int, item: T ->
            add(item, index + startPosition)
        }
    }


    fun remove(item: T) {
        data.remove(item)
        handleRemove(item)
    }

    fun clear() {
        data.clear()
        lookup.clear()
    }

    fun getDataBy(position: Int): T? {
        return if (isIndexValid(position)) {
            data[position]
        } else {
            null
        }
    }

    fun getTypeRepresentative(item: T): Rep? = lookup.getTypeRepresentative(item)

    fun getTypeRepresentativeFromTypeValue(value: Int): Rep? = lookup.getTypeRepresentativeFromTypeValue(value)


    operator fun get(position: Int): T? = getDataBy(position)

    fun isIndexValid(index: Int): Boolean = index in 0 until size

    fun indexOf(newItem: T): Int = data.indexOf(newItem)

    fun removeAt(index: Int) = ifSafeIndex(index) {
        handleRemove(data.removeAt(index))
    }

    fun replace(newItem: T, position: Int) = ifSafeIndex(position) {
        add(newItem, position)
        removeAt(position + 1)
    }

    private fun handleRemove(itemRemoved: T) = lookup.remove(itemRemoved)

    fun clearAndSet(items: Collection<T>) {
        clear()
        addAll(items)
    }

    fun removeAll(vararg toRemove: T) {
        toRemove.forEach(this::remove)
    }

    fun isRangeValid(range: IntRange): Boolean {
        return range.first >= 0 && range.last < size
    }

    fun removeIn(range: IntRange) {
        range.forEach { _ ->
            removeAt(range.first)
        }
    }


}