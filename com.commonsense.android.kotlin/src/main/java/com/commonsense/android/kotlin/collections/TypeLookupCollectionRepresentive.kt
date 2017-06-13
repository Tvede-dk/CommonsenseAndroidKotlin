package com.commonsense.android.kotlin.collections

import android.util.SparseArray

/**
 * Created by Kasper Tvede on 03-06-2017.
 */


//TODO -- the idea is that, for each "type" we want a "representative", alongside the counter.
// the other collection sorta have this, howver its VERY inefficient with large data sets
// (a loop over stuff when removing is not a good idea).
interface TypeHashCodeLookup {
    fun getTypeValue(): Int
}


interface TypeHashCodeLookupRepresent<out U> : TypeHashCodeLookup {

    fun getInflaterFunction(): U
}

//so the types are as
class TypeLookupCollectionRepresentive<T : TypeHashCodeLookupRepresent<Rep>, out Rep : Any> {

    //collection of the data
    private val data: MutableList<T> = mutableListOf()

    //maps a type to a rep function
    private val lookupRep: SparseArray<Rep> = SparseArray()

    //counts each type
    private val lookupCounter: SparseArray<Int> = SparseArray()

    val size
        get() = data.size

    fun add(item: T) {
        addType(item)
        data.add(item)
    }

    private fun addType(item: T) {
        val type = item.getTypeValue()
        createIfMissing(type, item.getInflaterFunction())
        lookupCounter.put(type, lookupCounter[type] + 1)
    }

    fun addAll(vararg items: T) = items.forEach(this::add)

    fun addAll(items: Iterable<T>) = items.forEach(this::add)

    fun add(item: T, at: Int) {
        if (at >= 0 && at < data.size) {
            addType(item)
            data.add(at, item)
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
        lookupCounter.clear()
        lookupRep.clear()
    }

    fun getDataBy(position: Int): T? {
        return data[position]
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

    operator fun get(position: Int): T? = getDataBy(position)

    fun isIndexValid(index: Int): Boolean = index in 0..(size - 1)

    fun indexOf(newItem: T): Int = data.indexOf(newItem)
    fun removeAt(index: Int) {
        if (isIndexValid(index)) {
            handleRemove(data.removeAt(index))
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


}