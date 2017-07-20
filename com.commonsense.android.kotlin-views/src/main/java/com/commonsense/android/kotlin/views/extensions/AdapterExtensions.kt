package com.commonsense.android.kotlin.views.extensions

import android.support.v7.widget.RecyclerView

/**
 * Created by Kasper Tvede on 31-05-2017.
 */
fun RecyclerView.setup(adapter: RecyclerView.Adapter<*>, layoutManager: RecyclerView.LayoutManager) {
    this.adapter = adapter
    this.layoutManager = layoutManager
}

/*fun <T : IRenderModelItem<*, *>> AbstractDataBindingRecyclerAdapter<T>.isIndexValid(index: Int): Boolean {
    return index in 0 until  itemCount
}*/
/*
fun <T : IRenderModelItem<*, *>> AbstractDataBindingRecyclerAdapter<T>.getLastItem(): IRenderModelItem<*, *>? {
    return getItem(itemCount - 1)
}

fun AbstractDataBindingRecyclerAdapter<*>.removeLast() {
    removeAt(itemCount - 1)
}*/