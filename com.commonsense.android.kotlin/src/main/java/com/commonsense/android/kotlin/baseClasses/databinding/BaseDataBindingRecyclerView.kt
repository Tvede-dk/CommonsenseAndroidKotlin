package com.commonsense.android.kotlin.baseClasses.databinding

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.annotation.IntRange
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.commonsense.android.kotlin.android.logging.L
import com.commonsense.android.kotlin.collections.TypeHashCodeLookup
import com.commonsense.android.kotlin.collections.TypeLookupCollection

/**
 * Created by kasper on 17/05/2017.
 */

typealias BindingFunction = (BaseViewHolderItem<*>) -> Unit

typealias InflatingFunction<T> = (inflater: LayoutInflater) -> T

data class BaseViewHolderItem<out T : ViewDataBinding>(val item: T) : RecyclerView.ViewHolder(item.root) {
    val viewBindingTypeValue = item.javaClass.hashCode()
}

interface IRenderModelItem<T : Any, Vm : ViewDataBinding> : TypeHashCodeLookup {
    fun getValue(): T
    fun inflaterFunction(inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean): Vm
    fun renderFunction(view: Vm, model: T)
    fun bindToViewHolder(holder: BaseViewHolderItem<*>)
}

abstract class BaseRenderModel<T : Any, Vm : ViewDataBinding>(val item: T, classType: Class<Vm>) : IRenderModelItem<T, Vm> {

    override fun getValue(): T = item

    override fun getTypeValue() = vmTypeValue
    val vmTypeValue: Int by lazy {
        classType.hashCode()
    }

    override fun bindToViewHolder(holder: BaseViewHolderItem<*>) {
        if (holder.viewBindingTypeValue == vmTypeValue) {
            @Suppress("UNCHECKED_CAST")
            renderFunction(holder.item as Vm, item)
            //we are now "sure" that the binding class is the same as ours, thus casting "should" be "ok". (we basically introduced our own type system)
        } else {
            L.debug("RenderModelItem", "unable to bind to view even though it should be correct type$vmTypeValue expected, got : ${holder.viewBindingTypeValue}")
        }
    }

}

open class RenderModelItem<T : Any, Vm : ViewDataBinding>(val item: T,
                                                          val vmInflater: (inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean) -> Vm,
                                                          val classType: Class<Vm>,
                                                          val vmRender: (view: Vm, model: T) -> Unit) : IRenderModelItem<T, Vm> {
    override fun getValue(): T = item

    override fun getTypeValue(): Int = vmTypeValue

    override fun inflaterFunction(inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean) = vmInflater(inflater, parent, false)

    override fun renderFunction(view: Vm, model: T) = vmRender(view, model)

    override fun bindToViewHolder(holder: BaseViewHolderItem<*>) {
        if (holder.viewBindingTypeValue == vmTypeValue) {
            @Suppress("UNCHECKED_CAST")
            renderFunction(holder.item as Vm, item)
            //we are now "sure" that the binding class is the same as ours, thus casting "should" be "ok". (we basically introduced our own type system)
        } else {
            L.debug("RenderModelItem", "unable to bind to view even though it should be correct type$vmTypeValue expected, got : ${holder.viewBindingTypeValue}")
        }
    }

    //more performant than an inline getter that retrives it.
    val vmTypeValue: Int by lazy {
        classType.hashCode()
    }
}

abstract class AbstractDataBindingRecyclerView<T : IRenderModelItem<*, *>>(context: Context) : RecyclerView.Adapter<BaseViewHolderItem<*>>() {
    protected val dataCollection: TypeLookupCollection<T> = TypeLookupCollection()

    protected val inflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, @IntRange(from = 0) viewType: Int): BaseViewHolderItem<*>? {
        return dataCollection.getAnItemFromType(viewType)
                ?.inflaterFunction(inflater, parent, false)
                ?.let { BaseViewHolderItem(it) }
    }

    override fun getItemViewType(@IntRange(from = 0) position: Int): Int {
        return dataCollection[position]?.getTypeValue() ?: 0

    }

    override fun onBindViewHolder(holder: BaseViewHolderItem<*>, @IntRange(from = 0) position: Int) {
        //lookup type to converter, then apply model on view using converter
        val render = dataCollection[position]
        render?.bindToViewHolder(holder)
    }


    override fun getItemCount(): Int = dataCollection.getCount()

    open fun add(newItem: T) {
        dataCollection.add(newItem)
        notifyItemInserted(dataCollection.getCount())
    }

    open fun addAll(items: List<T>) {
        val startPos = itemCount
        dataCollection.addAll(items)
        notifyItemRangeInserted(startPos, items.size)
    }


    open fun remove(newItem: T) {
        removeAt(dataCollection.indexOf(newItem))
    }

    open fun removeAt(index: Int) {
        if (dataCollection.isIndexValid(index)) {
            dataCollection.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    open fun getItem(index: Int): IRenderModelItem<*, *>? = dataCollection[index]

    open fun clear() {
        val size = dataCollection.getCount()
        dataCollection.clear()
        notifyItemRangeRemoved(0, size)
    }


    protected fun addNoNotify(item: T) {
        dataCollection.add(item)
    }

    protected fun addNoNotify(items: List<T>) {
        dataCollection.addAll(items)
    }

    fun clearAndSetItems(items: List<T>) {
        clear()
        addAll(items)
    }

    protected fun clearAndSetItemsNoNotify(items: List<T>) {
        dataCollection.clearAndSet(items)
    }
}

open class BaseDataBindingRecyclerView(context: Context) : AbstractDataBindingRecyclerView<IRenderModelItem<*, *>>(context)


