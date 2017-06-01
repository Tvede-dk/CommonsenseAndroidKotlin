package com.commonsense.android.kotlin.collections

import android.util.SparseArray
import com.commonsense.android.kotlin.extensions.collections.isIndexValid

/**
 * Created by kasper on 01/06/2017.
 */

interface TypeHashCodeLookup {
    fun getTypeValue(): Int
}


class TypeLookupCollection<T : TypeHashCodeLookup> {

    private val data: MutableList<T> = mutableListOf()

    private val lookup: SparseArray<T> = SparseArray()

    private val lookupCounter: SparseArray<Int> = SparseArray()

    fun add(item: T) {
        data.add(item)
        addLookupIfMissing(item)
    }

    fun remove(item: T) {
        data.remove(item)
        removeLookupIfLast(item)
    }

    fun addAll(vararg item: T) {
        item.forEach(this::add)
    }

    fun addAll(items: List<T>) {
        items.forEach(this::add)
    }

    fun removeAll(vararg item: T) {
        item.forEach(this::remove)
    }

    fun getAnItemFromType(type: Int): T? {
        return lookup.get(type)
    }

    fun clear() {
        lookup.clear()
        lookupCounter.clear()
        data.clear()
    }

    fun removeAt(index: Int) {
        if (data.isIndexValid(index)) {
            val item = data.removeAt(index)
            removeLookupIfLast(item)
        }
    }

    fun getCount(): Int {
        return data.size
    }

    private fun addLookupIfMissing(item: T) {
        val type = getType(item)
        lookupCounter.put(type, lookupCounter.get(type, 0))
        if (lookup.indexOfKey(type) < 0) { // <0 => non existing
            lookup.put(type, item)
        }
    }

    private fun removeLookupIfLast(item: T) {
        val type = getType(item)
        val oldCounter = lookupCounter[type, 0]
        if (oldCounter <= 1) { //last item,
            lookupCounter.remove(type)
            lookup.remove(type)
        } else { // more items.
            lookupCounter.put(type, oldCounter - 1)
        }
    }

    private fun getType(item: T): Int {
        return item.getTypeValue()
    }

    operator fun get(position: Int): T? {
        return getItemAt(position)
    }

    fun getItemAt(position: Int): T? {
        if (data.isIndexValid(position)) {
            return data[position]
        }
        return null
    }

    fun indexOf(newItem: T): Int {
        return data.indexOf(newItem)
    }

    fun isIndexValid(index: Int): Boolean = data.isIndexValid(index)

    fun foreach(function: (item: T) -> Unit) = data.forEach(function)

    fun filterBy(filterAction: (T) -> Boolean) = data.filter(filterAction)
    fun clearAndSet(items: List<T>) {
        clear()
        addAll(items)
    }

}
