package com.commonsense.android.kotlin.views.databinding.adapters

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.annotation.AnyThread
import android.support.annotation.IntRange
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.base.extensions.collections.length
import com.commonsense.android.kotlin.base.extensions.isNullOrEqualTo
import com.commonsense.android.kotlin.system.datastructures.IndexPath
import com.commonsense.android.kotlin.system.datastructures.TypeHashCodeLookupRepresent
import com.commonsense.android.kotlin.system.datastructures.TypeSection
import com.commonsense.android.kotlin.system.datastructures.TypeSectionLookupRepresentative
import com.commonsense.android.kotlin.system.logging.L
import com.commonsense.android.kotlin.views.ViewInflatingFunction
import java.lang.ref.WeakReference

/**
 * Created by kasper on 17/05/2017.
 */

typealias BindingFunction = (BaseViewHolderItem<*>) -> Unit


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

    override fun createViewHolder(inflatedView: Vm): BaseViewHolderItem<Vm> =
            BaseViewHolderItem(inflatedView)

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


    override fun createViewHolder(inflatedView: Vm): BaseViewHolderItem<Vm> =
            BaseViewHolderItem(inflatedView)

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
        return rep?.invoke(inflater, parent, false)
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

    open fun add(newItem: T, inSection: Int): Unit = updateData {
        dataCollection.add(newItem, inSection).rawRow.apply {
            notifyItemInserted(this)
        }
    }

    open fun addAll(items: Collection<T>, inSection: Int): Unit = updateData {
        dataCollection.addAll(items, inSection).inRaw.apply {
            notifyItemRangeInserted(this.start, this.length)
        }

    }

    open fun insert(item: T, atRow: Int, inSection: Int): Unit = updateData {
        dataCollection.insert(item, atRow, inSection)?.rawRow.apply {
            this?.let { notifyItemInserted(it) }
        }
    }

    open fun insertAll(items: Collection<T>, startPosition: Int, inSection: Int): Unit = updateData {
        dataCollection.insertAll(items, inSection, startPosition)?.inRaw.apply {
            this?.let { notifyItemRangeInserted(it.start, it.length) }
        }
    }

    open fun addAll(vararg items: T, inSection: Int): Unit = updateData {
        dataCollection.addAll(items.asList(), inSection).inRaw.apply {
            notifyItemRangeInserted(this.start, this.length)
        }
    }

    open fun insertAll(vararg items: T, startPosition: Int, inSection: Int): Unit = updateData {
        dataCollection.insertAll(items.asList(), startPosition, inSection)?.inRaw.apply {
            this?.let { notifyItemRangeInserted(it.start, it.length) }
        }
    }

    open fun remove(newItem: T, inSection: Int): Int? = updateData {
        return@updateData dataCollection.removeItem(newItem, inSection)?.apply {
            notifyItemRemoved(rawRow)
        }?.rawRow
    }

    open fun removeAt(row: Int, inSection: Int): Unit = updateData {
        dataCollection.removeAt(row, inSection)?.rawRow.apply {
            this?.let { notifyItemRemoved(it) }
        }
    }


    open fun removeIn(range: kotlin.ranges.IntRange, inSection: Int): Unit = updateData {
        dataCollection.removeInRange(range, inSection)?.inRaw.apply {
            this?.let {
                notifyItemRangeRemoved(it.start + range.start, range.length)
            }
        }
    }


    open fun getItem(atRow: Int, inSection: Int): T? = dataCollection[atRow, inSection]

    open fun clear(): Unit = updateData {
        dataCollection.clear()
        notifyDataSetChanged()
    }


    protected fun addNoNotify(item: T, inSection: Int): Unit = updateData {
        dataCollection.add(item, inSection).rawRow
    }

    protected fun addNoNotify(items: List<T>, inSection: Int): Unit = updateData {
        dataCollection.addAll(items, inSection).inRaw
    }

    open fun setSection(items: List<T>, inSection: Int) {
        val (changes, added, removed) = dataCollection.setSection(items, inSection)
        changes?.let {
            notifyItemRangeChanged(it.inRaw.start, it.inRaw.length)
        }
        added?.let {
            notifyItemRangeInserted(it.inRaw.start, it.inRaw.length)
        }
        removed?.let {
            notifyItemRangeRemoved(it.inRaw.start, it.inRaw.length)
        }
    }

    @UiThread
    private fun clearSection(inSection: Int): Unit = updateData {
        dataCollection.clearSection(inSection)?.inRaw.apply {
            this?.let { notifyItemRangeRemoved(it.start, it.length) }
        }
    }

    open fun replace(newItem: T, position: Int, inSection: Int): Unit = updateData {
        dataCollection.replace(newItem, position, inSection)?.rawRow.apply {
            this?.let { notifyItemChanged(it) }
        }
    }

    @UiThread
    protected fun clearAndSetItemsNoNotify(items: List<T>, inSection: Int, isIgnored: Boolean): Unit {
        stopScroll()
        dataCollection.setSection(items, inSection)
        isIgnored.ifTrue { dataCollection.ignoreSection(inSection) }
    }

    @UiThread
    protected fun setAllSections(sections: List<TypeSection<T>>): Unit {
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
    private inline fun <T> updateData(crossinline action: () -> T): T {
        stopScroll()
        return action()
    }

    open fun getRepresentUsingType(viewHolderItem: BaseViewHolderItem<*>): InflatingFunction<*>? =
            dataCollection.getTypeRepresentativeFromTypeValue(viewHolderItem.viewBindingTypeValue)

    open fun getItemFromRawIndex(rawIndex: Int): T? {
        val index = dataCollection.indexToPath(rawIndex) ?: return null
        return dataCollection[index]
    }

    open fun hideSection(sectionIndex: Int) {
        val sectionLocation = dataCollection.ignoreSection(sectionIndex)?.inRaw ?: return
        notifyItemRangeRemoved(sectionLocation.start, sectionLocation.length)
    }

    open fun showSection(sectionIndex: Int) {
        val sectionLocation = dataCollection.acceptSection(sectionIndex)?.inRaw ?: return
        notifyItemRangeInserted(sectionLocation.start, sectionLocation.length)
    }

    open fun toggleSectionsVisibility(vararg sectionIndexes: Int) {
        sectionIndexes.forEach(this::toggleSectionVisibility)
    }

    open fun toggleSectionVisibility(sectionIndex: Int) {
        val sectionData = dataCollection.sectionAt(sectionIndex) ?: return
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

    fun getIndexFor(item: T, @IntRange(from = 0) inSection: Int): IndexPath? {
        val innerIndex = dataCollection.sectionAt(inSection)?.collection?.indexOf(item)
                ?: return null
        return IndexPath(innerIndex, inSection)
    }

    fun getSectionSize(sectionIndex: Int): Int? = dataCollection.sectionAt(sectionIndex)?.size

}

open class BaseDataBindingRecyclerAdapter(context: Context) :
        AbstractDataBindingRecyclerAdapter<IRenderModelItem<*, *>>(context)


class DefaultDataBindingRecyclerAdapter(context: Context) : BaseDataBindingRecyclerAdapter(context)