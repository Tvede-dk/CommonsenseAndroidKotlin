package com.commonsense.android.kotlin.helperClasses

import android.databinding.ViewDataBinding
import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.commonsense.android.kotlin.android.extensions.widets.isVisible
import com.commonsense.android.kotlin.baseClasses.databinding.AbstractDataBindingRecyclerAdapter
import com.commonsense.android.kotlin.baseClasses.databinding.BaseViewHolderItem

/**
 * Created by Kasper Tvede on 22-06-2017.
 *
 */


enum class Direction {
    startToEnd, endToStart
}

fun Int.ToDirection(): Direction? {
    return if (this == ItemTouchHelper.START) {
        Direction.startToEnd
    } else if (this == ItemTouchHelper.END) {
        Direction.endToStart
    } else {
        null
    }
}

interface SwipeableItem {
    fun getMovementFlags(): Int
    fun onSwiped(direction: Direction, viewModel: ViewDataBinding)
    fun onChildDraw(c: Canvas, viewModel: ViewDataBinding, dx: Float, dy: Float)
    fun clearView(viewModel: ViewDataBinding)
}

/**
 *
 */
class RecyclerSwipe(recyclerAdapter: AbstractDataBindingRecyclerAdapter<*>)
    : ItemTouchHelper(innerSwipeHelper(recyclerAdapter))

private class innerSwipeHelper(val recyclerAdapter: AbstractDataBindingRecyclerAdapter<*>)
    : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
            recyclerView: RecyclerView?,
            viewHolder: RecyclerView.ViewHolder?): Int {
        val optInterface = getOptInterface(viewHolder)
        if (recyclerView == null || viewHolder == null || optInterface == null) {
            return 0
        }
        return optInterface.getMovementFlags()
    }

    private fun getOptInterface(viewHolder: RecyclerView.ViewHolder?): SwipeableItem? {
        return viewHolder?.adapterPosition?.let {
            recyclerAdapter.getItem(it) as? SwipeableItem
        }
    }

    override fun onChildDraw(canvas: Canvas?,
                             recyclerView: RecyclerView?,
                             viewHolder: RecyclerView.ViewHolder?,
                             dX: Float,
                             dY: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val temp = viewHolder as? BaseViewHolderItem<*>
        if (canvas != null && temp != null) {
            getOptInterface(viewHolder)?.onChildDraw(canvas, temp.item, dX, dY)
        }
    }

    //move is reordering. we only deal with swipe for simplicity.
    override fun onMove(recyclerView: RecyclerView?,
                        beforeViewHolder: RecyclerView.ViewHolder?,
                        afterViewHolder: RecyclerView.ViewHolder?): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, directionInt: Int) {
        val optInterface = getOptInterface(viewHolder)
        val dir = directionInt.ToDirection()
        val baseViewHolder = viewHolder as? BaseViewHolderItem<*>
        if (dir != null && baseViewHolder != null && optInterface != null) {
            optInterface.onSwiped(dir, baseViewHolder.item)
        }
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }
}


class CellItemInteraction<in T : ViewDataBinding>(val mainView: (T) -> View,
                                                  val startEndView: ((T) -> View)?,
                                                  val endStartView: ((T) -> View)?,
                                                  val onStartToEnd: () -> Unit,
                                                  val onEndToStart: () -> Unit
) : SwipeableItem {

    override fun getMovementFlags(): Int {
        val swipeStart = valueOr(startEndView != null, ItemTouchHelper.START, 0)
        val swipeEnd = valueOr(endStartView != null, ItemTouchHelper.END, 0)
        val swipeFlags = swipeStart or swipeEnd
        return ItemTouchHelper.Callback.makeMovementFlags(0, swipeFlags)
    }

    private fun <T> valueOr(condition: Boolean, valueIf: T, valueOr: T): T {
        return if (condition) {
            valueIf
        } else {
            valueOr
        }
    }

    override fun onSwiped(direction: Direction, viewModel: ViewDataBinding) {
        val binding = viewModel as? T ?: return
        if (!binding.root.isVisible) {
            return
        }
        if (direction == Direction.startToEnd) {
            onEndToStart()
        } else {
            onStartToEnd()
        }
    }

    override fun onChildDraw(c: Canvas, viewModel: ViewDataBinding, dx: Float, dy: Float) {
        val binding = viewModel as? T ?: return

        val main = mainView(binding)
        val start = startEndView?.invoke(binding)
        val end = endStartView?.invoke(binding)
        binding.root.translationX = 0F
        main.translationX = dx
        if (!main.isVisible) {
            return
        }
        if (dx > 0) {
            start?.visibility = View.VISIBLE
            end?.visibility = View.GONE
        } else {
            start?.visibility = View.GONE
            end?.visibility = View.VISIBLE
        }

    }

    override fun clearView(viewModel: ViewDataBinding) {
        val binding = viewModel as? T ?: return
        //rest all state here. (that we did modify).
        val main = mainView(binding)
        val start = startEndView?.invoke(binding)
        val end = endStartView?.invoke(binding)
        main.visibility = View.VISIBLE
        end?.visibility = View.GONE
        start?.visibility = View.GONE
    }


}

