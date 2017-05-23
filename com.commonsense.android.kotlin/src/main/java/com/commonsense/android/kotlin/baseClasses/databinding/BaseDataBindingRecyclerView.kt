package com.commonsense.android.kotlin.baseClasses.databinding

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.annotation.IntRange
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import com.commonsense.android.kotlin.android.logging.L

/**
 * Created by kasper on 17/05/2017.
 */

typealias BindingFunction = (BaseViewHolderItem<*>) -> Unit

typealias InflatingFunction<T> = (inflater: LayoutInflater) -> T

data class BaseViewHolderItem<out T : ViewDataBinding>(val item: T) : RecyclerView.ViewHolder(item.root) {
    val viewBindingTypeValue = item.javaClass.hashCode()
}

interface IRenderModelItem<in T : Any, Vm : ViewDataBinding> {
    fun inflaterFunction(inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean): Vm
    val typeValue: Int
    fun renderFunction(view: Vm, model: T)
    fun bindToViewHolder(holder: BaseViewHolderItem<*>)
}

abstract class BaseRenderModel<T : Any, Vm : ViewDataBinding>(val item: T, classType: Class<Vm>) : IRenderModelItem<T, Vm> {

    override val typeValue: Int by lazy {
        classType.hashCode()
    }

    override fun bindToViewHolder(holder: BaseViewHolderItem<*>) {
        if (holder.viewBindingTypeValue == typeValue) {
            @Suppress("UNCHECKED_CAST")
            renderFunction(holder.item as Vm, item)
            //we are now "sure" that the binding class is the same as ours, thus casting "should" be "ok". (we basically introduced our own type system)
        } else {
            L.debug("RenderModelItem", "unable to bind to view even though it should be correct type$typeValue expected, got : ${holder.viewBindingTypeValue}")
        }
    }

}

open class RenderModelItem<T : Any, Vm : ViewDataBinding>(val item: T,
                                                          val vmInflater: (inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean) -> Vm,
                                                          val classType: Class<Vm>,
                                                          val vmRender: (view: Vm, model: T) -> Unit) : IRenderModelItem<T, Vm> {
    override fun inflaterFunction(inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean) = vmInflater(inflater, parent, false)

    override fun renderFunction(view: Vm, model: T) = vmRender(view, model)

    override fun bindToViewHolder(holder: BaseViewHolderItem<*>) {
        if (holder.viewBindingTypeValue == typeValue) {
            @Suppress("UNCHECKED_CAST")
            renderFunction(holder.item as Vm, item)
            //we are now "sure" that the binding class is the same as ours, thus casting "should" be "ok". (we basically introduced our own type system)
        } else {
            L.debug("RenderModelItem", "unable to bind to view even though it should be correct type$typeValue expected, got : ${holder.viewBindingTypeValue}")
        }
    }

    //more performant than an inline getter that retrives it.
    override val typeValue: Int by lazy {
        classType.hashCode()
    }
}


class BaseDataBindingRecyclerView(context: Context) : RecyclerView.Adapter<BaseViewHolderItem<*>>() {

    private val inflater by lazy {
        LayoutInflater.from(context)
    }

    private val data: MutableList<IRenderModelItem<*, *>> = mutableListOf()

    private val lookup: SparseArray<IRenderModelItem<*, *>> = SparseArray()

    override fun onCreateViewHolder(parent: ViewGroup?, @IntRange(from = 0) viewType: Int): BaseViewHolderItem<*> {
        return BaseViewHolderItem(lookup[viewType].inflaterFunction(inflater, parent, false))
    }

    override fun getItemViewType(@IntRange(from = 0) position: Int): Int {
        return data[position].typeValue

    }

    override fun onBindViewHolder(holder: BaseViewHolderItem<*>, @IntRange(from = 0) position: Int) {
        //lookup type to converter, then apply model on view using converter
        val render = data[position]
        render.bindToViewHolder(holder)
    }

    @IntRange(from = 0)
    override fun getItemCount(): Int = data.size


    fun add(newItem: IRenderModelItem<*, *>) {
        addTypeIfMissing(newItem)
        data.add(newItem)
        notifyItemInserted(data.size - 1)
    }

    fun addAll(items: List<IRenderModelItem<*, *>>) {
        addTypesIfMissing(items)
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun remove(newItem: IRenderModelItem<*, *>) {
        val index = data.indexOf(newItem)
        if (index >= 0 && index < data.size) {
            data.removeAt(index)
            notifyItemRemoved(index)
            recalculateAllTypes()
            //recalculate the lookup
        }
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
        lookup.clear()
    }


    private fun addTypeIfMissing(newItem: IRenderModelItem<*, *>) {
        if (lookup.indexOfKey(newItem.typeValue) < 0) {
            lookup.put(newItem.typeValue, newItem)
        }
    }

    private fun recalculateAllTypes() {
        lookup.clear()
        addTypesIfMissing(data)
    }

    private fun addTypesIfMissing(items: List<IRenderModelItem<*, *>>) {
        items.forEach(this::addTypeIfMissing)
    }

}