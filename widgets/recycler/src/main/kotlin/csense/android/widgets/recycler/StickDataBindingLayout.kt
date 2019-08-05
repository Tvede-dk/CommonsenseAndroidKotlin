package csense.android.widgets.recycler

import android.support.v7.widget.*
import android.util.*
import android.view.*
import com.commonsense.android.kotlin.base.extensions.*
import kotlin.system.*

class StickDataBindingVerticalLayout(autoDrawingOrder: Boolean) : RecyclerView.LayoutManager() {

    private val mHelper = StickDataBindingVerticalLayoutImpl(autoDrawingOrder, this)


    override fun canScrollVertically(): Boolean = true

    override fun canScrollHorizontally(): Boolean = false

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
            RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        mHelper.onLayoutChildren(recycler, state)
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        return mHelper.scrollVerticallyBy(dy, recycler, state)
    }

    override fun scrollToPosition(position: Int) {
        mHelper.scrollToPosition(position)
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        mHelper.smoothScrollToPosition(recyclerView, state, position)
    }

    override fun onAdapterChanged(oldAdapter: RecyclerView.Adapter<*>?, newAdapter: RecyclerView.Adapter<*>?) {
        mHelper.onAdapterChanged(newAdapter)
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        mHelper.onAttachedToWindow(view)
        if (view.adapter != null) {
            onAdapterChanged(null, view.adapter)
        }
    }

    override fun onDetachedFromWindow(view: RecyclerView?, recycler: RecyclerView.Recycler?) {
        super.onDetachedFromWindow(view, recycler)
        mHelper.onDetachedFromWindow()
    }

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }
}

class StickDataBindingVerticalLayoutImpl(
        val autoDrawingOrder: Boolean,
        self: RecyclerView.LayoutManager) {

    private var mCurrentAdapter: BaseDataBindingRecyclerAdapter? = null

    private var mCurrentRecyclerView: RecyclerView? = null

    fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        val self = weakLayoutManager.get() ?: return
        if (self.childCount < 1) {
            self.detachAndScrapAttachedViews(recycler)

            val parentLeft: Int = calcParentLeft()
            val parentRight: Int = calcParentRight()
            val parentBottom: Int = calcParentBottom()
            //start with padding top.
            var nextTop = self.paddingTop

            for (i in 0 until state.itemCount) {
                //out of view ?
                if (nextTop >= parentBottom) {
                    break
                }
                val v = recycler.getViewForPosition(i)
                val bottom = self.addViewAndMeasureHeight(v, i, nextTop)
                self.layoutDecoratedWithMargins(v, parentLeft, nextTop, parentRight, bottom)
                nextTop = bottom
            }
        } else {
            val items = mCurrentAdapter?.itemCount ?: 0

            Log.e("omg", "here")
        }
    }

    private fun RecyclerView.LayoutManager.addViewAndMeasureHeight(viewToAdd: View, viewIndex: Int, currentTopLocation: Int): Int {
        addView(viewToAdd, viewIndex)
        measureChildWithMargins(viewToAdd, 0, 0)
        return currentTopLocation + getDecoratedMeasuredHeight(viewToAdd)
    }

    internal fun calcParentLeft(): Int {
        return weakLayoutManager.get()?.paddingLeft ?: 0
    }

    internal fun calcParentRight(): Int {
        return (weakLayoutManager.get()?.height ?: 0) - (weakLayoutManager.get()?.paddingRight ?: 0)
    }

    private fun calcParentBottom(): Int {
        return calcParentHeight() - (weakLayoutManager.get()?.paddingBottom ?: 0)
    }

    internal fun calcParentHeight(): Int {
        return weakLayoutManager.get()?.height ?: 0
    }

    fun onDetachedFromWindow() {
        mCurrentRecyclerView = null
    }

    fun onAdapterChanged(newAdapter: RecyclerView.Adapter<*>?) {
        if (newAdapter is BaseDataBindingRecyclerAdapter) {
            mCurrentAdapter = newAdapter
        } else {
            //reset / disable state
            mCurrentRecyclerView = null
            mCurrentAdapter = null
            mCurrentStickyView = null
        }
    }

    private val weakLayoutManager = self.weakReference()

    private var mCurrentStickyView: View? = null

    fun scrollToPosition(position: Int) {
        throw UnsupportedOperationException()
    }

    fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        throw UnsupportedOperationException()
    }

    fun onAttachedToWindow(view: RecyclerView) {
        mCurrentRecyclerView = view
        if (autoDrawingOrder) {
            view.setChildDrawingOrderCallback { childCount, index ->
                val previousStickyChildPosition = mPreviousStickySection?.let {
                    getChildPositionByAdapterPosition(findStickyPositionByRawIndex(it))
                }

                val currentStickyChildPosition = mLastStickySection?.let {
                    getChildPositionByAdapterPosition(findStickyPositionByRawIndex(it))
                }

                return@setChildDrawingOrderCallback if (previousStickyChildPosition != null && index == childCount - 2) {
                    previousStickyChildPosition
                } else if (currentStickyChildPosition != null && index == childCount - 1) {
                    currentStickyChildPosition
                } else {
                    val firstSticky = Math.min(
                            previousStickyChildPosition ?: Integer.MAX_VALUE,
                            currentStickyChildPosition ?: Integer.MAX_VALUE
                    )
                    val lastSticky: Int = if (previousStickyChildPosition != null && currentStickyChildPosition != null) {
                        Math.max(
                                previousStickyChildPosition,
                                currentStickyChildPosition
                        )
                    } else {
                        Integer.MAX_VALUE
                    }

                    val increment: Int
                    increment = when {
                        index > lastSticky - 2 -> 2
                        index > firstSticky - 1 -> 1
                        else -> 0
                    }
                    index + increment
                }
            }
        }
    }

    private fun getChildPositionByAdapterPosition(adapterPosition: Int): Int? {
        val self = weakLayoutManager.get() ?: return null
        var i = 0
        self.forEachChild {
            if (getAdapterPositionByView(it) == adapterPosition) {
                return i
            }
            i += 1
        }
        return null
    }

    internal fun isStickyViewItemType(v: View): Boolean {
        return v is StickLinearLayout
    }

    fun findStickyPositionByRawIndex(rawIndex: Int): Int {
        val self = mCurrentAdapter ?: return 0
        if (self.getItemFromRawIndex(rawIndex) != null) {
            return rawIndex
        }
        return RecyclerView.NO_POSITION
    }

    internal fun getAdapterPositionByView(v: View): Int? {
        return mCurrentRecyclerView?.getChildAdapterPosition(v)
    }


    private var mPreviousStickySection: Int? = null
    private var mLastStickySection: Int? = null


    fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val self = weakLayoutManager.get() ?: return 0
        val adapter = mCurrentAdapter ?: return 0
        var result = 0
        val timeInMs = measureTimeMillis {
            result = VerticalScrollHandler.scrollVerticallyBy(dy, recycler, state, this, adapter, self)
        }
        Log.e("scroll", "took $timeInMs ms for $result px")
        return result
    }

    internal fun getFirstNormalChild(): View? {
        val self = weakLayoutManager.get() ?: return null
        var resolved: View? = null
        val currentSticky = getCurrentSticky()
        val isCurrentStickyNull = currentSticky == null
        var lastMinTop = Integer.MAX_VALUE
        self.forEachChild { v ->
            if (isCurrentStickyNull || v !== currentSticky) {
                val top: Int = self.getDecoratedTop(v)
                if (top < lastMinTop) {
                    lastMinTop = top
                    resolved = v
                }
            }
        }
        return resolved
    }

    internal fun getLastNormalChild(): View? {
        val self = weakLayoutManager.get() ?: return null
        val currentSticky = getCurrentSticky()
        val doesNotHaveSticky = currentSticky == null
        val childPositionForSticky = currentSticky?.let { mCurrentRecyclerView?.getChildAdapterPosition(it) }
        var resolved: View? = null
        var lastMaxBottom = Integer.MIN_VALUE
        self.forEachChildBackwards { v ->
            val vh = mCurrentRecyclerView?.findContainingViewHolder(v)
            if (doesNotHaveSticky ||
                    vh?.adapterPosition != childPositionForSticky) {
                val bottom: Int = self.getDecoratedBottom(v)
                if (bottom > lastMaxBottom) {
                    lastMaxBottom = bottom
                    resolved = v
                }
            }
        }
        return resolved
    }


    private fun resetCurrentStickyPosition(currentSticky: View) {
        val self = weakLayoutManager.get() ?: return
        self.layoutDecorated(
                currentSticky,
                calcParentLeft(),
                0,
                calcParentRight(),
                self.getDecoratedMeasuredHeight(currentSticky)
        )
    }

    internal fun setCurrentSticky(newView: View?): Boolean {
        if (newView == null || newView.isStickyView()) {
            mCurrentStickyView = newView
            return true
        }
        return false
    }

    internal fun getCurrentSticky(): View? {
        val toret = mCurrentStickyView
        return if (toret?.isStickyView() == true) {
            mCurrentStickyView
        } else {
            null
        }
    }

    fun View.isStickyView(): Boolean {
        return isStickyViewItemType(this)
    }

    fun updateIdentifiers(identifier: Int) {
        if (mLastStickySection == identifier) {
            return
        }
        mPreviousStickySection = mLastStickySection
        mLastStickySection = identifier

    }

    fun updateCurrentSticky(currentSticky: View) {
        resetCurrentStickyPosition(currentSticky)
        setCurrentSticky(currentSticky)
    }
}

private object VerticalScrollHandler {

    fun scrollVerticallyBy(
            dy: Int,
            recycler: RecyclerView.Recycler,
            state: RecyclerView.State,
            layoutManager: StickDataBindingVerticalLayoutImpl,
            adapter: BaseDataBindingRecyclerAdapter,
            self: RecyclerView.LayoutManager
    ): Int {
        val height = layoutManager.calcParentHeight()
        val left = layoutManager.calcParentLeft()
        val right = layoutManager.calcParentRight()
        val scrolled = if (dy >= 0) {
            onScrollUp(dy, self, state, recycler, layoutManager, height, left, right, adapter)
        } else {
            onScrollDown(dy, self, state, recycler, layoutManager, height, left, right, adapter)
        }
        afterScroll(self, layoutManager.calcParentHeight(), recycler)
        return scrolled
    }


    private fun onScrollUp(
            dy: Int,
            self: RecyclerView.LayoutManager,
            state: RecyclerView.State,
            recycler: RecyclerView.Recycler,
            toDelegateTo: StickDataBindingVerticalLayoutImpl,
            parentHeight: Int,
            parentLeft: Int,
            parentRight: Int,
            mCurrentAdapter: BaseDataBindingRecyclerAdapter
    ): Int {
        var scrolled = 0
        //add children until we have "scrolled down".
        while (scrolled < dy) { //todo limit number of iterations to at max the child count.
            val bottomNormal = toDelegateTo.getLastNormalChild() ?: break
            val hangingBottom = Math.max(self.getDecoratedBottom(bottomNormal) - parentHeight, 0)
            val scrollBy = -Math.min(dy - scrolled, hangingBottom)

            scrolled -= scrollBy

            self.offsetChildrenVertical(scrollBy)

            val nextPosition = (toDelegateTo.getAdapterPositionByView(bottomNormal) ?: 0) + 1
            if (nextPosition < state.itemCount && scrolled < dy) {
                val v = recycler.getViewForPosition(nextPosition)

                self.addView(v)

                self.measureChildWithMargins(v, 0, 0)

                val nextTop = self.getDecoratedBottom(bottomNormal)
                self.layoutDecorated(
                        v,
                        parentLeft,
                        nextTop,
                        parentRight,
                        nextTop + self.getDecoratedMeasuredHeight(v)
                )
            } else {
                break
            }
        }
        var currentSticky: View? = null
        var nextSticky: View? = null
        var maxBottom = Integer.MIN_VALUE
        var minTop = Integer.MAX_VALUE
        self.forEachChild { v ->
            if (toDelegateTo.isStickyViewItemType(v)) {
                val bottom = self.getDecoratedBottom(v)
                val top = self.getDecoratedTop(v)
                if (maxBottom <= bottom && top <= 0) {
                    maxBottom = bottom
                    currentSticky = v
                }
                if (minTop > top && currentSticky !== v) {
                    minTop = top
                    nextSticky = v
                }
            }
        }
        currentSticky?.let {
            toDelegateTo.updateCurrentSticky(it)
//update what is "currentSticky"
            val safeNextSticky = nextSticky
            if (safeNextSticky != null) {
                var diff = 0
                val bottom = self.getDecoratedBottom(it)
                val nextTop = self.getDecoratedTop(safeNextSticky)
                diff = nextTop - bottom
                if (diff < 0) {
                    self.layoutDecorated(
                            it,
                            parentLeft,
                            diff,
                            parentRight,
                            self.getDecoratedMeasuredHeight(it) + diff
                    )
                    toDelegateTo.setCurrentSticky(nextSticky)
                }
            }
            toDelegateTo.getAdapterPositionByView(it)?.let { pos ->
                toDelegateTo.updateIdentifiers(
                        pos
//                        mCurrentAdapter.getIdentifierByPosition(pos)
                )
            }

        }
        return scrolled
    }

    private fun onScrollDown(
            dy: Int,
            self: RecyclerView.LayoutManager,
            state: RecyclerView.State,
            recycler: RecyclerView.Recycler,
            toDelegateTo: StickDataBindingVerticalLayoutImpl,
            parentHeight: Int,
            parentLeft: Int,
            parentRight: Int,
            mCurrentAdapter: BaseDataBindingRecyclerAdapter
    ): Int {
        var scrolled = 0

        while (scrolled > dy) {
            val topNormal = toDelegateTo.getFirstNormalChild() ?: break
            val hangingTop = Math.max(-self.getDecoratedTop(topNormal), 0)
            val scrollBy = Math.min(scrolled - dy, hangingTop)

            scrolled -= scrollBy
            self.offsetChildrenVertical(scrollBy)

            val sticky = toDelegateTo.getCurrentSticky()
            val nextPosition = (toDelegateTo.getAdapterPositionByView(topNormal) ?: 0) - 1
            val isScoped = nextPosition >= 0
            if (isScoped && sticky != null && nextPosition == toDelegateTo.getAdapterPositionByView(sticky)) {
                val nextBottom = self.getDecoratedTop(topNormal)
                self.layoutDecorated(
                        sticky,
                        parentLeft,
                        nextBottom - self.getDecoratedMeasuredHeight(sticky),
                        parentRight,
                        nextBottom
                )
                toDelegateTo.setCurrentSticky(null)
            } else if (isScoped && scrolled > dy) {
                val v = recycler.getViewForPosition(nextPosition)

                self.addView(v, 0)

                self.measureChildWithMargins(v, 0, 0)

                val nextBottom = self.getDecoratedTop(topNormal)
                val nextTop = nextBottom - self.getDecoratedMeasuredHeight(v)
                self.layoutDecorated(
                        v,
                        parentLeft,
                        nextTop,
                        parentRight,
                        nextBottom
                )
            } else {
                break
            }
        }
        var firstNormal: View? = null
        var firstSticky: View? = null
        var minNormalTop = Integer.MAX_VALUE
        var minStickyTop = Integer.MAX_VALUE
        val selfSticky = toDelegateTo.getCurrentSticky()
        self.forEachChild { v ->
            if (selfSticky !== v) {
                val top = self.getDecoratedTop(v)
                if (minNormalTop > top) {
                    minNormalTop = top
                    firstNormal = v
                }
            }
            if (toDelegateTo.isStickyViewItemType(v)) {
                val top = self.getDecoratedTop(v)
                if (minStickyTop > top) {
                    minStickyTop = top
                    firstSticky = v
                }
            }
        }
        val safeFirstNormal = firstNormal ?: return scrolled
        val firstPosition = toDelegateTo.getAdapterPositionByView(safeFirstNormal)
                ?: return scrolled
        if (toDelegateTo.isStickyViewItemType(safeFirstNormal)) {

            var resolved = false
            self.forEachChild { v ->
                val pos = toDelegateTo.getAdapterPositionByView(v)
                if (toDelegateTo.isStickyViewItemType(v) && pos != null && pos == firstPosition) {
                    resolved = true
                    return@forEachChild //break.
                }
            }
            val safeSticky = firstSticky
            if (!resolved && safeSticky != null && self.getDecoratedTop(safeSticky) >= 0) {
                val i = firstPosition
                val v = recycler.getViewForPosition(i)
                self.addView(v, 0)
                self.measureChildWithMargins(v, 0, 0)
                val bottom = self.getDecoratedTop(safeFirstNormal)
                self.layoutDecorated(
                        v,
                        parentLeft,
                        bottom - self.getDecoratedMeasuredHeight(v),
                        parentRight,
                        bottom
                )
            }
        }
        var currentSticky: View? = null
        var previousSticky: View? = null
        self.forEachChildBackwards { v ->
            if (toDelegateTo.isStickyViewItemType(v)) {
                val top = self.getDecoratedTop(v)
                val safePreviousSticky = previousSticky
                if (top >= 0 && (safePreviousSticky == null || self.getDecoratedTop(safePreviousSticky) > top)) {
                    previousSticky = v
                }

            }
        }
        self.forEachChildBackwards { v ->
            if (toDelegateTo.isStickyViewItemType(v)) {
                val safePreviousSticky = previousSticky
                val bottom = self.getDecoratedBottom(v)
                val safeCurrentSticky = currentSticky
                if (previousSticky == null && (safeCurrentSticky == null || self.getDecoratedBottom(safeCurrentSticky) < bottom) || safePreviousSticky != null && self.getDecoratedTop(safePreviousSticky) >= bottom && (safeCurrentSticky == null || self.getDecoratedBottom(safeCurrentSticky) < bottom)) {
                    currentSticky = v
                }
            }
        }


        if (currentSticky == null && previousSticky != null) {
            currentSticky = previousSticky
            previousSticky = null
        }
        currentSticky?.let {
            toDelegateTo.updateCurrentSticky(it)

            previousSticky?.let { previousSticky ->
                val diff = self.getDecoratedTop(previousSticky) - self.getDecoratedBottom(it)
                if (diff < 0) {
                    self.layoutDecorated(
                            it,
                            parentLeft,
                            diff,
                            parentRight,
                            diff + self.getDecoratedMeasuredHeight(it)
                    )
                }
            }

            val pos = toDelegateTo.getAdapterPositionByView(it) ?: return@let
            toDelegateTo.updateIdentifiers(
                    pos
            )
        }
        return scrolled
    }

    private fun afterScroll(self: RecyclerView.LayoutManager, parentHeight: Int, recycler: RecyclerView.Recycler) {
        val toRecycleViews = mutableListOf<View>()
        for (i in 0 until self.childCount) {
            val v = self.getChildAt(i) ?: continue
            val orgTop: Int = self.getDecoratedTop(v)
            val orgBottom: Int = self.getDecoratedBottom(v)
            val isHidedTop: Boolean = orgBottom < 0
            val isHidedBottom: Boolean = orgTop > parentHeight
            if (isHidedTop || isHidedBottom) {
                toRecycleViews.add(v)
            }
        }
        toRecycleViews.forEach { self.removeAndRecycleView(it, recycler) }
    }
}


inline fun RecyclerView.LayoutManager.forEachChild(onEachChild: (View) -> Unit) {
    for (i in 0 until childCount) {
        val child = getChildAt(i) ?: continue
        onEachChild(child)
    }
}

inline fun RecyclerView.LayoutManager.forEachChildBackwards(onEachChild: (View) -> Unit) {
    for (i in childCount - 1 downTo 0) {
        val child = getChildAt(i) ?: continue
        onEachChild(child)
    }
}