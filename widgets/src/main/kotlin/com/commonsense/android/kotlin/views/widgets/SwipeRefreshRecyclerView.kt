package com.commonsense.android.kotlin.views.widgets

import android.content.Context
import android.support.annotation.UiThread
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.commonsense.android.kotlin.base.extensions.launchOnCompleted
import com.commonsense.android.kotlin.views.databinding.CustomDataBindingView
import com.commonsense.android.kotlin.views.databinding.InflaterFunction
import com.commonsense.android.kotlin.views.databinding.SwipeRefreshRecylerViewBinding
import com.commonsense.android.kotlin.views.extensions.setup
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI


/**
 * Created by Kasper Tvede on 30-05-2017.
 */
class SwipeRefreshRecyclerView : CustomDataBindingView<SwipeRefreshRecylerViewBinding>, SwipeRefreshLayout.OnRefreshListener {
    override fun getStyleResource(): IntArray? = null

    override fun updateView() {}

    override fun afterSetupView() {}

    override fun inflate(): InflaterFunction<SwipeRefreshRecylerViewBinding>
            = SwipeRefreshRecylerViewBinding::inflate

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        binding.swipeRefreshRecyclerSwipeRefresh.setOnRefreshListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        binding.swipeRefreshRecyclerSwipeRefresh.setOnRefreshListener(null)
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

    @UiThread
    fun stopRefreshing() {
        refreshLayout.isRefreshing = false
    }

    @UiThread
    fun setup(newAdapter: RecyclerView.Adapter<*>, newLayoutManager: LinearLayoutManager, refreshCallback: () -> Unit) {
        recyclerView.setup(newAdapter, newLayoutManager)
        onRefreshListener = refreshCallback
    }

    @UiThread
    fun setupAsync(newAdapter: RecyclerView.Adapter<*>, newLayoutManager: LinearLayoutManager, refreshCallback: () -> Job) {
        recyclerView.setup(newAdapter, newLayoutManager)
        onRefreshListener = {
            refreshCallback().launchOnCompleted(UI) {
                stopRefreshing()
            }
        }
    }
}

