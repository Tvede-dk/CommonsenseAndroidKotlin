package com.commonsense.android.kotlin.views.extensions

import android.support.v7.widget.RecyclerView
import com.commonsense.android.kotlin.base.FunctionUnit
import com.commonsense.android.kotlin.base.extensions.collections.length

/**
 * Setup this recycler view with an adapter and a layout manager
 * @receiver RecyclerView
 * @param adapter RecyclerView.Adapter<*> the adapter to use
 * @param layoutManager RecyclerView.LayoutManager the layoutmanager to use
 */
fun RecyclerView.setup(adapter: RecyclerView.Adapter<*>,
                       layoutManager: RecyclerView.LayoutManager) {
    this.adapter = adapter
    this.layoutManager = layoutManager
}

/**
 * adds an onscroll listener that calls the given action when we have scrolled past the first item in the adapter
 * @receiver RecyclerView
 * @param action FunctionUnit<Boolean>
 * @return RecyclerView.OnScrollListener
 */
inline fun RecyclerView.addOnScrollWhenPastFirstItem(crossinline action: FunctionUnit<Boolean>): RecyclerView.OnScrollListener {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (recyclerView == null) {
                return
            }
            val isFirstVisible = recyclerView.findViewHolderForLayoutPosition(0) == null
            action(isFirstVisible)
        }
    }
    addOnScrollListener(listener)
    return listener
}


/**
 * Calls notify range changed on the given range.
 * There are no validation whenever the range exists or not.
 * uses the range.start and the range.length as the item count.
 * @receiver RecyclerView.Adapter<*>
 * @param range IntRange
 */
@Suppress("NOTHING_TO_INLINE")
inline fun RecyclerView.Adapter<*>.notifyItemRangeChanged(range: IntRange) {
    notifyItemRangeChanged(range.start, range.length)
}

/**
 * Scrolls this recycler view to the top.
 * @receiver RecyclerView
 * @param shouldScrollSmooth Boolean whenever this should be performed smoothly (animated) or instantly; if true then it will be smoothly
 */
fun RecyclerView.scrollToTop(shouldScrollSmooth: Boolean = true) {
    if (shouldScrollSmooth) {
        smoothScrollToPosition(0)
    } else {
        layoutManager?.scrollToPosition(0)
    }
}

/**
 * Scrolls this recycler view to the bottom.
 * @receiver RecyclerView
 * @param shouldScrollSmooth Boolean whenever this should be performed smoothly (animated) or instantly; if true then it will be smoothly
 */

fun RecyclerView.scrollToBottom(shouldScrollSmooth: Boolean = true) {
    val lastPositionPlus1 = maxOf(adapter?.itemCount ?: 0, 1) // [1 -> ??]
    val lastPosition = lastPositionPlus1 - 1 //go into bounds
    if (shouldScrollSmooth) {
        smoothScrollToPosition(lastPosition)
    } else {
        layoutManager?.scrollToPosition(lastPosition)
    }
}
//
