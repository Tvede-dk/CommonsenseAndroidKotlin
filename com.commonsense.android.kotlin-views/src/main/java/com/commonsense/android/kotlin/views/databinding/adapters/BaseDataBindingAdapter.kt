package com.commonsense.android.kotlin.views.databinding.adapters

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.annotation.IntRange
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.commonsense.android.kotlin.base.extensions.collections.clearAndSet
import com.commonsense.android.kotlin.views.baseClasses.BaseAdapter

/**
 * created by Kasper Tvede on 30-09-2016.
 */
open class BaseDataBindingAdapter(context: Context) : BaseAdapter<BaseAdapterItemBinding<*>>(context) {
    private val viewTypes = SparseIntArray()

    private val layoutInflator: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position) ?: throw RuntimeException()
        val otherClass = convertView?.tag as ViewDataBinding?
        val binding = if (otherClass != null && otherClass::class.java == item.viewBindingClass) {
            item.useConvertedView(otherClass)
        } else {
            item.createBinding(layoutInflator, parent)
        }
        binding.root.tag = binding
        return binding.root
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)?.let { it::class.java.hashCode() } ?: 0
        return viewTypes.get(item, IGNORE_ITEM_VIEW_TYPE)
    }

    @IntRange(from = 1)

    override fun getViewTypeCount() = Math.max(viewTypes.size(), 1)

    fun calculateViewTypes(): Int {
        val classes = getItems().map { it::class.java }.distinct()
        val mappings = classes.map { it::class.java.hashCode() }.zip(IntRange(0, classes.size))
        viewTypes.clearAndSet(mappings)
        return viewTypeCount
    }

    override fun add(obj: BaseAdapterItemBinding<*>) {
        super.add(obj)
        obj.let(this::addHashCodeIfNeeded)
    }

    fun addHashCodeIfNeeded(obj: BaseAdapterItemBinding<*>) {
        val hashcode = obj::class.java.hashCode()
        if (viewTypes.get(hashcode, -1) == -1) {
            viewTypes.append(hashcode, viewTypes.size())
        }
    }

    override fun addAll(collection: Collection<BaseAdapterItemBinding<*>>) {
        super.addAll(collection)
        collection.forEach { addHashCodeIfNeeded(it) }
    }

    override fun addAll(vararg items: BaseAdapterItemBinding<*>) {
        super.addAll(*items)
        items.forEach(this::addHashCodeIfNeeded)
    }

    override fun clear() {
        super.clear()
        viewTypes.clear()
    }

    override fun remove(obj: BaseAdapterItemBinding<*>) {
        super.remove(obj)
        calculateViewTypes()
    }

    override fun insert(obj: BaseAdapterItemBinding<*>, index: Int) {
        super.insert(obj, index)
        addHashCodeIfNeeded(obj)
    }

}

abstract class BaseAdapterItemBinding<T : ViewDataBinding>(val constructorFunc: (LayoutInflater, ViewGroup, Boolean) -> T, val viewBindingClass: Class<T>) {
    /**
     *  assumption : this function only gets called, iff the class type of convertview.tag == viewbidingclass.
     * @param convertView
     */
    fun useConvertedView(convertView: ViewDataBinding): T {
        val binding = viewBindingClass.cast(convertView)
        useBinding(binding)
        return binding
    }

    fun createBinding(inflator: LayoutInflater, parent: ViewGroup): T {
        val binding = constructorFunc(inflator, parent, false)
        useBinding(binding)
        return binding
    }


    abstract fun useBinding(binding: T)

}

open class BaseAdapterItemBindingFunc<T : ViewDataBinding>(constructorFunc: (LayoutInflater, ViewGroup, Boolean) -> T, viewBindingClass: Class<T>, val viewHandler: (T) -> Unit) :
        BaseAdapterItemBinding<T>(constructorFunc, viewBindingClass) {
    override fun useBinding(binding: T) {
        viewHandler(binding)
    }
}
