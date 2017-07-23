package com.CommonSenseAndroidKotlin.example.fragments

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import com.CommonSenseAndroidKotlin.example.databinding.DemoRecyclerFastscrollSearchableBinding
import com.CommonSenseAndroidKotlin.example.databinding.SimpleListItemBinding
import com.commonsense.android.kotlin.views.ViewInflatingFunction
import com.commonsense.android.kotlin.views.databinding.adapters.AbstractSearchableDataBindingRecyclerAdapter
import com.commonsense.android.kotlin.views.databinding.adapters.BaseSearchRenderModel
import com.commonsense.android.kotlin.views.databinding.adapters.BaseViewHolderItem
import com.commonsense.android.kotlin.views.databinding.fragments.BaseDatabindingFragment
import com.commonsense.android.kotlin.views.databinding.fragments.InflateBinding
import com.commonsense.android.kotlin.views.extensions.setup
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
    override fun renderFunction(view: SimpleListItemBinding, model: DateTime, viewHolder: BaseViewHolderItem<SimpleListItemBinding>) {
        view.simpleListText.text = model.toString()

    }

    private val internalTitle by lazy {
        item.toString("MM/YYYY")
    }

    override fun getTitle(): String {
        return internalTitle
    }

    override fun isAcceptedByFilter(value: String): Boolean {
        return false
    }

    override fun getInflaterFunction(): ViewInflatingFunction<SimpleListItemBinding> =
            SimpleListItemBinding::inflate


}

class FastScrollAdapter(context: Context) : AbstractSearchableDataBindingRecyclerAdapter<SearchAbleSimpleListDateTime, String>(context),
        SectionTitleProvider {
    override fun getSectionTitle(p0: Int): String {
        return getItem(p0, 0)?.getTitle() ?: "<no title>"
    }


}

class SearchableFastScrollRecyclerDemo : BaseDatabindingFragment<DemoRecyclerFastscrollSearchableBinding>() {
    override fun getInflater(): InflateBinding<DemoRecyclerFastscrollSearchableBinding>
            = DemoRecyclerFastscrollSearchableBinding::inflate

    private val adapter by lazy {
        FastScrollAdapter(context)
    }


    override fun useBinding() {

        val items = (0 until 50000).map { SearchAbleSimpleListDateTime(DateTime.now().plusHours(it)) }

//        val items = mutableListOf(
//                SearchAbleSimpleListDateTime(DateTime.now()),
//                SearchAbleSimpleListDateTime(DateTime.now())
//        ).repeateToSize(50000)

        adapter.setSection(items, 0)
        binding.demoRecyclerSearchableRecyclerview.recyclerView.setup(adapter, LinearLayoutManager(context))
        binding.demoRecyclerSearchableFastscroll.setRecyclerView(binding.demoRecyclerSearchableRecyclerview.recyclerView)
    }

}
