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
import com.commonsense.android.kotlin.collections.TypeSection
import com.commonsense.android.kotlin.collections.TypeSectionLookupRepresentative
import com.commonsense.android.kotlin.extensions.isNullOrEqualTo
import ifTrue
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
            //we are now "sure" that the bi nding class is the same as ours, thus casting "should" be "ok". (we basically introduced our own type system)
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

    override fun getItemId(position: Int): Long = RecyclerView.NO_ID

    private val dataCollection: TypeSectionLookupRepresentative<T, InflatingFunction<*>>
            = TypeSectionLookupRepresentative()

    private val listeningRecyclers = mutableSetOf<WeakReference<RecyclerView>>()

    private val inflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    val sectionCount: Int
        get() = dataCollection.sectionCount


    override fun onCreateViewHolder(parent: ViewGroup?, @IntRange(from = 0) viewType: Int): BaseViewHolderItem<*>? {
        val rep = dataCollection.getTypeRepresentativeFromTypeValue(viewType)
        (rep == null).ifTrue { logContentData() }
        return rep?.invoke(inflater, parent, false)
    }

    //for debug only.
    private fun logContentData() {
        val cachedSize = dataCollection.size
        val realSize = dataCollection.calculateLocationForSection(
                dataCollection.sectionCount - 1)?.endInclusive ?: -1
        L.error("Inconsistency", "precheck, real size : $realSize, cached size: $cachedSize ")
    }

    override fun getItemViewType(@IntRange(from = 0) position: Int): Int {
        val index = dataCollection.indexToPath(position) ?: return 0
        return dataCollection[index]?.getTypeValue() ?: 0
    }

    override fun onBindViewHolder(holder: BaseViewHolderItem<*>, @IntRange(from = 0) position: Int) {
        //lookup type to converter, then apply model on view using converter
        val index = dataCollection.indexToPath(position) ?: return
        val render = dataCollection[index]
        render?.bindToViewHolder(holder)
    }


    override fun getItemCount(): Int = dataCollection.size

    open fun add(newItem: T, atSection: Int) = updateData {
        stopScroll()
        dataCollection.add(newItem, atSection)
        val index = dataCollection.calculateLocationForSection(atSection) ?: return@updateData
        notifyItemInserted(index.start + dataCollection.size)
    }

    open fun addAll(items: Collection<T>, atSection: Int) = updateData {

        dataCollection.addAll(items, atSection)
        val index = dataCollection.calculateLocationForSection(atSection) ?: return@updateData
        val startPos = itemCount + index.start
        notifyItemRangeInserted(startPos, items.size)
    }

    open fun add(item: T, atRow: Int, atSection: Int) = updateData {
        dataCollection.add(item, atRow, atSection)
        val index = dataCollection.calculateLocationForSection(atSection) ?: return@updateData
        notifyItemInserted(index.start + atRow)
    }

    open fun addAll(items: Collection<T>, startPosition: Int, atSection: Int) = updateData {
        dataCollection.addAll(items, atSection, startPosition)
        val index = dataCollection.calculateLocationForSection(atSection) ?: return@updateData
        notifyItemRangeInserted(index.start + startPosition, items.size)
    }

    open fun addAll(vararg items: T, atSection: Int) = updateData {
        val index = dataCollection.calculateLocationForSection(atSection) ?: return@updateData
        dataCollection.addAll(items.asList(), atSection)
        notifyItemRangeInserted(index.endInclusive, items.size)
    }

    open fun addAll(vararg items: T, startPosition: Int, atSection: Int) = updateData {
        dataCollection.addAll(items.asList(), startPosition)
        val index = dataCollection.calculateLocationForSection(atSection) ?: return@updateData
        notifyItemRangeInserted(index.start + startPosition, items.size)
    }

    open fun remove(newItem: T, atSection: Int): Int {
        val index = dataCollection.indexOf(newItem, atSection)
        updateData {
            removeAt(index, atSection)
        }
        return index

    }

    open fun removeAt(row: Int, inSection: Int) = updateData {
        val sectionLocation = dataCollection.getSectionLocation(inSection)
        if (sectionLocation != null && dataCollection.removeAt(row, inSection)) {
            notifyItemRemoved(sectionLocation.start + row)
        }
    }


    open fun removeIn(range: kotlin.ranges.IntRange, atSection: Int) = updateData {
        val sectionLocation = dataCollection.getSectionLocation(atSection)
        if (sectionLocation != null && dataCollection.removeInRange(range, atSection)) {
            notifyItemRangeRemoved(sectionLocation.start + range.start, sectionLocation.start + range.length)
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
        val (changes, added, removed) = dataCollection.clearAndSetSection(items, atSection)
        changes?.let {
            notifyItemRangeChanged(it.start, it.length)
        }
        added?.let {
            notifyItemRangeInserted(it.start, it.length)
        }
        removed?.let {
            notifyItemRangeRemoved(it.start, it.length)
        }
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
    protected fun clearAndSetItemsNoNotify(items: List<T>, atSection: Int, isIgnored: Boolean) {
        stopScroll()
        dataCollection.clearAndSetSection(items, atSection)
        isIgnored.ifTrue { dataCollection.ignoreSection(atSection) }
    }

    @UiThread
    protected fun setAllSections(sections: List<TypeSection<T>>) {
        stopScroll()
        dataCollection.setAllSections(sections)
        super.notifyDataSetChanged()
    }

    private fun stopScroll() {
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
        listeningRecyclers.removeAll {
            it.get().isNullOrEqualTo(recyclerView)
        }
    }


    /**
     * must be called from all things that manipulate the dataCollection.
     */
    private fun updateData(action: () -> Unit) {
        stopScroll()
        action()
    }

    open fun getRepresentUsingType(viewHolderItem: BaseViewHolderItem<*>): InflatingFunction<*>? =
            dataCollection.getTypeRepresentativeFromTypeValue(viewHolderItem.viewBindingTypeValue)

    open fun getItemFromRawIndex(rawIndex: Int): T? {
        val index = dataCollection.indexToPath(rawIndex) ?: return null
        return dataCollection[index]
    }

    open fun hideSection(sectionIndex: Int) {
        val sectionLocation = dataCollection.ignoreSection(sectionIndex) ?: return
        notifyItemRangeRemoved(sectionLocation.start, sectionLocation.endInclusive - sectionLocation.start)
    }

    open fun showSection(sectionIndex: Int) {
        val sectionLocation = dataCollection.acceptSection(sectionIndex) ?: return
        notifyItemRangeInserted(sectionLocation.start, sectionLocation.endInclusive - sectionLocation.start)
    }

    open fun toggleSectionsVisibility(vararg sectionIndexes: Int) {
        sectionIndexes.forEach(this::toggleSectionVisibility)
    }

    open fun toggleSectionVisibility(sectionIndex: Int) {
        val sectionData = dataCollection.getSectionAt(sectionIndex) ?: return
        if (sectionData.isIgnored) {
            showSection(sectionIndex)
        } else {
            hideSection(sectionIndex)
        }

    }


    @AnyThread
    protected fun clearNoNotify() {
        dataCollection.clear()
    }

}

open class BaseDataBindingRecyclerAdapter(context: Context) :
        AbstractDataBindingRecyclerAdapter<IRenderModelItem<*, *>>(context)


class DefaultDataBindingRecyclerAdapter(context: Context) : BaseDataBindingRecyclerAdapter(context)