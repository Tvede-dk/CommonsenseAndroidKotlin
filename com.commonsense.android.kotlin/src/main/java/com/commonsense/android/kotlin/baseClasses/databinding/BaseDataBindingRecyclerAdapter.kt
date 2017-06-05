package com.commonsense.android.kotlin.baseClasses.databinding

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.annotation.IntRange
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.commonsense.android.kotlin.android.logging.L
import com.commonsense.android.kotlin.collections.TypeHashCodeLookupRepresentive
import com.commonsense.android.kotlin.collections.TypeLookupCollectionRepresentive
import java.lang.ref.WeakReference

/**
 * Created by kasper on 17/05/2017.
 */

typealias BindingFunction = (BaseViewHolderItem<*>) -> Unit

typealias InflatingFunction<T> = (inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean) -> T

data class BaseViewHolderItem<out T : ViewDataBinding>(val item: T) : RecyclerView.ViewHolder(item.root) {
    val viewBindingTypeValue = item.javaClass.hashCode()
}

interface IRenderModelItem<T : Any, Vm : ViewDataBinding> : TypeHashCodeLookupRepresentive<InflatingFunction<Vm>> {
    fun getValue(): T
    //    fun inflaterFunction(inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean): Vm
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
    override fun getRepresentive(): InflatingFunction<Vm> = this.vmInflater

    override fun getValue(): T = item

    override fun getTypeValue(): Int = vmTypeValue

//    override fun inflaterFunction(inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean) = vmInflater(inflater, parent, false)

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

abstract class AbstractDataBindingRecyclerAdapter<T : IRenderModelItem<*, *>>(context: Context) : RecyclerView.Adapter<BaseViewHolderItem<*>>() {
    protected val dataCollection: TypeLookupCollectionRepresentive<T, InflatingFunction<ViewDataBinding>> = TypeLookupCollectionRepresentive()

    protected val listeningRecyclers = mutableSetOf<WeakReference<RecyclerView>>()


    protected val inflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, @IntRange(from = 0) viewType: Int): BaseViewHolderItem<*>? {
        val rep = dataCollection.getTypeRepresentativeFromTypeValue(viewType)
        return rep?.invoke(inflater, parent, false)?.let { BaseViewHolderItem(it) }
    }

    override fun getItemViewType(@IntRange(from = 0) position: Int): Int {
        return dataCollection[position]?.getTypeValue() ?: 0

    }

    override fun onBindViewHolder(holder: BaseViewHolderItem<*>, @IntRange(from = 0) position: Int) {
        //lookup type to converter, then apply model on view using converter
        val render = dataCollection[position]
        render?.bindToViewHolder(holder)
    }


    override fun getItemCount(): Int = dataCollection.size

    open fun add(newItem: T) {
        stopScroll()
        dataCollection.add(newItem)
        notifyItemInserted(dataCollection.size)
    }

    open fun addAll(items: List<T>) {
        stopScroll()
        val startPos = itemCount
        dataCollection.addAll(items)
        notifyItemRangeInserted(startPos, items.size)
    }


    open fun remove(newItem: T) {
        removeAt(dataCollection.indexOf(newItem))
    }

    open fun removeAt(index: Int) {
        if (dataCollection.isIndexValid(index)) {
            stopScroll()
            dataCollection.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    open fun getItem(index: Int): IRenderModelItem<*, *>? = dataCollection[index]

    open fun clear() {
        stopScroll()
        dataCollection.clear()
        notifyDataSetChanged()
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
        stopScroll()
        dataCollection.clearAndSet(items)
    }

    protected fun stopScroll() {
        listeningRecyclers.forEach { recyclerView ->
            recyclerView.get()?.stopScroll()
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        if (recyclerView != null) {
            listeningRecyclers.add(WeakReference(recyclerView))
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (recyclerView != null) {
            listeningRecyclers.removeAll {
                val temp = it.get()
                temp == null || temp == recyclerView
            }
        }
    }
}

open class BaseDataBindingRecyclerAdapter(context: Context) : AbstractDataBindingRecyclerAdapter<IRenderModelItem<*, *>>(context)


