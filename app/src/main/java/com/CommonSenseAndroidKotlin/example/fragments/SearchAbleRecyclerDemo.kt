package com.CommonSenseAndroidKotlin.example.fragments

import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import com.CommonSenseAndroidKotlin.example.databinding.DemoRecyclerSearchableBinding
import com.CommonSenseAndroidKotlin.example.databinding.SimpleListItemBinding
import com.commonsense.android.kotlin.android.extensions.widets.setup
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingFragment
import com.commonsense.android.kotlin.baseClasses.databinding.BaseSearchableDataBindingRecyclerView
import com.commonsense.android.kotlin.baseClasses.databinding.IRenderModelSearchItem
import com.commonsense.android.kotlin.baseClasses.databinding.toSearchable

/**
 * Created by kasper on 01/06/2017.
 */

class SearchAbleSimpleListItemRender(item: String) : SimpleListItemRender(item), IRenderModelSearchItem<String, SimpleListItemBinding, String> {
    override fun isAcceptedByFilter(value: String): Boolean = item.contains(value)
}

class SearchAbleRecyclerDemo : BaseDatabindingFragment<DemoRecyclerSearchableBinding>() {
    override fun createView(inflater: LayoutInflater, parent: ViewGroup?): DemoRecyclerSearchableBinding = DemoRecyclerSearchableBinding.inflate(inflater, parent, false)

    private val adapter by lazy {
        BaseSearchableDataBindingRecyclerView<String>(context.applicationContext)
    }

    override fun useBinding() {

        adapter.clear()
        for (i in 0..50000) {
            adapter.add(SearchAbleSimpleListItemRender("First $i"))
            adapter.add(SimpleListImageItemRender(Color.BLUE).toSearchable { _, _ -> false })
            adapter.add(SearchAbleSimpleListItemRender("Whats up $i"))
            adapter.add(SimpleListImageItemRender(Color.RED).toSearchable { _, _ -> false })
        }

        binding.demoRecyclerSearchableRecyclerview.recyclerView.setup(adapter, LinearLayoutManager(context.applicationContext))
        binding.demoRecyclerSearchableEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val temp = s?.toString()
                if (temp.isNullOrEmpty()) {
                    adapter.removeFilter()
                } else {
                    temp?.let(adapter::filterBy)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }


}