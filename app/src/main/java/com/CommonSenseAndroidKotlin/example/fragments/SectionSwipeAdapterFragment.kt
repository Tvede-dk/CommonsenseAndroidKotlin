package com.CommonSenseAndroidKotlin.example.fragments

import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.CommonSenseAndroidKotlin.example.databinding.DemoRecyclerSectionSwipeBinding
import com.CommonSenseAndroidKotlin.example.databinding.SimpleSwipeItemBinding
import com.commonsense.android.kotlin.baseClasses.databinding.*
import com.commonsense.android.kotlin.helperClasses.Direction
import com.commonsense.android.kotlin.helperClasses.SwipeableItem
import com.commonsense.android.kotlin.helperClasses.attachSwipeFeature

/**
 * Created by Kasper Tvede on 24-06-2017.
 */
class SectionSwipeAdapterFragment : BaseDatabindingFragment<DemoRecyclerSectionSwipeBinding>() {
    override fun getInflater(): InflateBinding<DemoRecyclerSectionSwipeBinding>
            = DemoRecyclerSectionSwipeBinding::inflate

    val adapter by lazy {
        BaseSearchableDataBindingRecyclerAdapter<String>(context)
    }


    override fun useBinding() {
        LaunchInBackground {
            val items = (0..100).map { SimpleSwipeItem("") { adapter.removeAt(it) } }
            LaunchInUi {
                adapter.addAll(items)
                adapter.attachSwipeFeature(binding.demoRecyclerSearchableRecyclerview.recyclerView)
                binding.demoRecyclerSearchableRecyclerview.setupAsync(adapter, LinearLayoutManager(context), this::refresh)
            }
        }

    }

    fun refresh() = LaunchInBackground {

    }

}


class SimpleSwipeItem(value: String, val deleteAction: () -> Unit) :
        BaseSearchRenderModel<String, SimpleSwipeItemBinding, String>(value, SimpleSwipeItemBinding::class.java),
        SwipeableItem {
    override fun startView(binding: ViewDataBinding): View? {
        val ourBinding = binding as? SimpleSwipeItemBinding ?: return null
        return ourBinding.simpleSwipeItemDelete
    }

    override fun endView(binding: ViewDataBinding): View? {
        val ourBinding = binding as? SimpleSwipeItemBinding ?: return null
        return ourBinding.simpleSwipeItemSolve
    }

    override fun floatingView(binding: ViewDataBinding): View {
        val ourBinding = binding as? SimpleSwipeItemBinding ?: return binding.root
        return ourBinding.simpleSwipeItemContent
    }

    override fun onSwiped(direction: Direction, viewModel: ViewDataBinding) {
        deleteAction()
    }

    override fun isAcceptedByFilter(value: String): Boolean {
        return item.contains(value, ignoreCase = true)
    }

    override fun renderFunction(view: SimpleSwipeItemBinding, model: String, viewHolder: BaseViewHolderItem<SimpleSwipeItemBinding>) {
//        view.simpleSwipeItemContent.translationX = 0f
//        view.simpleSwipeItemContent.visibility = View.VISIBLE
//
//        view.simpleSwipeItemDelete.visibility = View.GONE
//        view.simpleSwipeItemSolve.visibility = View.GONE
    }

    override fun getInflaterFunction(): ViewInflatingFunction<SimpleSwipeItemBinding>
            = SimpleSwipeItemBinding::inflate


}