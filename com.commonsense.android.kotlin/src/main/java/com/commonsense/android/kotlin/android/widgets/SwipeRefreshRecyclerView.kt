package com.commonsense.android.kotlin.android.widgets

import android.content.Context
import android.support.annotation.MainThread
import android.support.annotation.UiThread
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import com.commonsense.android.kotlin.android.extensions.widets.setup
import com.commonsense.android.kotlin.android.widgets.base.CustomDataBindingView
import com.commonsense.kotlin.databinding.SwipeRefreshRecylerViewBinding
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by Kasper Tvede on 30-05-2017.
 */
class SwipeRefreshRecyclerView : CustomDataBindingView<SwipeRefreshRecylerViewBinding>, SwipeRefreshLayout.OnRefreshListener {
    override fun inflate(): (LayoutInflater, ViewGroup, Boolean) -> SwipeRefreshRecylerViewBinding
            = SwipeRefreshRecylerViewBinding::inflate

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        binding.swipeRefreshRecyclerSwipeRefresh.setOnRefreshListener(this)
    }


    val refreshLayout: SwipeRefreshLayout
        get() = binding.swipeRefreshRecyclerSwipeRefresh

    val recyclerView: RecyclerView
        get() = binding.swipeRefreshRecyclerRecycler


    var adapter: RecyclerView.Adapter<*>?
        get() = binding.swipeRefreshRecyclerRecycler.adapter
        @UiThread
        set(value) {
            binding.swipeRefreshRecyclerRecycler.adapter = value
        }

    override fun onRefresh() {
        onRefreshListener?.invoke()
    }


    var onRefreshListener: (() -> Unit)? = null

    @MainThread
    fun stopRefreshing() {
        refreshLayout.isRefreshing = false
    }

    @MainThread
    fun setup(newAdapter: RecyclerView.Adapter<*>, newLayoutManager: LinearLayoutManager, refreshCallback: () -> Unit) {
        recyclerView.setup(newAdapter, newLayoutManager)
        onRefreshListener = refreshCallback
    }

    @MainThread
    fun setupAsync(newAdapter: RecyclerView.Adapter<*>, newLayoutManager: LinearLayoutManager, refreshCallback: () -> Job) {
        recyclerView.setup(newAdapter, newLayoutManager)
        onRefreshListener = {
            refreshCallback().invokeOnCompletion {
                launch(UI) { stopRefreshing() }
            }
        }
    }
}

