package csense.android.widgets.recycler

import android.support.v7.widget.*
import android.view.*
import com.commonsense.android.kotlin.base.extensions.*
import java.util.*

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

            val firstTop = self.paddingTop
            val firstPosition = 0

            val parentLeft = calcParentLeft()
            val parentRight = calcParentRight()
            val parentBottom = calcParentBottom()
            var nextTop = firstTop

            var i = firstPosition
            while (i < state.itemCount && nextTop < parentBottom) {
                val v = recycler.getViewForPosition(i)
                self.addView(v, i)

                self.measureChildWithMargins(v, 0, 0)

                val bottom = nextTop + self.getDecoratedMeasuredHeight(v)

                self.layoutDecoratedWithMargins(v, parentLeft, nextTop, parentRight, bottom)

                nextTop = bottom
                i++
            }
        }

    }

    private fun calcParentLeft(): Int {
        return weakLayoutManager.get()?.paddingLeft ?: 0
    }

    private fun calcParentRight(): Int {
        return (weakLayoutManager.get()?.height ?: 0) - (weakLayoutManager.get()?.paddingRight ?: 0)
    }

    private fun calcParentBottom(): Int {
        return calcParentHeight() - (weakLayoutManager.get()?.paddingBottom ?: 0)
    }

    private fun calcParentHeight(): Int {
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
            view.setChildDrawingOrderCallback(object : RecyclerView.ChildDrawingOrderCallback {

                override fun onGetChildDrawingOrder(childCount: Int, i: Int): Int {
                    val previousStickyChildPosition: Int? = mPreviousStickySection?.let {
                        getChildPositionByAdapterPosition(findStickyPositionByRawIndex(it))
                    }
                    val currentStickyChildPosition: Int? = mLastStickySection?.let {
                        getChildPositionByAdapterPosition(findStickyPositionByRawIndex(it))
                    }

                    val ret: Int

                    if (previousStickyChildPosition != null && i == childCount - 2) {
                        ret = previousStickyChildPosition
                    } else if (currentStickyChildPosition != null && i == childCount - 1) {
                        ret = currentStickyChildPosition
                    } else {
                        val firstSticky = Math.min(
                                previousStickyChildPosition ?: Integer.MAX_VALUE,
                                currentStickyChildPosition ?: Integer.MAX_VALUE
                        )
                        val lastSticky: Int
                        run {
                            lastSticky = if (previousStickyChildPosition != null && currentStickyChildPosition != null) {
                                Math.max(
                                        previousStickyChildPosition,
                                        currentStickyChildPosition
                                )
                            } else {
                                if (previousStickyChildPosition != null && previousStickyChildPosition != firstSticky) {
                                    previousStickyChildPosition
                                } else if (currentStickyChildPosition != null && currentStickyChildPosition != firstSticky) {
                                    currentStickyChildPosition
                                } else {
                                    Integer.MAX_VALUE
                                }
                            }
                        }

                        val increment: Int
                        increment = when {
                            i > lastSticky - 2 -> 2
                            i > firstSticky - 1 -> 1
                            else -> 0
                        }
                        ret = i + increment
                    }

                    return ret
                }

            })
        }
    }

    private fun getChildPositionByAdapterPosition(adapterPosition: Int): Int? {
        val self = weakLayoutManager.get() ?: return null
        for (i in 0 until self.childCount) {
            val v = self.getChildAt(i)
            if (v != null && getAdapterPositionByView(v) == adapterPosition) {
                return i
            }
        }
        return null
    }

    private fun isStickyViewItemType(v: View): Boolean {
        return v is StickLinearLayout
//        return false
    }

    fun findStickyPositionByRawIndex(rawIndex: Int): Int {
        val self = mCurrentAdapter ?: return 0
        if (self.getItemFromRawIndex(rawIndex) != null) {
            return rawIndex
        }
        return RecyclerView.NO_POSITION
    }

    private fun getAdapterPositionByView(v: View): Int? {
        return mCurrentRecyclerView?.getChildAdapterPosition(v)
    }


    private var mPreviousStickySection: Int? = null
    private var mLastStickySection: Int? = null


    fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {

        val self = weakLayoutManager.get() ?: return 0
        val adapter = mCurrentAdapter ?: return 0

        val parentLeft: Int
        val parentRight: Int
        val parentHeight: Int
        run {
            parentLeft = calcParentLeft()
            parentRight = calcParentRight()
            parentHeight = calcParentHeight()
        }

        var scrolled = 0
        if (dy >= 0) {
            var counter = 0
            while (scrolled < dy) {
                val bottomNormal = getLastNormalChild()
                val hangingBottom = Math.max(self.getDecoratedBottom(bottomNormal!!) - parentHeight, 0)
                val scrollBy = -Math.min(dy - scrolled, hangingBottom)

                scrolled -= scrollBy
                if (scrollBy == 0) {
                    counter += 1
                } else {
                    counter = 0
                }
                self.offsetChildrenVertical(scrollBy)
                val nextPosition = getAdapterPositionByView(bottomNormal)!! + 1
                if (counter < adapter.itemCount && nextPosition < state.itemCount && scrolled < dy) {
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
            run {
                var currentSticky: View? = null
                var nextSticky: View? = null
                run {
                    var maxBottom = Integer.MIN_VALUE
                    var minTop = Integer.MAX_VALUE
                    for (i in 0 until self.childCount) {
                        val v = self.getChildAt(i)
                        if (v != null && isStickyViewItemType(v)) {
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
                        resetCurrentStickyPosition(it)
                        setCurrentSticky(it)
                    }
                }
                run {
                    var diff = 0
                    if (currentSticky != null && nextSticky != null) {
                        val bottom = self.getDecoratedBottom(currentSticky!!)
                        val nextTop = self.getDecoratedTop(nextSticky!!)
                        diff = nextTop - bottom
                    }

                    if (currentSticky != null && diff < 0) {
                        self.layoutDecorated(
                                currentSticky!!,
                                parentLeft,
                                0 + diff,
                                parentRight,
                                0 + self.getDecoratedMeasuredHeight(currentSticky!!) + diff
                        )
                        setCurrentSticky(nextSticky)
                    }

                }
            }
        } else {
            while (scrolled > dy) {
                val topNormal = getFirstNormalChild()
                val hangingTop = Math.max(-self.getDecoratedTop(topNormal!!), 0)
                val scrollBy = Math.min(scrolled - dy, hangingTop)

                scrolled -= scrollBy
                self.offsetChildrenVertical(scrollBy)

                val sticky = getCurrentSticky()
                val nextPosition = getAdapterPositionByView(topNormal)!! - 1
                val isScoped = nextPosition >= 0
                if (isScoped && sticky != null && nextPosition == getAdapterPositionByView(sticky)) {
                    val nextBottom = self.getDecoratedTop(topNormal!!)
                    self.layoutDecorated(
                            sticky!!,
                            parentLeft,
                            nextBottom - self.getDecoratedMeasuredHeight(sticky!!),
                            parentRight,
                            nextBottom
                    )
                    setCurrentSticky(null)
                } else if (isScoped && scrolled > dy) {
                    val v = recycler.getViewForPosition(nextPosition)

                    self.addView(v, 0)

                    self.measureChildWithMargins(v, 0, 0)

                    val nextBottom = self.getDecoratedTop(topNormal!!)
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
            run {
                var firstNormal: View? = null
                var firstSticky: View? = null
                run {
                    var minNormalTop = Integer.MAX_VALUE
                    for (i in 0 until self.childCount) {
                        val v = self.getChildAt(i)
                        if (getCurrentSticky() == null || getCurrentSticky() !== v) {
                            val top = self.getDecoratedTop(v!!)
                            if (minNormalTop > top) {
                                minNormalTop = top
                                firstNormal = v
                            }
                        }
                    }
                    var minStickyTop = Integer.MAX_VALUE
                    for (i in 0 until self.childCount) {
                        val v = self.getChildAt(i)
                        if (v != null && isStickyViewItemType(v)) {
                            val top = self.getDecoratedTop(v)
                            if (minStickyTop > top) {
                                minStickyTop = top
                                firstSticky = v
                            }
                        }
                    }
                }
                run {
                    val safeFirstNormal = firstNormal
                    if (safeFirstNormal != null) {
                        val firstPosition = getAdapterPositionByView(safeFirstNormal)
                        if (firstPosition != null && isStickyViewItemType(safeFirstNormal)) {
                            val requiredIdentifier = findStickyPositionByRawIndex(firstPosition)

                            var resolved = false
                            for (i in 0 until self.childCount) {
                                val v = self.getChildAt(i)
                                if (v != null &&
                                        isStickyViewItemType(v) &&
                                        findStickyPositionByRawIndex(getAdapterPositionByView(v)!!) == requiredIdentifier) {
                                    resolved = true
                                    break
                                }
                            }
                            if (!resolved && self.getDecoratedTop(firstSticky!!) >= 0) {
                                val i = findStickyPositionByRawIndex(requiredIdentifier)
                                val v = recycler.getViewForPosition(i)
                                self.addView(v, 0)
                                self.measureChildWithMargins(v, 0, 0)
                                val bottom = self.getDecoratedTop(firstNormal!!)
                                self.layoutDecorated(
                                        v,
                                        parentLeft,
                                        bottom - self.getDecoratedMeasuredHeight(v),
                                        parentRight,
                                        bottom
                                )
                            }
                        }
                    }
                }
            }
            run {
                var currentSticky: View? = null
                var previousSticky: View? = null
                run {
                    for (pass in 1..2) {
                        for (i in self.childCount - 1 downTo 0) {
                            val v = self.getChildAt(i)
                            if (v != null && isStickyViewItemType(v)) {
                                val top = self.getDecoratedTop(v!!)
                                val bottom = self.getDecoratedBottom(v!!)
                                if (pass == 1) {
                                    if (top >= 0 && (previousSticky == null || self.getDecoratedTop(previousSticky!!) > top)) {
                                        previousSticky = v
                                    }
                                } else if (pass == 2) {
                                    if (previousSticky == null && (currentSticky == null || self.getDecoratedBottom(currentSticky!!) < bottom) || previousSticky != null && self.getDecoratedTop(previousSticky!!) >= bottom && (currentSticky == null || self.getDecoratedBottom(currentSticky!!) < bottom)) {
                                        currentSticky = v
                                    }
                                }
                            }
                        }
                    }
                    if (currentSticky == null && previousSticky != null) {
                        currentSticky = previousSticky
                        previousSticky = null
                    }
                }
                run {
                    currentSticky?.let {
                        resetCurrentStickyPosition(it)
                        setCurrentSticky(it)
                    }
                    var diff = 0
                    if (currentSticky != null && previousSticky != null) {
                        diff = self.getDecoratedTop(previousSticky!!) - self.getDecoratedBottom(currentSticky!!)
                    }
                    if (diff < 0) {
                        val top = 0 + diff
                        self.layoutDecorated(
                                currentSticky!!,
                                parentLeft,
                                top,
                                parentRight,
                                top + self.getDecoratedMeasuredHeight(currentSticky!!)
                        )
                    }

                }
            }
        }
        run {
            val toRecycleViews = LinkedList<View>()
            for (i in 0 until self.childCount) {
                val v = self.getChildAt(i)
                val orgTop: Int
                val orgBottom: Int
                run {
                    orgTop = self.getDecoratedTop(v!!)
                    orgBottom = self.getDecoratedBottom(v!!)
                }
                val isHidedTop: Boolean
                val isHidedBottom: Boolean
                run {
                    isHidedTop = orgBottom < 0
                    isHidedBottom = orgTop > parentHeight
                }
                if ((isHidedTop || isHidedBottom) && v != null) {
                    toRecycleViews.add(v)
                }
            }
            for (v in toRecycleViews) {
                self.removeAndRecycleView(v, recycler)
            }
        }

        return scrolled
    }

    private fun getFirstNormalChild(): View? {
        val self = weakLayoutManager.get() ?: return null

        var resolved: View? = null
        var lastMinTop = Integer.MAX_VALUE
        for (i in 0 until self.childCount) {
            val v = self.getChildAt(i)
            if (getCurrentSticky() == null || v !== getCurrentSticky()) {
                val top: Int = self.getDecoratedTop(v!!)
                if (top < lastMinTop) {
                    lastMinTop = top
                    resolved = v
                }
            }
        }
        return resolved
    }

    private fun getLastNormalChild(): View? {
        val self = weakLayoutManager.get() ?: return null
        var resolved: View? = null
        var lastMaxBottom = Integer.MIN_VALUE
        for (i in self.childCount - 1 downTo 0) {
            val v = self.getChildAt(i)
            if ((v != null && !isStickyViewItemType(v)) || (v != null && getCurrentSticky() == null)) {
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
                0 + self.getDecoratedMeasuredHeight(currentSticky)
        )
    }

    private fun setCurrentSticky(newView: View?): Boolean {
        if (newView == null || isStickyViewItemType(newView)) {
            mCurrentStickyView = newView
            return true
        }
        return false
    }

    private fun getCurrentSticky(): View? {
        return mCurrentStickyView?.let {
            return if (isStickyViewItemType(it)) {
                it
            } else {
                null
            }
        }
    }

}