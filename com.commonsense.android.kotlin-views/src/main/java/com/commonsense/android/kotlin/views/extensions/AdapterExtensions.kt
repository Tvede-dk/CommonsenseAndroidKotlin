package com.commonsense.android.kotlin.views.extensions

import android.support.v7.widget.RecyclerView

/**
 * Created by Kasper Tvede on 31-05-2017.
 */
fun RecyclerView.setup(adapter: RecyclerView.Adapter<*>, layoutManager: RecyclerView.LayoutManager) {
    this.adapter = adapter
    this.layoutManager = layoutManager
}
