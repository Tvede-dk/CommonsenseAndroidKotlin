package com.commonsense.android.kotlin.views.widgets

import com.commonsense.android.kotlin.android.extensions.widets.setup
import com.commonsense.kotlin.databinding.SwipeRefreshRecylerViewBinding

/**
 * Created by Kasper Tvede on 30-05-2017.
 */
class SwipeRefreshRecyclerView : com.commonsense.android.kotlin.android.widgets.base.CustomDataBindingView<SwipeRefreshRecylerViewBinding>, android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {
    override fun inflate(): (android.view.LayoutInflater, android.view.ViewGroup, Boolean) -> com.commonsense.kotlin.databinding.SwipeRefreshRecylerViewBinding
            = com.commonsense.kotlin.databinding.SwipeRefreshRecylerViewBinding::inflate

    constructor(context: android.content.Context) : super(context)
    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(context, attrs)
    constructor(context: android.content.Context, attrs: android.util.AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        binding.swipeRefreshRecyclerSwipeRefresh.setOnRefreshListener(this)
    }


    val refreshLayout: android.support.v4.widget.SwipeRefreshLayout
        get() = binding.swipeRefreshRecyclerSwipeRefresh

    val recyclerView: android.support.v7.widget.RecyclerView
        get() = binding.swipeRefreshRecyclerRecycler


    var adapter: android.support.v7.widget.RecyclerView.Adapter<*>?
        get() = binding.swipeRefreshRecyclerRecycler.adapter
        @android.support.annotation.UiThread
        set(value) {
            binding.swipeRefreshRecyclerRecycler.adapter = value
        }

    override fun onRefresh() {
        onRefreshListener?.invoke()
    }


    var onRefreshListener: (() -> Unit)? = null

    @android.support.annotation.MainThread
    fun stopRefreshing() {
        refreshLayout.isRefreshing = false
    }

    @android.support.annotation.MainThread
    fun setup(newAdapter: android.support.v7.widget.RecyclerView.Adapter<*>, newLayoutManager: android.support.v7.widget.LinearLayoutManager, refreshCallback: () -> Unit) {
        recyclerView.setup(newAdapter, newLayoutManager)
        onRefreshListener = refreshCallback
    }

    @android.support.annotation.MainThread
    fun setupAsync(newAdapter: android.support.v7.widget.RecyclerView.Adapter<*>, newLayoutManager: android.support.v7.widget.LinearLayoutManager, refreshCallback: () -> kotlinx.coroutines.experimental.Job) {
        recyclerView.setup(newAdapter, newLayoutManager)
        onRefreshListener = {
            refreshCallback().invokeOnCompletion {
                kotlinx.coroutines.experimental.launch(kotlinx.coroutines.experimental.android.UI) { stopRefreshing() }
            }
        }
    }
}

