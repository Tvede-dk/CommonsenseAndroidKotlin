package com.commonsense.android.kotlin.android.extensions.widets

import com.commonsense.android.kotlin.baseClasses.databinding.BaseDataBindingRecyclerView
import com.commonsense.android.kotlin.baseClasses.databinding.IRenderModelItem

/**
 * Created by Kasper Tvede on 31-05-2017.
 */


fun <T : IRenderModelItem<*, *>> BaseDataBindingRecyclerView.clearAndSet(items: List<T>) {
    clear()
    addAll(items)
}