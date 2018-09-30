package com.commonsense.android.kotlin.views.baseClasses

import android.content.*
import android.content.res.*
import android.support.annotation.IntRange
import android.widget.*
import java.util.*

/**
 * created by Kasper Tvede on 29-09-2016.
 * Makes sure that the array adapter is type safe / no nulls
 *
 *
 */
open class BaseAdapter<T>(context: Context) : ArrayAdapter<T>(context, 0) {

    override fun add(obj: T) {
        if (obj != null) {
            super.add(obj)
        }
    }

    @Suppress("RedundantOverride")
    override fun sort(comparator: Comparator<in T>) {
        super.sort(comparator)
    }

    fun addAll(map: List<T>) {
        map.forEach { add(it) }
    }

    @Suppress("RedundantOverride")
    override fun addAll(collection: Collection<T>) {
        super.addAll(collection)
    }

    override fun addAll(vararg items: T) {
        items.forEach { this.add(it) }
    }

    override fun getItem(@IntRange(from = 0) position: Int): T? {
        if (isIndexValid(position)) {
            return super.getItem(position)
        }
        return null
    }

    fun getItems(): List<T> {
        val list = mutableListOf<T>()
        (0 until count).forEach {
            getItem(it)?.let(list::add)
        }
        return list
    }

    override fun remove(obj: T) {
        if (obj != null) {
            super.remove(obj)
        }
    }

    @Suppress("RedundantOverride")
    override fun getPosition(item: T): Int = super.getPosition(item)

    @Suppress("RedundantOverride")
    @IntRange(from = 0)
    override fun getCount(): Int = super.getCount()

    @Suppress("RedundantOverride")
    override fun setDropDownViewTheme(theme: Resources.Theme?) {
        super.setDropDownViewTheme(theme)
    }

    /**
     *  insert the given item, if it is not null
     */
    override fun insert(obj: T, @IntRange(from = 0) index: Int) {
        if (obj != null) {
            super.insert(obj, index)
        }
    }

    /**
     * Tells if the given index is valid ( in range 0 until count /  [0 ;count[ )
     */
    fun isIndexValid(@IntRange(from = 0) index: Int): Boolean = index in 0 until count
}



