package com.CommonSenseAndroidKotlin.example.fragments

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.CommonSenseAndroidKotlin.example.databinding.DemoRecyclerFastscrollSearchableBinding
import com.CommonSenseAndroidKotlin.example.databinding.SimpleListItemBinding
import com.commonsense.android.kotlin.android.extensions.widets.setup
import com.commonsense.android.kotlin.baseClasses.databinding.AbstractSearchableDataBindingRecyclerAdapter
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingFragment
import com.commonsense.android.kotlin.baseClasses.databinding.BaseSearchRenderModel
import com.commonsense.android.kotlin.baseClasses.databinding.InflatingFunction
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import org.joda.time.DateTime

/**
 * Created by Kasper Tvede on 06-06-2017.
 */


interface FastScrollListItemInterface {
    fun getTitle(): String

}

class SearchAbleSimpleListDateTime(item: DateTime) : BaseSearchRenderModel<DateTime, SimpleListItemBinding, String>(item, SimpleListItemBinding::class.java)
        , FastScrollListItemInterface {

    private val internalTitle by lazy {
        item.toString("MM/YYYY")
    }

    override fun getTitle(): String {
        return internalTitle
    }

    override fun isAcceptedByFilter(value: String): Boolean {
        return false
    }

    override fun getInflaterFunction(): InflatingFunction<SimpleListItemBinding> {
        return SimpleListItemBinding::inflate
    }

    override fun renderFunction(view: SimpleListItemBinding, model: DateTime) {
        view.simpleListText.text = model.toString()
    }


}

class FastScrollAdapter(context: Context) : AbstractSearchableDataBindingRecyclerAdapter<SearchAbleSimpleListDateTime, String>(context),
        SectionTitleProvider {
    override fun getSectionTitle(p0: Int): String {
        return getItem(p0)?.getTitle() ?: "<no title>"
    }


}

class SearchableFastScrollRecyclerDemo : BaseDatabindingFragment<DemoRecyclerFastscrollSearchableBinding>() {
    override fun createView(inflater: LayoutInflater, parent: ViewGroup?): DemoRecyclerFastscrollSearchableBinding {
        return DemoRecyclerFastscrollSearchableBinding.inflate(inflater, parent, false)
    }

    private val adapter by lazy {
        FastScrollAdapter(context)
    }


    override fun useBinding() {

        val items = (0..50000).map { SearchAbleSimpleListDateTime(DateTime.now().plusHours(it)) }

//        val items = mutableListOf(
//                SearchAbleSimpleListDateTime(DateTime.now()),
//                SearchAbleSimpleListDateTime(DateTime.now())
//        ).repeateToSize(50000)

        adapter.clearAndSet(items)
        binding.demoRecyclerSearchableRecyclerview.recyclerView.setup(adapter, LinearLayoutManager(context))
        binding.demoRecyclerSearchableFastscroll.setRecyclerView(binding.demoRecyclerSearchableRecyclerview.recyclerView)
    }

}