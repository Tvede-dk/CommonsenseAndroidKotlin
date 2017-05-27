package com.commonsense.android.kotlin.baseClasses

import android.content.Context
import android.content.res.Resources
import android.widget.ArrayAdapter
import java.util.*

/**
 * created by Kasper Tvede on 29-09-2016.
 */
open class BaseAdapter<T>(context: Context) : ArrayAdapter<T>(context, 0) {

    override fun add(obj: T) {
        if (obj != null) {
            super.add(obj)
        }
    }

    fun addF(obj: T): BaseAdapter<T> {
        add(obj)
        return this
    }

    fun addF(vararg objs: T): BaseAdapter<T> {
        objs.forEach { add(it) }
        return this
    }


    override fun sort(comparator: Comparator<in T>) {
        super.sort(comparator)
    }

    fun addAllF(collection: Collection<T>): BaseAdapter<T> {
        addAll(collection)
        return this
    }

    override fun addAll(collection: Collection<T>) {
        super.addAll(collection)
    }

    override fun addAll(vararg items: T) {
        items.forEach { this.add(it) }
    }

    override fun getItem(position: Int): T? {
        if (isIndexValid(position)) {
            return super.getItem(position)
        }
        return null
    }

    fun getItems(): List<T> {
        val list = mutableListOf<T>()
        (0..count - 1).forEach {
            getItem(it)?.let(list::add)
        }
        return list
    }

    override fun clear() {
        super.clear()
    }

    override fun remove(obj: T) {
        if (obj != null) {
            super.remove(obj)
        }
    }

    override fun getPosition(item: T): Int {
        return super.getPosition(item)
    }

    override fun getCount(): Int {
        return super.getCount()
    }

    override fun setDropDownViewTheme(theme: Resources.Theme?) {
        super.setDropDownViewTheme(theme)
    }

    override fun insert(obj: T, index: Int) {
        if (obj != null) {
            super.insert(obj, index)
        }
    }

    fun isIndexValid(index: Int): Boolean {
        return index in 0..(count - 1)
    }


    fun addAll(map: List<T>) {
        map.forEach { add(it) }
    }

}



