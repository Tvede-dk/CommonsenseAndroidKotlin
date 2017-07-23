package com.commonsense.android.kotlin.views.features

import android.databinding.ViewDataBinding
import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.commonsense.android.kotlin.base.extensions.collections.forEachNotNull
import com.commonsense.android.kotlin.base.extensions.collections.map
import com.commonsense.android.kotlin.views.databinding.adapters.AbstractDataBindingRecyclerAdapter
import com.commonsense.android.kotlin.views.databinding.adapters.AbstractSearchableDataBindingRecyclerAdapter
import com.commonsense.android.kotlin.views.databinding.adapters.BaseDataBindingRecyclerAdapter
import com.commonsense.android.kotlin.views.databinding.adapters.BaseViewHolderItem
import com.commonsense.android.kotlin.views.extensions.ViewHelper
import com.commonsense.android.kotlin.views.extensions.resetTransformations
import com.commonsense.android.kotlin.views.extensions.visible

/**
 * Created by Kasper Tvede on 22-06-2017.
 *
 */


enum class Direction {
    startToEnd, endToStart
}

fun Int.ToDirection(): Direction? =
        when (this) {
            ItemTouchHelper.START -> Direction.startToEnd
            ItemTouchHelper.END -> Direction.endToStart
            else -> null
        }

interface SwipeableItem {
    fun onSwiped(direction: Direction, viewModel: ViewDataBinding)

    fun startView(binding: ViewDataBinding): View?

    fun endView(binding: ViewDataBinding): View?

    fun floatingView(binding: ViewDataBinding): View

}

fun BaseDataBindingRecyclerAdapter.attachSwipeFeature(
        view: RecyclerView) {
    val swipe = RecyclerSwipe(this)
    swipe.attachToRecyclerView(view)
}


fun AbstractSearchableDataBindingRecyclerAdapter<*, *>.attachSwipeFeature(
        view: RecyclerView) {
    val swipe = RecyclerSwipe(this)
    swipe.attachToRecyclerView(view)
}

/**
 *
 */
class RecyclerSwipe(recyclerAdapter: AbstractDataBindingRecyclerAdapter<*>)
    : ItemTouchHelper(innerSwipeHelper(recyclerAdapter))

private data class SwipeItemViews(val startView: View?, val endView: View?, val mainView: View)

private class innerSwipeHelper(val recyclerAdapter: AbstractDataBindingRecyclerAdapter<*>)
    : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val swipeItem = getOptInterface(viewHolder) ?: return 0
        val baseViewHolder = viewHolder as? BaseViewHolderItem<*> ?: return 0
        val (startView, endView, _) = getViews(swipeItem, baseViewHolder.item)
        val startMovement = (startView != null).map(ifTrue = ItemTouchHelper.START, ifFalse = 0)
        val endMovement = (endView != null).map(ifTrue = ItemTouchHelper.END, ifFalse = 0)

        return ItemTouchHelper.Callback.makeMovementFlags(0, startMovement or endMovement)
    }

    private fun getOptInterface(viewHolder: RecyclerView.ViewHolder?)
            : SwipeableItem? {
        return viewHolder?.adapterPosition?.let {
            recyclerAdapter.getItemFromRawIndex(it) as? SwipeableItem
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
        if (viewHolder?.adapterPosition ?: -1 < 0) {
            return
        }
        val swipeItem = getOptInterface(viewHolder) ?: return
        val baseViewBinding = viewHolder as? BaseViewHolderItem<*> ?: return
        val (startView, endView, mainView) = getViews(swipeItem, baseViewBinding.item)
        baseViewBinding.item.root.translationX = 0f
        mainView.translationX = dX

        if (!isCurrentlyActive) {
            /*  baseViewBinding.item.root.postDelayed({
                  resetItem(swipeItem, baseViewBinding.item)
              }, getAnimationDuration(recyclerView, DEFAULT_SWIPE_ANIMATION_DURATION, dX, dY))*/
            return
        }

        when {
            dX in (-0.1f..0.1f) -> //nothing is visible. just hide it
                ViewHelper.goneViews(startView, endView)
            dX > 0 -> //else, what side.
                ViewHelper.showGoneView(startView, endView)
            else -> {
                ViewHelper.showGoneView(endView, startView)
            }
        }
    }

    override fun onChildDrawOver(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        val swipeItem = getOptInterface(viewHolder) ?: return
        val baseViewBinding = viewHolder as? BaseViewHolderItem<*> ?: return
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            resetItem(swipeItem, baseViewBinding.item)
        }

    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        super.clearView(recyclerView, viewHolder)
        val view = (viewHolder as? BaseViewHolderItem<*>)?.item ?: return
        val swipeInterface = getOptInterface(viewHolder) ?: return
        resetItem(swipeInterface, view)
    }

    private fun resetItem(swipeInterface: SwipeableItem, view: ViewDataBinding) {
        val (startView, endView, mainView) = getViews(swipeInterface, view)
        resetViews(mainView, startView, endView, view.root)
    }


    private fun getViews(swipeableItem: SwipeableItem, viewModel: ViewDataBinding): SwipeItemViews {
        val startView: View? = swipeableItem.startView(viewModel)
        val endView: View? = swipeableItem.endView(viewModel)
        val mainView: View = swipeableItem.floatingView(viewModel)
        return SwipeItemViews(startView, endView, mainView)
    }

    //move is reordering. we only deal with swipe for simplicity.
    override fun onMove(recyclerView: RecyclerView?,
                        beforeViewHolder: RecyclerView.ViewHolder?,
                        afterViewHolder: RecyclerView.ViewHolder?): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, directionInt: Int) {
        val optInterface = getOptInterface(viewHolder)
        val dir = directionInt.ToDirection()
        val baseViewHolder = viewHolder as? BaseViewHolderItem<*>
        if (dir != null && baseViewHolder != null && optInterface != null) {
            optInterface.onSwiped(dir, baseViewHolder.item)
        }
    }

    override fun isLongPressDragEnabled(): Boolean = false

    override fun isItemViewSwipeEnabled(): Boolean = true
}

private fun resetViews(mainView: View, startView: View?, endView: View?, rootView: View) {
    ViewHelper.goneViews(startView, endView)
    mainView.visible()
    //remove transformations from all views.
    listOf(mainView, rootView, endView, startView).forEachNotNull(View::resetTransformations)
}


fun SwipeableItem.clearSwipe(mainView: View, startView: View?, endView: View?, rootView: View) {
    resetViews(mainView, startView, endView, rootView)
}

