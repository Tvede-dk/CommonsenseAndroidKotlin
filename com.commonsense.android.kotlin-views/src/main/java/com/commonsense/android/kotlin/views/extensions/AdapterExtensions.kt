package com.commonsense.android.kotlin.views.extensions

import android.support.v7.widget.RecyclerView
import com.commonsense.android.kotlin.base.FunctionUnit

/**
 * Created by Kasper Tvede on 31-05-2017.
 */
fun RecyclerView.setup(adapter: RecyclerView.Adapter<*>, layoutManager: RecyclerView.LayoutManager) {
    this.adapter = adapter
    this.layoutManager = layoutManager
}


inline fun RecyclerView.addOnScrollWhenPastFirstItem(crossinline action: FunctionUnit<Boolean>): RecyclerView.OnScrollListener {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (recyclerView == null) {
                return
            }
            val isFirstVisible = recyclerView.findViewHolderForLayoutPosition(0) != null
            action(isFirstVisible)
        }
    }
    addOnScrollListener(listener)
    return listener
}