package com.commonsense.android.kotlin.android.extensions.widets

import android.support.v7.widget.RecyclerView
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDataBindingRecyclerView
import com.commonsense.android.kotlin.baseClasses.databinding.IRenderModelItem

/**
 * Created by Kasper Tvede on 31-05-2017.
 */


fun <T : IRenderModelItem<*, *>> BaseDataBindingRecyclerView.clearAndSet(items: List<T>) {
    clear()
    addAll(items)
}


fun RecyclerView.setup(adapter: RecyclerView.Adapter<*>, layoutManager: RecyclerView.LayoutManager) {
    this.adapter = adapter
    this.layoutManager = layoutManager
}