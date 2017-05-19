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

data class RenderModelItem<T : Any, Vm : ViewDataBinding>(val item: T, val classType: Class<Vm>,
                                                          val renderFunction: (view: Vm, model: T) -> Unit,
                                                          val inflaterFunction: (inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean) -> Vm) {
    fun bindToViewHolder(holder: BaseViewHolderItem<*>) {
        if (holder.viewBindingTypeValue == typeValue) {
            @Suppress("UNCHECKED_CAST")
            bindDataToView(holder.item as Vm, item)
            //we are now "sure" that the binding class is the same as ours, thus casting "should" be "ok". (we basically introduced our own type system)
        } else {
            L.debug("RenderModelItem", "unable to bind to view even though it should be correct type$typeValue expected, got : ${holder.viewBindingTypeValue}")
        }
    }

    fun bindDataToView(viewBinding: Vm, toShow: T) {
        renderFunction(viewBinding, toShow)
    }

    //more performant than an inline getter that retrives it.
    val typeValue : Int by lazy {
        classType.hashCode()
    } 

}


class BaseDataBindingRecyclerView(context: Context) : RecyclerView.Adapter<BaseViewHolderItem<*>>() {

    private val inflater by lazy {
        LayoutInflater.from(context)
    }

    private val data: MutableList<RenderModelItem<*, *>> = mutableListOf()

    private val lookup: SparseArray<RenderModelItem<*, *>> = SparseArray()

    override fun onCreateViewHolder(parent: ViewGroup?, @IntRange(from = 0) viewType: Int): BaseViewHolderItem<*> {
        L.error("recycler binder","creating item for viewtype $viewType")
        return BaseViewHolderItem(lookup[viewType].inflaterFunction(inflater, parent, false))
    }

    override fun getItemViewType(@IntRange(from = 0) position: Int): Int {
        L.error("recycler binder","Getting viewtype for $position")
        return data[position].typeValue

    }

    override fun onBindViewHolder(holder: BaseViewHolderItem<*>, @IntRange(from = 0) position: Int) {
        //lookup type to converter, then apply model on view using converter
        val render = data[position]
        L.error("recycler binder","binding view at position $position")
        render.bindToViewHolder(holder)
    }

    @IntRange(from = 0)
    override fun getItemCount(): Int = data.size


    fun add(newItem: RenderModelItem<*, *>) {
        addTypeIfMissing(newItem)
        data.add(newItem)
        notifyItemInserted(data.size - 1)
    }

    fun addAll(items: List<RenderModelItem<*, *>>) {
        addTypesIfMissing(items)
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun remove(newItem: RenderModelItem<*, *>) {
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


    private fun addTypeIfMissing(newItem: RenderModelItem<*, *>) {
        if (lookup.indexOfKey(newItem.typeValue) == -1) {
            lookup.put(newItem.typeValue, newItem)
        }
    }

    private fun recalculateAllTypes() {
        lookup.clear()
        addTypesIfMissing(data)
    }

    private fun addTypesIfMissing(items: List<RenderModelItem<*, *>>) {
        items.forEach(this::addTypeIfMissing)
    }

}