@file:Suppress("unused")
package com.commonsense.android.kotlin.views.features

import android.databinding.*
import android.graphics.*
import android.support.v7.widget.*
import android.support.v7.widget.helper.*
import android.view.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.views.databinding.adapters.*
import com.commonsense.android.kotlin.views.extensions.*

/**
 * Created by Kasper Tvede on 22-06-2017.
 *
 */


enum class Direction {
    StartToEnd, EndToStart
}

fun Int.toDirection(): Direction? =
        when (this) {
            ItemTouchHelper.START -> Direction.StartToEnd
            ItemTouchHelper.END -> Direction.EndToStart
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
class RecyclerSwipe(recyclerAdapter: DataBindingRecyclerAdapter<*>)
    : ItemTouchHelper(InnerSwipeHelper(recyclerAdapter))

private data class SwipeItemViews(val startView: View?, val endView: View?, val mainView: View)

private class InnerSwipeHelper(val recyclerAdapter: DataBindingRecyclerAdapter<*>)
    : ItemTouchHelper.Callback() {

    private var lastDirection: Direction? = null

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
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


    override fun onChildDraw(canvas: Canvas,
                             recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float,
                             dY: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (viewHolder.adapterPosition < 0) {
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

        lastDirection = when {
            dX in (-0.1f..0.1f) -> { //nothing is visible. just hide it
                ViewHelper.goneViews(startView, endView)
                null
            }
            dX > 0 -> { //else, what side.
                ViewHelper.showGoneView(startView, endView)
                Direction.StartToEnd
            }
            else -> {
                ViewHelper.showGoneView(endView, startView)
                Direction.EndToStart
            }
        }
    }


    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        val swipeItem = getOptInterface(viewHolder) ?: return
        val baseViewBinding = viewHolder as? BaseViewHolderItem<*> ?: return
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            resetItem(swipeItem, baseViewBinding.item)
        }

    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
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
    override fun onMove(recyclerView: RecyclerView,
                        beforeViewHolder: RecyclerView.ViewHolder,
                        afterViewHolder: RecyclerView.ViewHolder): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, directionInt: Int) {
        val optInterface = getOptInterface(viewHolder)
        val dir = directionInt.toDirection()
        val baseViewHolder = viewHolder as? BaseViewHolderItem<*>
        if (dir != null && baseViewHolder != null && optInterface != null) {
            //use our own logic, since the dir from the item touch helper can sometimes be reveresed.
            //if we do not have any clue, use the helper class's idea.
            optInterface.onSwiped(lastDirection ?: dir, baseViewHolder.item)
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

