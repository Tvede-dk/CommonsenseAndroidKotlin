package com.commonsense.android.kotlin.widgets

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.commonsense.android.kotlin.android.extensions.widets.setup
import com.commonsense.kotlin.databinding.SwipeRefreshRecylerViewBinding

/**
 * Created by Kasper Tvede on 30-05-2017.
 */
class SwipeRefreshRecyclerView : FrameLayout, SwipeRefreshLayout.OnRefreshListener {


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val viewBinding: SwipeRefreshRecylerViewBinding

    init {
        viewBinding = SwipeRefreshRecylerViewBinding.inflate(LayoutInflater.from(context), this, true)
        viewBinding.swipeRefreshRecyclerSwipeRefresh.setOnRefreshListener(this)
    }


    val refreshLayout: SwipeRefreshLayout
        get() = viewBinding.swipeRefreshRecyclerSwipeRefresh

    val recyclerView: RecyclerView
        get() = viewBinding.swipeRefreshRecyclerRecycler

    var adapter: RecyclerView.Adapter<*>?
        get() = viewBinding.swipeRefreshRecyclerRecycler.adapter
        set(value) {
            viewBinding.swipeRefreshRecyclerRecycler.adapter = value
        }

    override fun onRefresh() {
        onRefreshListener?.invoke()
    }


    var onRefreshListener: (() -> Unit)? = null

    fun stopRefreshing() {
        refreshLayout.isRefreshing = false
    }

    fun setupAll(newAdapter: RecyclerView.Adapter<*>, newLayoutManager: LinearLayoutManager, refreshCallback: () -> Unit) {
        recyclerView.setup(newAdapter, newLayoutManager)
        onRefreshListener = refreshCallback
    }
}
