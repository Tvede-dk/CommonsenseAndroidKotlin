package com.commonsense.android.kotlin.views.baseClasses

import android.support.v4.view.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.system.logging.*

/**
 *
 * @property onNewPageSelected Function1<Int, Unit> This method will be invoked when a new page becomes selected. Animation is not necessarily complete.
 * @constructor
 */
class BaseViewPagerOnChangeListener(val onNewPageSelected: FunctionUnit<Int>) : ViewPager.OnPageChangeListener {


    /**
     * This method will be invoked when a new page becomes selected. Animation is not necessarily complete.
     * @param index Int
     */
    override fun onPageSelected(index: Int) {
        onNewPageSelected(index)
    }

    /**

     * @param state Int
     * either
     * SCROLL_STATE_IDLE
     * SCROLL_STATE_DRAGGING
     * SCROLL_STATE_SETTLING
     */
    override fun onPageScrollStateChanged(state: Int) {
        logClassWarning("test2 : $state")
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {


        logClassWarning("test: $position, $positionOffset, $positionOffsetPixels")
    }

}