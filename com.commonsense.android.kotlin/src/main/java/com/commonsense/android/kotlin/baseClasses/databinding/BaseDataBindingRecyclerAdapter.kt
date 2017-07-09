package com.commonsense.android.kotlin.baseClasses.databinding

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.annotation.AnyThread
import android.support.annotation.IntRange
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.commonsense.android.kotlin.android.logging.L
import com.commonsense.android.kotlin.collections.TypeHashCodeLookupRepresent
import com.commonsense.android.kotlin.collections.TypeSectionLookupRepresentative
import length
import java.lang.ref.WeakReference

/**
 * Created by kasper on 17/05/2017.
 */

typealias BindingFunction = (BaseViewHolderItem<*>) -> Unit

typealias ViewInflatingFunction<Vm> = (inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean) -> Vm

typealias InflatingFunction<Vm> = (inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean) -> BaseViewHolderItem<Vm>

open class BaseViewHolderItem<out T : ViewDataBinding>(val item: T) : RecyclerView.ViewHolder(item.root) {
    val viewBindingTypeValue = item.javaClass.hashCode()
}

interface IRenderModelItem<T : Any, Vm : ViewDataBinding> :
        TypeHashCodeLookupRepresent<InflatingFunction<Vm>> {

    fun getValue(): T

    fun renderFunction(view: Vm, model: T, viewHolder: BaseViewHolderItem<Vm>)

    fun bindToViewHolder(holder: BaseViewHolderItem<*>)

    fun createViewHolder(inflatedView: Vm): BaseViewHolderItem<Vm>

    fun getInflaterFunction(): ViewInflatingFunction<Vm>
}

abstract class BaseRenderModel<
        T : Any,
        Vm : ViewDataBinding>(val item: T, classType: Class<Vm>)
    : IRenderModelItem<T, Vm> {


    override fun getValue(): T = item

    override fun getTypeValue() = vmTypeValue
    private val vmTypeValue: Int by lazy {
        classType.hashCode()
    }

    override fun bindToViewHolder(holder: BaseViewHolderItem<*>) {
        if (holder.viewBindingTypeValue == vmTypeValue) {
            @Suppress("UNCHECKED_CAST")
            renderFunction(holder.item as Vm, item, holder as BaseViewHolderItem<Vm>)
            //we are now "sure" that the binding class is the same as ours, thus casting "should" be "ok". (we basically introduced our own type system)
        } else {
            L.debug("RenderModelItem", "unable to bind to view even though it should be correct type$vmTypeValue expected, got : ${holder.viewBindingTypeValue}")
        }
    }

    override fun createViewHolder(inflatedView: Vm): BaseViewHolderItem<Vm> {
        return BaseViewHolderItem(inflatedView)
    }

    override fun getCreatorFunction(): InflatingFunction<Vm> {
        return { inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean ->
            createViewHolder(getInflaterFunction().invoke(inflater, parent, attach))
        }
    }
}

open class RenderModelItem<
        T : Any,
        Vm : ViewDataBinding>(private val item: T,
                              private val vmInflater: ViewInflatingFunction<Vm>,
                              private val classType: Class<Vm>,
                              private val vmRender: (view: Vm, model: T, viewHolder: BaseViewHolderItem<Vm>) -> Unit)
    : IRenderModelItem<T, Vm> {
    override fun getInflaterFunction() = vmInflater


    override fun createViewHolder(inflatedView: Vm): BaseViewHolderItem<Vm> {
        return BaseViewHolderItem(inflatedView)
    }

    override fun getCreatorFunction(): InflatingFunction<Vm> {
        return { inflater: LayoutInflater, parent: ViewGroup?, attach: Boolean ->
            createViewHolder(vmInflater(inflater, parent, attach))
        }
    }

    override fun getValue(): T = item

    override fun getTypeValue(): Int = vmTypeValue

    override fun renderFunction(view: Vm, model: T, viewHolder: BaseViewHolderItem<Vm>)
            = vmRender(view, model, viewHolder)

    override fun bindToViewHolder(holder: BaseViewHolderItem<*>) {
        if (holder.viewBindingTypeValue == vmTypeValue) {
            @Suppress("UNCHECKED_CAST")
            renderFunction(holder.item as Vm, item, holder as BaseViewHolderItem<Vm>)
            //we are now "sure" that the binding class is the same as ours, thus casting "should" be "ok". (we basically introduced our own type system)
        } else {
            L.debug("RenderModelItem", "unable to bind to view even though it should be correct type$vmTypeValue expected, got : ${holder.viewBindingTypeValue}")
        }
    }

    //more performant than an inline getter that retrives it.
    private val vmTypeValue: Int by lazy {
        classType.hashCode()
    }
}

//Base class for data binding recycler adapters.
abstract class AbstractDataBindingRecyclerAdapter<T>(context: Context) :
        RecyclerView.Adapter<BaseViewHolderItem<*>>() where T : IRenderModelItem<*, *> {

    override fun getItemId(position: Int): Long {
        return RecyclerView.NO_ID
    }

    protected val dataCollection: TypeSectionLookupRepresentative<T, InflatingFunction<*>>
            = TypeSectionLookupRepresentative()

    private val listeningRecyclers = mutableSetOf<WeakReference<RecyclerView>>()

    private val inflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }


    override fun onCreateViewHolder(parent: ViewGroup?, @IntRange(from = 0) viewType: Int): BaseViewHolderItem<*>? {
        val rep = dataCollection.getTypeRepresentativeFromTypeValue(viewType)
        return rep?.invoke(inflater, parent, false)
    }

    override fun getItemViewType(@IntRange(from = 0) position: Int): Int {
        val (row, section) = dataCollection.indexToPath(position)
        return dataCollection[row, section]?.getTypeValue() ?: 0

    }

    override fun onBindViewHolder(holder: BaseViewHolderItem<*>, @IntRange(from = 0) position: Int) {
        //lookup type to converter, then apply model on view using converter
        val (row, section) = dataCollection.indexToPath(position)
        val render = dataCollection[row, section]
        render?.bindToViewHolder(holder)
    }


    override fun getItemCount(): Int = dataCollection.size

    open fun add(newItem: T, atSection: Int) = updateData {
        stopScroll()
        dataCollection.add(newItem, atSection)
        notifyItemInserted(dataCollection.size)
    }

    open fun addAll(items: Collection<T>, atSection: Int) = updateData {
        val startPos = itemCount
        dataCollection.addAll(items, atSection)
        notifyItemRangeInserted(startPos, items.size)
    }

    open fun add(item: T, atRow: Int, atSection: Int) = updateData {
        dataCollection.add(item, atRow, atSection)
    }

    open fun addAll(items: Collection<T>, startPosition: Int, atSection: Int) = updateData {
        dataCollection.addAll(items, atSection, startPosition)
        notifyItemRangeInserted(startPosition, items.size)
    }

    open fun addAll(vararg items: T, startPosition: Int, atSection: Int) = updateData {
        dataCollection.addAll(items.asList(), startPosition)
        notifyItemRangeInserted(startPosition, items.size)
    }

    open fun remove(newItem: T, atSection: Int) = updateData {
        removeAt(dataCollection.indexOf(newItem, atSection), atSection) // ?????
    }

    open fun removeAt(row: Int, inSection: Int) = updateData {
        if (dataCollection.removeAt(row, inSection)) {
            notifyItemRemoved(row)
        }
    }


    open fun removeIn(range: kotlin.ranges.IntRange, atSection: Int) = updateData {
        if (dataCollection.removeInRange(range, atSection)) {
            notifyItemRangeRemoved(range.start, range.length)
        }
    }


    open fun getItem(atRow: Int, atSection: Int): T? = dataCollection[atRow, atSection]

    open fun clear() = updateData {
        dataCollection.clear()
        notifyDataSetChanged()
    }


    protected fun addNoNotify(item: T, atSection: Int) = updateData {
        dataCollection.add(item, atSection)
    }

    protected fun addNoNotify(items: List<T>, atSection: Int) = updateData {
        dataCollection.addAll(items, atSection)
    }

    open fun clearAndSetSection(items: List<T>, atSection: Int) {
        val changes = dataCollection.clearAndSetSection(items, atSection) ?: return
        notifyItemRangeChanged(changes.start, changes.length)
    }

    private fun clearSection(atSection: Int) {
        val location = dataCollection.clearSection(atSection) ?: return
        notifyItemRangeRemoved(location.start, location.length)
    }

    open fun replace(newItem: T, position: Int, atSection: Int) = updateData {
        dataCollection.replace(newItem, position, atSection)
        notifyItemChanged(position)
    }

    @UiThread
    protected fun clearAndSetItemsNoNotify(items: List<T>, atSection: Int) {
        stopScroll()
        dataCollection.clearAndSetSection(items, atSection)
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


    /**
     * must be called from all things that manipulate the dataCollection.
     */
    private fun updateData(action: () -> Unit) {
        stopScroll()
        action()
    }

    fun getRepresentUsingType(viewHolderItem: BaseViewHolderItem<*>): InflatingFunction<*>? {
        return dataCollection.getTypeRepresentativeFromTypeValue(viewHolderItem.viewBindingTypeValue)
    }

    fun getItemFromRawIndex(rawIndex: Int): T? {
        val (row, section) = dataCollection.indexToPath(rawIndex)
        return dataCollection[row, section]
    }

    fun hideSection(sectionIndex: Int) {
        val sectionLocation = dataCollection.ignoreSection(sectionIndex) ?: return
        notifyItemRangeRemoved(sectionLocation.start, sectionLocation.endInclusive - sectionLocation.start)
    }

    fun showSection(sectionIndex: Int) {
        val sectionLocation = dataCollection.acceptSection(sectionIndex) ?: return
        notifyItemRangeInserted(sectionLocation.start, sectionLocation.endInclusive - sectionLocation.start)
    }

    val sectionCount: Int
        get() = dataCollection.size

    @AnyThread
    protected fun clearNoNotify() {
        dataCollection.clear()
    }

}

open class BaseDataBindingRecyclerAdapter(context: Context) :
        AbstractDataBindingRecyclerAdapter<IRenderModelItem<*, *>>(context)


class DefaultDataBindingRecyclerAdapter(context: Context) : BaseDataBindingRecyclerAdapter(context)