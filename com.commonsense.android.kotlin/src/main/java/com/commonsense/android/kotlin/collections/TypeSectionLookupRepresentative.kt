package com.commonsense.android.kotlin.collections

import android.support.annotation.IntRange
import android.util.SparseArray
import com.commonsense.android.kotlin.extensions.collections.getSafe
import com.commonsense.android.kotlin.extensions.collections.removeAll
import com.commonsense.android.kotlin.extensions.collections.replace
import com.commonsense.android.kotlin.extensions.collections.toList
import length

/**
 * Created by kasper on 05/07/2017.
 */

class TypeSection<T> {
    val size: Int
        get() = collection.size

    val collection: MutableList<T> = mutableListOf()

    /**
     * if this section is to be ignored. (in ui terms, hidden for example)
     */
    var isIgnored = false
//
//    var header: T? = null
//
//    var footer: T? = null

}

class TypeSectionLookupRepresentative<T : TypeHashCodeLookupRepresent<Rep>, out Rep : Any> {


    private val lookup = TypeRepresentative<T, Rep>()

    private val data: SparseArray<TypeSection<T>> = SparseArray()

    @IntRange(from = 0)
    private var cachedSize: Int = 0

    val size
        get() = cachedSize

    fun add(item: T, @IntRange(from = 0) atSection: Int) = ensureSection(atSection) {
        data.get(atSection).collection.add(item)
        lookup.add(item)
        cachedSize += 1

    }


    fun addAll(items: Collection<T>, @IntRange(from = 0) atSection: Int) = ensureSection(atSection) {
        cachedSize += items.count()
        data.get(atSection).collection.addAll(items)
        lookup.addAll(items)
    }

    fun removeItem(item: T, @IntRange(from = 0) atSection: Int) {
        val didRemove = data[atSection]?.collection?.remove(item) ?: false
        if (didRemove) {
            lookup.remove(item)
            cachedSize -= 1
        }
    }

    fun removeItems(items: List<T>, @IntRange(from = 0) atSection: Int) {
        items.forEach {
            this.removeItem(it, atSection)
        }
    }


    fun getItem(atRow: Int, @IntRange(from = 0) atSection: Int): T? {
        return data.get(atSection)?.collection?.getSafe(atRow)
    }

    private fun IndexPathIsValid(@IntRange(from = 0) atRow: Int, @IntRange(from = 0) atSection: Int): Boolean {
        return getItem(atRow, atSection) != null
    }

    operator fun get(@IntRange(from = 0) atRow: Int, @IntRange(from = 0) atSection: Int): T? = getItem(atRow, atSection)

    operator fun get(index: IndexPath): T? = getItem(index.row, index.section)

    private inline fun ensureSection(@IntRange(from = 0) atSection: Int, crossinline function: () -> Unit): Unit {
        addSectionIfMissing(atSection)
        function()
    }

    private fun addSectionIfMissing(atSection: Int) {
        if (data[atSection, null] == null) {
            data.put(atSection, TypeSection())
        }
    }

    fun getTypeRepresentativeFromTypeValue(type: Int): Rep? {
        return lookup.getTypeRepresentativeFromTypeValue(type)
    }

    fun add(item: T, atRow: Int, atSection: Int) = ensureSection(atSection) {
        data[atSection].collection.add(atRow, item)
        lookup.add(item)
    }

    /**
     * returns what have changed. (the diff).
     */
    fun clearAndSetSection(items: List<T>, atSection: Int): kotlin.ranges.IntRange? {
        val removed = clearSection(atSection)
        addAll(items, atSection)
        if (removed == null) {
            return calculateLocationForSection(atSection)
        }
        val largestLength = maxOf(removed.length, items.size)
        return removed.start until largestLength
    }

    fun replace(newItem: T, position: Int, inSection: Int) = ensureSection(inSection) {
        data[inSection].collection.replace(newItem, position)
    }

    fun clear() {
        data.clear()
        lookup.clear()
        cachedSize = 0
    }

    fun addAll(items: Collection<T>, startPosition: Int, atSection: Int) = ensureSection(atSection) {
        data[atSection].collection.addAll(startPosition, items)
        lookup.addAll(items)
    }

    fun indexToPath(position: Int): IndexPath? {
        //naive implementation

        var currentPosition = position
        for (dataIndex in 0 until data.size()) {
            val section = data.keyAt(dataIndex)
            val item = data[section]
            if (item.isIgnored) {
                continue//skip ignored entries.
            }

            if (currentPosition < item.size) {
                return IndexPath(currentPosition, section)
            }
            currentPosition -= item.size
        }
        return null
    }


    fun calculateLocationForSection(@IntRange(from = 0) sectionIndex: Int): kotlin.ranges.IntRange? {
        val dataItems = data.toList(sectionIndex + 1).filter { !it.isIgnored }
        if (dataItems.isEmpty()) {
            return null
        }
        val start = dataItems.dropLast(1).sumBy { it.size }
        val end = dataItems.last().size + start
        return start..end
    }

    fun removeAt(row: Int, inSection: Int): Boolean {
        val item = data[inSection]?.collection?.removeAt(row)
        item?.let { lookup.remove(it) }
        return item != null
    }

    fun removeInRange(range: kotlin.ranges.IntRange, atSection: Int): Boolean {
        addSectionIfMissing(atSection)
        val section = data[atSection]
        return section.collection.removeAll(range)
    }

    fun indexOf(newItem: T, atSection: Int): Int {
        addSectionIfMissing(atSection)
        return data[atSection].collection.indexOf(newItem)
    }

    fun ignoreSection(sectionIndex: Int): kotlin.ranges.IntRange? {
        addSectionIfMissing(sectionIndex)
        val location = calculateLocationForSection(sectionIndex)
        if (data[sectionIndex].isIgnored) {
            return null
        }
        data[sectionIndex].isIgnored = true
        cachedSize -= data[sectionIndex].size
        return location
    }

    /**
     * Inverse of ignore section.
     */
    fun acceptSection(sectionIndex: Int): kotlin.ranges.IntRange? {
        addSectionIfMissing(sectionIndex)
        if (!data[sectionIndex].isIgnored) {
            return null
        }
        data[sectionIndex].isIgnored = false
        cachedSize += data[sectionIndex].size
        return calculateLocationForSection(sectionIndex)
    }

    fun getSectionLocation(sectionIndex: Int): kotlin.ranges.IntRange? {
        return calculateLocationForSection(sectionIndex)
    }

    fun clearSection(atSection: Int): kotlin.ranges.IntRange? {
        addSectionIfMissing(atSection)
        val location = calculateLocationForSection(atSection) ?: return null
        data[atSection].collection.clear()
        return location
    }

    fun <U> map(converter: (TypeSection<T>) -> U): List<U> {
        return (0 until data.size()).map { converter(data.get(it)) }
    }

}

data class IndexPath(val row: Int, val section: Int)
